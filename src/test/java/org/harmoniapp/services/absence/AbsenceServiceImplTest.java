package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.absence.StatusDto;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.absence.AbsenceType;
import org.harmoniapp.entities.absence.Status;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.enums.AbsenceNotificationType;
import org.harmoniapp.enums.AbsenceStatus;
import org.harmoniapp.exception.InvalidAbsenceStatusException;
import org.harmoniapp.exception.InvalidDateException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.absence.AbsenceRepository;
import org.harmoniapp.repositories.absence.AbsenceTypeRepository;
import org.harmoniapp.repositories.absence.StatusRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.services.notification.NotificationService;
import org.harmoniapp.utils.HolidayCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbsenceServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private AbsenceTypeRepository absenceTypeRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AbsenceDaysUpdater absenceDaysUpdater;

    @Mock
    private ShiftUpdater shiftUpdater;

    @InjectMocks
    private AbsenceServiceImpl absenceService;

    @Test
    public void getByUserIdTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("updated").descending());
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(4);
        User user = User.builder().id(1L).build();
        AbsenceType absenceType = new AbsenceType(1L, "Sick leave");
        Absence absence = Absence.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceType(absenceType)
                .user(user)
                .status(new Status(1L, "Awaiting"))
                .build();
        Page<Absence> absences = new PageImpl<>(List.of(absence));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findAwaitingOrApprovedAbsenceByUserId(1L, pageable)).thenReturn(absences);

        PageDto<AbsenceDto> result = absenceService.getByUserId(1L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void getByStatusTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("updated").descending());
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(4);
        User user = User.builder().id(1L).build();
        AbsenceType absenceType = new AbsenceType(1L, "Sick leave");
        Absence absence = Absence.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceType(absenceType)
                .user(user)
                .status(new Status(1L, "Awaiting"))
                .build();
        Page<Absence> absences = new PageImpl<>(List.of(absence));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findAbsenceByStatusId(1L, pageable)).thenReturn(absences);

        PageDto<AbsenceDto> result = absenceService.getByStatus(1L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void getAllTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("updated").descending());
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(4);
        User user = User.builder().id(1L).build();
        AbsenceType absenceType = new AbsenceType(1L, "Sick leave");
        Absence absence = Absence.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceType(absenceType)
                .user(user)
                .status(new Status(1L, "Awaiting"))
                .build();
        Page<Absence> absences = new PageImpl<>(List.of(absence));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findAllWithActiveUsers(pageable)).thenReturn(absences);

        PageDto<AbsenceDto> result = absenceService.getAll(1, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void createTest() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(4);
        StatusDto statusDto = new StatusDto(1L, "Awaiting");
        AbsenceDto absenceDto = AbsenceDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceTypeId(1L)
                .status(statusDto)
                .userId(1L)
                .build();
        User user = User.builder().id(1L).build();
        AbsenceType absenceType = new AbsenceType(1L, "Sick leave");
        Absence absence = Absence.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceType(absenceType)
                .user(user)
                .status(new Status(1L, "Awaiting"))
                .build();

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.save(absence)).thenReturn(absence);
        when(repositoryCollector.getAbsenceTypes()).thenReturn(absenceTypeRepository);
        when(absenceTypeRepository.findById(1L)).thenReturn(Optional.of(absenceType));
        when(repositoryCollector.getStatuses()).thenReturn(statusRepository);
        when(statusRepository.findById(AbsenceStatus.AWAITING.getId())).thenReturn(Optional.of(mock(Status.class)));

        AbsenceDto result;
        try (MockedStatic<AbsenceDto> mockedAbsenceDto = mockStatic(AbsenceDto.class);
             MockedStatic<HolidayCalculator> mockedHolidayCalculator = mockStatic(HolidayCalculator.class);
             MockedStatic<AbsenceNotification> mockedAbsenceNotifications = mockStatic(AbsenceNotification.class)) {

            mockedHolidayCalculator.when(() -> HolidayCalculator.calculateWorkingDays(start, end)).thenReturn(5L);
            mockedAbsenceDto.when(() -> AbsenceDto.fromEntity(absence)).thenReturn(absenceDto);
            mockedAbsenceNotifications.when(() -> AbsenceNotification.createNotification(any(Absence.class), any(AbsenceNotificationType.class))).thenReturn(mock(NotificationDto.class));
            result = absenceService.create(absenceDto);
        }


        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void updateStatusTest() {
        AbsenceDto absenceDto = mock(AbsenceDto.class);
        Status status = mock(Status.class);
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(4);
        User user = User.builder().id(1L).build();
        AbsenceType absenceType = new AbsenceType(1L, "Sick leave");
        Absence absence = Absence.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceType(absenceType)
                .user(user)
                .status(new Status(1L, "Awaiting"))
                .build();
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(repositoryCollector.getStatuses()).thenReturn(statusRepository);
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));
        when(statusRepository.findById(AbsenceStatus.APPROVED.getId())).thenReturn(Optional.of(status));
        when(absenceRepository.save(absence)).thenReturn(absence);
        AbsenceDto result;
        try (MockedStatic<AbsenceDto> mockedStatic = mockStatic(AbsenceDto.class)) {
            mockedStatic.when(() -> AbsenceDto.fromEntity(absence)).thenReturn(absenceDto);
            result = absenceService.updateStatus(1L, AbsenceStatus.APPROVED.getId());
        }

        assertNotNull(result);
        verify(notificationService).create(any());
    }

    @Test
    public void updateStatusInvalidStatusThrowsInvalidAbsenceStatusExceptionTest() {
        Absence absence = mock(Absence.class);
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));

        assertThrows(InvalidAbsenceStatusException.class, () -> absenceService.updateStatus(1L, AbsenceStatus.AWAITING.getId()));
    }

    @Test
    public void updateStatusStartDateInPastThrowsInvalidDateExceptionTest() {
        Absence absence = mock(Absence.class);
        when(absence.getStart()).thenReturn(LocalDate.now().minusDays(1));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));

        assertThrows(InvalidDateException.class, () -> absenceService.updateStatus(1L, AbsenceStatus.APPROVED.getId()));
    }

    @Test
    public void updateStatusAbsenceCancelledThrowsInvalidAbsenceStatusExceptionTest() {
        Absence absence = mock(Absence.class);
        when(absence.getStart()).thenReturn(LocalDate.now().plusDays(1));
        when(absence.getStatus()).thenReturn(new Status(AbsenceStatus.CANCELLED.getId(), "Cancelled"));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));

        assertThrows(InvalidAbsenceStatusException.class, () -> absenceService.updateStatus(1L, AbsenceStatus.APPROVED.getId()));
    }

    @Test
    public void updateStatusAbsenceRejectedThrowsInvalidAbsenceStatusExceptionTest() {
        Absence absence = mock(Absence.class);
        when(absence.getStart()).thenReturn(LocalDate.now().plusDays(1));
        when(absence.getStatus()).thenReturn(new Status(AbsenceStatus.REJECTED.getId(), "Cancelled"));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));

        assertThrows(InvalidAbsenceStatusException.class, () -> absenceService.updateStatus(1L, AbsenceStatus.APPROVED.getId()));
    }

    @Test
    public void deleteAbsenceTest() {
        Status status = new Status(1L, "Awaiting");
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(4);
        User user = User.builder().id(1L).build();
        AbsenceType absenceType = new AbsenceType(1L, "Sick leave");
        Absence absence = Absence.builder()
                .id(1L)
                .start(start)
                .end(end)
                .absenceType(absenceType)
                .user(user)
                .status(status)
                .build();
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));

        absenceService.deleteAbsence(1L, AbsenceStatus.CANCELLED.getId());

        verify(absenceRepository).delete(absence);
    }
}