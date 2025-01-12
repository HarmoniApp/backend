package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.absence.AbsenceType;
import org.harmoniapp.entities.absence.Status;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.enums.AbsenceNotificationType;
import org.harmoniapp.enums.AbsenceStatus;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.exception.InvalidAbsenceStatusException;
import org.harmoniapp.exception.InvalidDateException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.services.notification.NotificationService;
import org.harmoniapp.utils.HolidayCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service class for managing absences.
 * Provides methods to retrieve absences information.
 */
@Service
@RequiredArgsConstructor
public class AbsenceServiceImpl implements AbsenceService {
    private final RepositoryCollector repositoryCollector;
    private final NotificationService notificationService;
    private final AbsenceDaysUpdater absenceDaysUpdater;
    private final ShiftUpdater shiftUpdater;

    /**
     * Retrieves a paginated list of absences for a specific user.
     *
     * @param id         the user ID
     * @param pageNumber the page number to retrieve
     * @param pageSize   the size of the page to retrieve
     * @return a PageDto containing AbsenceDto objects
     */
    @Override
    public PageDto<AbsenceDto> getByUserId(long id, int pageNumber, int pageSize) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<Absence> userAbsences = repositoryCollector.getAbsences().findAwaitingOrApprovedAbsenceByUserId(id, pageable);
        return PageDto.mapPage(userAbsences, AbsenceDto::fromEntity);
    }

    /**
     * Retrieves a paginated list of absences with a specific status.
     *
     * @param statusId   the status ID
     * @param pageNumber the page number to retrieve
     * @param pageSize   the size of the page to retrieve
     * @return a PageDto containing AbsenceDto objects
     */
    @Override
    public PageDto<AbsenceDto> getByStatus(long statusId, int pageNumber, int pageSize) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<Absence> absencesWithStatus = repositoryCollector.getAbsences().findAbsenceByStatusId(statusId, pageable);
        return PageDto.mapPage(absencesWithStatus, AbsenceDto::fromEntity);
    }

    /**
     * Retrieves a paginated list of all absences with active users.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the size of the page to retrieve
     * @return a PageDto containing AbsenceDto objects
     */
    @Override
    public PageDto<AbsenceDto> getAll(int pageNumber, int pageSize) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<Absence> absence = repositoryCollector.getAbsences().findAllWithActiveUsers(pageable);
        return PageDto.mapPage(absence, AbsenceDto::fromEntity);
    }

    /**
     * Creates a new absence.
     *
     * @param absenceDto the AbsenceDto object containing absence details
     * @return the created AbsenceDto object
     */
    @Override
    @Transactional
    public AbsenceDto create(AbsenceDto absenceDto) {
        validateAbsenceDays(absenceDto);
        User user = getUserById(absenceDto.userId());
        Absence absence = buildAbsence(absenceDto, user, AbsenceStatus.AWAITING);
        absence.setSubmission(LocalDate.now());
        if (isVacation(absence)) {
            absenceDaysUpdater.updateUserAbsenceDays(user, absence.getWorkingDays().intValue());
        }
        Absence savedAbsence = repositoryCollector.getAbsences().save(absence);
        sendNotification(savedAbsence, AbsenceNotificationType.NEW_ABSENCE);
        return AbsenceDto.fromEntity(savedAbsence);
    }

    /**
     * Updates the status of an absence.
     *
     * @param id       the absence ID
     * @param statusId the new status ID
     * @return the updated AbsenceDto object
     * @throws EntityNotFoundException                if the absence is not found
     * @throws InvalidDateException          if the absence start date is in the past
     * @throws InvalidAbsenceStatusException if the absence status is already finalized
     */
    @Override
    @Transactional
    public AbsenceDto updateStatus(long id, long statusId) {
        Absence existingAbsence = getAbsenceById(id);
        validateUpdateStatusData(existingAbsence, statusId);
        Absence updatedAbsence = updateStatus(existingAbsence, statusId);
        long updatedStatus = updatedAbsence.getStatus().getId();
        if (updatedStatus == AbsenceStatus.APPROVED.getId()) {
            shiftUpdater.removeOverlappedShifts(updatedAbsence);
        } else if (updatedStatus == AbsenceStatus.REJECTED.getId() && isVacation(updatedAbsence)) {
            absenceDaysUpdater.updateUserAbsenceDays(updatedAbsence);
        }

        sendNotification(updatedAbsence, AbsenceNotificationType.EMPLOYER_UPDATED);
        return AbsenceDto.fromEntity(updatedAbsence);
    }

    /**
     * Deletes an absence.
     *
     * @param id       the absence ID
     * @param statusId the status ID
     */
    @Override
    @Transactional
    public void deleteAbsence(long id, long statusId) {
        Absence existingAbsence = getAbsenceById(id);
        validateDeleteAbsence(existingAbsence, statusId);
        if (isVacation(existingAbsence)) {
            absenceDaysUpdater.updateUserAbsenceDays(existingAbsence);
        }
        sendNotification(existingAbsence, AbsenceNotificationType.EMPLOYEE_DELETED);
        repositoryCollector.getAbsences().delete(existingAbsence);
    }

    /**
     * Validates if an absence can be deleted based on its status.
     *
     * @param absence  the Absence entity to be validated
     * @param statusId the status ID to be checked
     * @throws InvalidAbsenceStatusException if the status is not CANCELLED or the absence status is not AWAITING
     */
    private void validateDeleteAbsence(Absence absence, long statusId) {
        if (statusId != AbsenceStatus.CANCELLED.getId()) {
            throw new InvalidAbsenceStatusException("Niprawidłowy status");
        }
        if (!absence.getStatus().getId().equals(AbsenceStatus.AWAITING.getId())) {
            throw new InvalidAbsenceStatusException("Nie można usunąć wniosku o urlop");
        }
    }

    private boolean isVacation(Absence absence) {
        return absence.getAbsenceType().getName().equalsIgnoreCase("Urlop wypoczynkowy");
    }

    /**
     * Builds an Absence entity from the given AbsenceDto, User, and AbsenceStatusEnum.
     *
     * @param absenceDto the AbsenceDto object containing absence details
     * @param user       the User entity associated with the absence
     * @param statusEnum the AbsenceStatusEnum representing the status of the absence
     * @return the built Absence entity
     */
    private Absence buildAbsence(AbsenceDto absenceDto, User user, AbsenceStatus statusEnum) {
        AbsenceType absenceType = getAbsenceTypeById(absenceDto.absenceTypeId());
        Status status = getStatusById(statusEnum.getId());

        Absence absence = absenceDto.toEntity(user, absenceType, status);
        absence.setUpdated(LocalDate.now());
        absence.setWorkingDays(HolidayCalculator.calculateWorkingDays(absence.getStart(), absence.getEnd()));
        return absence;
    }

    /**
     * Validates the absence days in an AbsenceDto object.
     *
     * @param absenceDto the AbsenceDto object
     * @throws InvalidDateException if the dates are invalid
     */
    private void validateAbsenceDays(AbsenceDto absenceDto) {
        if (absenceDto.start().isBefore(LocalDate.now()) || absenceDto.end().isBefore(LocalDate.now())) {
            throw new InvalidDateException("Nie można rozpocząć ani zakończyć urlopu w przeszłości.");
        }
        if (absenceDto.end().isBefore(absenceDto.start())) {
            throw new InvalidDateException("Nie można zakończyć urlopu przed jego rozpoczęciem");
        }
    }

    /**
     * Retrieves a User entity by ID.
     *
     * @param userId the user ID
     * @return the User entity
     * @throws EntityNotFoundException if the user is not found
     */
    private User getUserById(long userId) {
        return repositoryCollector.getUsers().findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika"));
    }

    /**
     * Retrieves an AbsenceType entity by ID.
     *
     * @param typeId the type ID
     * @return the AbsenceType entity
     * @throws EntityNotFoundException if the absence type is not found
     */
    private AbsenceType getAbsenceTypeById(long typeId) {
        return repositoryCollector.getAbsenceTypes().findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono typu urlopu"));
    }

    /**
     * Retrieves a Status entity by ID.
     *
     * @param statusId the status ID
     * @return the Status entity
     * @throws EntityNotFoundException if the status is not found
     */
    private Status getStatusById(long statusId) {
        return repositoryCollector.getStatuses().findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono statusu"));
    }

    /**
     * Validates the data required to update the status of an absence.
     *
     * @param absence        the Absence entity to be updated
     * @param updateStatusId the new status ID to be set
     * @throws InvalidAbsenceStatusException if the new status ID is invalid or the current status is CANCELLED or REJECTED
     * @throws InvalidDateException          if the absence start date is in the past
     */
    private void validateUpdateStatusData(Absence absence, long updateStatusId) {
        if (updateStatusId == AbsenceStatus.AWAITING.getId()) {
            throw new InvalidAbsenceStatusException("Nieprawidłowy status");
        }
        if (absence.getStart().isBefore(LocalDate.now())) {
            throw new InvalidDateException("Nie można zmienić statusu wniosku o urlop, który rozpoczą się w przeszłości");
        }
        if (absence.getStatus().getId().equals(AbsenceStatus.CANCELLED.getId())
                || absence.getStatus().getId().equals(AbsenceStatus.REJECTED.getId())) {
            throw new InvalidAbsenceStatusException("Nie można zaktualizować statusu wniosku o urlop");
        }
    }

    /**
     * Creates a Pageable object for pagination.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the size of the page to retrieve
     * @return a Pageable object
     */
    private Pageable createPageable(int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber - 1;
        pageSize = (pageSize < 1) ? 10 : pageSize;
        return PageRequest.of(pageNumber, pageSize, Sort.by("updated").descending());
    }

    /**
     * Updates the status of an Absence entity.
     *
     * @param absence  the Absence entity
     * @param statusId the new status ID
     * @return the updated Absence entity
     */
    private Absence updateStatus(Absence absence, long statusId) {
        Status status = getStatusById(statusId);
        absence.setStatus(status);
        absence.setUpdated(LocalDate.now());
        return repositoryCollector.getAbsences().save(absence);
    }

    /**
     * Sends a notification for the given absence.
     *
     * @param savedAbsence the Absence entity
     * @param type         the type of notification
     */
    private void sendNotification(Absence savedAbsence, AbsenceNotificationType type) {
        NotificationDto notificationDto = AbsenceNotification.createNotification(savedAbsence, type);
        if (notificationDto != null) notificationService.create(notificationDto);
    }

    /**
     * Retrieves an Absence entity by ID.
     *
     * @param id the absence ID
     * @return the Absence entity
     * @throws EntityNotFoundException if the absence is not found
     */
    private Absence getAbsenceById(long id) {
        return repositoryCollector.getAbsences().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono wniosku o urlop"));
    }
}
