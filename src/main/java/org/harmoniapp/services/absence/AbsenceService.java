package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.absence.AbsenceType;
import org.harmoniapp.entities.absence.Status;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing absences.
 * Provides methods to retrieve absences information.
 */
@Service
@RequiredArgsConstructor
public class AbsenceService {
    private final RepositoryCollector repositoryCollector;
    private final NotificationService notificationService;

    /**
     * Retrieves a paginated list of absences for a specific user.
     *
     * @param id         the ID of the user whose absences are to be retrieved
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of items per page
     * @return a PageDto containing the user's absences
     */
    public PageDto<AbsenceDto> getAbsenceByUserId(long id, int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber-1;
        pageSize = (pageSize < 1) ? 10 : pageSize;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updated").descending());
        Page<Absence> userAbsences = repositoryCollector.getAbsences().findAwaitingOrApprovedAbsenceByUserId(id, pageable);

        return new PageDto<>(userAbsences.stream().map(AbsenceDto::fromEntity).toList(),
                userAbsences.getSize(),
                userAbsences.getNumber()+1,
                userAbsences.getTotalPages());
    }

//    /**
//     * Retrieves a paginated list of absences for a specific user based on their archived status.
//     *
//     * @param id         the ID of the user whose absences are to be retrieved.
//     * @param archived   a boolean indicating whether to retrieve archived or non-archived absences.
//     * @param pageNumber the page number to retrieve.
//     * @param pageSize   the number of items per page.
//     * @return a PageDto containing the user's absences with the specified archived status.
//     */
//    public PageDto<AbsenceDto> getAbsenceByUserIdAndArchive(long id, boolean archived, int pageNumber, int pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updated").descending());
//        Page<Absence> userAbsences = repositoryCollector.getAbsences().findByUserIdAndArchived(id, archived, pageable);
//
//        return new PageDto<>(userAbsences.stream().map(AbsenceDto::fromEntity).toList(),
//                userAbsences.getSize(),
//                userAbsences.getNumber(),
//                userAbsences.getTotalPages());
//    }

    /**
     * Retrieves a paginated list of absences filtered by status ID.
     *
     * @param statusId   the ID of the status to filter absences by
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of items per page
     * @return a PageDto containing the absences with the specified status
     */
    public PageDto<AbsenceDto> getAbsenceByStatus(long statusId, int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber-1;
        pageSize = (pageSize < 1) ? 10 : pageSize;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updated").descending());

        Page<Absence> absencesWithStatus = repositoryCollector.getAbsences().findAbsenceByStatusId(statusId, pageable);
        return new PageDto<>(absencesWithStatus.stream().map(AbsenceDto::fromEntity).toList(),
                absencesWithStatus.getSize(),
                absencesWithStatus.getNumber()+1,
                absencesWithStatus.getTotalPages());

    }

    /**
     * Retrieves a list of approved absences for a specified user within a given date range.
     *
     * @param userId    the ID of the user to filter absences for
     * @param startDate the start date of the range to filter absences
     * @param endDate   the end date of the range to filter absences
     * @return a list of AbsenceDto representing the approved absences within the specified date range for the given user
     */
    public List<AbsenceDto> getAbsenceByDateRangeAndUserId(long userId, LocalDate startDate, LocalDate endDate) {
        List<Absence> absenceByDateRangeAndUserId = repositoryCollector.getAbsences().findAbsenceByDateRangeAndUserId(startDate, endDate, userId);

        return absenceByDateRangeAndUserId.stream()
                .filter(a -> a.getStatus().getId() == 2)
                .map(AbsenceDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of approved absences for a specified user within a given date range.
     *
     * @param userId    the ID of the user to filter absences for
     * @param startDate the start date of the range to filter absences
     * @param endDate   the end date of the range to filter absences
     * @return a list of AbsenceDto representing the approved absences within the specified date range for the given user
     * @throws RuntimeException if an error occurs during the retrieval process
     */
    public List<AbsenceDto> getApprovedAbsenceByDateRangeAndUserId(long userId, LocalDate startDate, LocalDate endDate) {
        List<Absence> userAbsences = repositoryCollector.getAbsences().findAbsenceByDateRangeAndUserId(startDate, endDate, userId);

        return userAbsences.stream()
                .filter(a -> a.getStatus().getId() == 2)
                .map(AbsenceDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated list of all absences.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of items per page
     * @return a PageDto containing the details of all absences
     * @throws RuntimeException if an error occurs while retrieving absences
     */
    public PageDto<AbsenceDto> getAllAbsences(int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber-1;
        pageSize = (pageSize < 1) ? 10 : pageSize;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updated").descending());

        var absence = repositoryCollector.getAbsences().findAllWithActiveUsers(pageable);
        return new PageDto<>(absence.stream().map(AbsenceDto::fromEntity).toList(),
                absence.getSize(),
                absence.getNumber()+1,
                absence.getTotalPages());

    }

    /**
     * Creates a new Absence and sends a notification to the supervisor.
     *
     * @param absenceDto the AbsenceDto containing the details of the absence to create
     * @return the created AbsenceDto
     * @throws IllegalArgumentException if the user or absence type ID provided does not exist
     * @throws RuntimeException         if an error occurs during creation
     */
    public AbsenceDto createAbsence(AbsenceDto absenceDto) { //TODO: change subbmision and updated to date anh hour

        User user = repositoryCollector.getUsers()
                .findById(absenceDto.userId())
                .orElseThrow(IllegalArgumentException::new);

        AbsenceType absenceType = repositoryCollector.getAbsenceTypes()
                .findById(absenceDto.absenceTypeId())
                .orElseThrow(IllegalArgumentException::new);

        Status status = repositoryCollector.getStatuses()
                .findById(1L)    //Here status MUST be awaiting
                .orElseThrow(IllegalArgumentException::new);

        if (absenceDto.start().isBefore(LocalDate.now()) || absenceDto.end().isBefore(LocalDate.now())) {
            throw new RuntimeException("Błąd: Nie można rozpocząć ani zakończyć urlopu w przeszłości.");
        }
        if (absenceDto.end().isBefore(absenceDto.start())) {
            throw new RuntimeException("Błąd: Nie można zakończyć urlopu przed jego rozpoczęciem");
        }

        Absence absence = absenceDto.toEntity(user, absenceType, status);
        absence.setSubmission(LocalDate.now());
        absence.setUpdated(LocalDate.now());
        absence.setWorkingDays(HolidayCalculator.calculateWorkingDays(absence.getStart(), absence.getEnd()));

        int requestedDays = absence.getWorkingDays().intValue();
        int availableDays = user.getAvailableAbsenceDays() + user.getUnusedAbsenceDays();

        if (requestedDays > availableDays) {
            throw new RuntimeException("Błąd: Nie można wziąć więcej dni niż jest dostępnych");
        }

        if (requestedDays <= user.getUnusedAbsenceDays()) {
            user.setUnusedAbsenceDays(user.getUnusedAbsenceDays() - requestedDays);
        } else {
            requestedDays -= user.getUnusedAbsenceDays();
            user.setUnusedAbsenceDays(0);
            user.setAvailableAbsenceDays(user.getAvailableAbsenceDays() - requestedDays);
        }

        repositoryCollector.getUsers().save(user);
        Absence savedAbsence = repositoryCollector.getAbsences().save(absence);

        newAbsenceCreatedNotification(savedAbsence);

        return AbsenceDto.fromEntity(savedAbsence);
    }

    //TODO: check if we are using this anywhere?
    /**
     * Updates an existing Absence or creates a new one if it doesn't exist and sends a notification.
     * IMPORTANT it is updated by employee
     *
     * @param id         the ID of the absence to update
     * @param absenceDto the AbsenceDto containing the updated details of the absence
     * @return the updated or newly created AbsenceDto
     * @throws IllegalArgumentException if the user or absence type ID provided does not exist
     * @throws RuntimeException         if an error occurs during the update process
     */
    @Transactional
    public AbsenceDto updateAbsence(long id, AbsenceDto absenceDto) {
        try {
            if(absenceDto.status().getId() != 1L ){ // Employee can only change absence if the status is awaiting
                throw new RuntimeException("An error occurred: You are not allowed to update the status of the absence");
            }

            Absence existingAbsence = repositoryCollector.getAbsences().findById(id)
                    .orElse(null);

            User user = repositoryCollector.getUsers().findById(absenceDto.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            AbsenceType absenceType = repositoryCollector.getAbsenceTypes().findById(absenceDto.absenceTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("AbsenceType not found"));

            Status status = repositoryCollector.getStatuses()
                    .findById(absenceDto.status().getId())
                    .orElseThrow(IllegalArgumentException::new);

            if (absenceDto.start().isBefore(LocalDate.now()) || absenceDto.end().isBefore(LocalDate.now())) {
                throw new RuntimeException("An error occurred: You can't start or end the absence in the past.");
            }
            if (absenceDto.end().isBefore(absenceDto.start())) {
                throw new RuntimeException("An error occurred: You can't end the absence before it starts.");
            }

            if (existingAbsence == null) {
                Absence newAbsence = absenceDto.toEntity(user, absenceType, status);
                newAbsence.setWorkingDays(HolidayCalculator.calculateWorkingDays(newAbsence.getStart(), newAbsence.getEnd()));
                Absence savedAbsence = repositoryCollector.getAbsences().save(newAbsence);

                employeeUpdatedAbsenceNotification(savedAbsence);

                return AbsenceDto.fromEntity(savedAbsence);
            } else {
                existingAbsence.setStart(absenceDto.start());
                existingAbsence.setEnd(absenceDto.end());
                existingAbsence.setUser(user);
                existingAbsence.setAbsenceType(absenceType);
                existingAbsence.setStatus(status);
                existingAbsence.setSubmission(absenceDto.submission());
                existingAbsence.setUpdated(absenceDto.updated());
                existingAbsence.setWorkingDays(HolidayCalculator.calculateWorkingDays(existingAbsence.getStart(), existingAbsence.getEnd()));
                Absence updatedAbsence = repositoryCollector.getAbsences().save(existingAbsence);

                employeeUpdatedAbsenceNotification(updatedAbsence);

                return AbsenceDto.fromEntity(updatedAbsence);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the status of an existing Absence and sends a notification to the employee.
     * IMPORTANT it is updated by employer
     *
     * @param id       the ID of the absence to update
     * @param statusId the ID of the new status to set for the absence
     * @return the updated AbsenceDto
     * @throws IllegalArgumentException if the provided status ID does not exist
     * @throws RuntimeException         if the absence does not exist or if an error occurs during the update process
     */
    @Transactional
    public AbsenceDto updateAbsenceStatus(long id, long statusId) {
        //TODO: we can only change status if the absence is in future
        try {
            Absence existingAbsence = repositoryCollector.getAbsences().findById(id)
                    .orElseThrow(() -> new RuntimeException("You can only change status if absence exists"));

            Status status = repositoryCollector.getStatuses()
                    .findById(statusId)
                    .orElseThrow(IllegalArgumentException::new);

            existingAbsence.setStatus(status);
            existingAbsence.setUpdated(LocalDate.now());

            if (status.getId() == 2) { // if the absence is approved, delete all shifts for the user that overlap with the absence period
                LocalDateTime startDateTime = existingAbsence.getStart().atStartOfDay();
                LocalDateTime endDateTime = existingAbsence.getEnd().atTime(LocalTime.MAX);

                List<Shift> overlappingShifts = repositoryCollector.getShifts()
                        .findAllByDateRangeAndUserId(startDateTime, endDateTime, existingAbsence.getUser().getId());

                repositoryCollector.getShifts().deleteAll(overlappingShifts);
            } else if (statusId == 4) {
                User user = existingAbsence.getUser();
                user.setAvailableAbsenceDays(user.getAvailableAbsenceDays() + existingAbsence.getWorkingDays().intValue());
                repositoryCollector.getUsers().save(user);
            }

            Absence updatedAbsence = repositoryCollector.getAbsences().save(existingAbsence);

            employerChangeAbsenceStatusNotification(updatedAbsence);

            return AbsenceDto.fromEntity(updatedAbsence);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update absence status: " + e.getMessage(), e);
        }
    }

    //TODO: check if we need this?
    /**
     * Updates the archived status of an existing absence.
     *
     * @param id       the ID of the absence to update
     * @param archived a boolean indicating the new archived status to set
     * @return the updated AbsenceDto object representing the absence with the modified archived status
     * @throws RuntimeException if the absence does not exist or if an error occurs during the update process
     */
    @Transactional
    public AbsenceDto updateAbsenceArchived(long id, boolean archived) {
        try {
            Absence existingAbsence = repositoryCollector.getAbsences().findById(id)
                    .orElseThrow(() -> new RuntimeException("You can only change archived if absence exists"));

            Absence updatedAbsence = repositoryCollector.getAbsences().save(existingAbsence);
            return AbsenceDto.fromEntity(updatedAbsence);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update absence status: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to the supervisor when a new absence is created.
     *
     * @param savedAbsence the saved absence
     */
    private void newAbsenceCreatedNotification(Absence savedAbsence) {
        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                savedAbsence.getUser().getSupervisor().getId(),
                "Nowy wniosek o urlop",
                "Nowy wniosek o urlop. Pracownik " + savedAbsence.getUser().getFirstname() + " " + savedAbsence.getUser().getSurname() + " złożył wniosek o urlop. Zapoznaj się ze zmianami",
                false,
                LocalDateTime.now()
        );

        notificationService.create(notificationDto);
    }

    /**
     * Sends a notification to the supervisor when an employee updates an absence.
     *
     * @param savedAbsence the saved absence
     */
    private void employeeUpdatedAbsenceNotification(Absence savedAbsence) {
        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                savedAbsence.getUser().getSupervisor().getId(),
                "Urlop zaaktualizowany",
                "Urlop zaaktualizowany. Pracownik " + savedAbsence.getUser().getFirstname() + " " + savedAbsence.getUser().getSurname() + " zmienił swoją nieobecność. Zapoznaj się ze zmianami.",
                false,
                LocalDateTime.now()
        );

        notificationService.create(notificationDto);
    }

    /**
     * Sends a notification to the employee when the employer changes the absence status.
     *
     * @param savedAbsence the saved absence
     */
    private void employerChangeAbsenceStatusNotification(Absence savedAbsence) {
        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                savedAbsence.getUser().getId(),
                "Status urlopu zaaktualizowany",
                "Status urlopu zaaktualizowany. Status urlopu " +
                        savedAbsence.getStart() + "-" + savedAbsence.getEnd() +
                        " to " + savedAbsence.getStatus().getName() +
                        " Zapoznaj się ze zmianami.",
                false,
                LocalDateTime.now()
        );

        notificationService.create(notificationDto);
    }

    /**
     * Deletes an absence by its ID.
     *
     * @param id the ID of the Absence to be deleted
     * @throws RuntimeException if an error occurs during deletion
     */
    public void deleteAbsence(long id, long statusId) {
        try {
            Absence existingAbsence = repositoryCollector.getAbsences().findById(id)
                    .orElseThrow(() -> new RuntimeException("Absence not found"));

            User user = repositoryCollector.getUsers().findById(existingAbsence.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setAvailableAbsenceDays(user.getAvailableAbsenceDays() + existingAbsence.getWorkingDays().intValue());
            repositoryCollector.getUsers().save(user);

            deleteAbsenceNotification(id, statusId);
            repositoryCollector.getAbsences().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to the appropriate user when an absence is deleted.
     *
     * @param absenceId the ID of the deleted absence
     * @param statusId the ID of the status to determine who gets the notification
     * @throws RuntimeException if the absence or notification type is not found
     */
    private void deleteAbsenceNotification(long absenceId, long statusId) {
        Absence absence = repositoryCollector.getAbsences().findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence not found"));

        String message;
        User user;
        if(statusId == 3){ // employer gets notification
            user = absence.getUser().getSupervisor();
            message = "Pracownik " + absence.getUser().getFirstname() + " "
                    + absence.getUser().getSurname() + "anulował urlop";
        } else if (statusId == 4) { // employee gets notification
            user = absence.getUser();
            message = "Urlop " + absence.getStart() + " - " + absence.getEnd() + " odrzucony";
        } else {
            throw new RuntimeException("Invalid status for notification");
        }

        NotificationDto notificationDto = new NotificationDto(
                                0L, // id is set automatically by the database
                                user.getId(),
                                "Urlop zaaktualizowany",
                                message,
                                false,
                                LocalDateTime.now()
                        );

        notificationService.create(notificationDto);
    }
}
