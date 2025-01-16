package org.harmoniapp.services.absence;

import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.AbsenceDaysExceededException;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbsenceDaysUpdaterImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AbsenceDaysUpdaterImpl absenceDaysUpdater;

    @Test
    public void updateUserAbsenceDaysTest() {
        User user = mock(User.class);
        when(user.getAvailableAbsenceDays()).thenReturn(5);
        when(user.getUnusedAbsenceDays()).thenReturn(3);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.save(user)).thenReturn(user);
        absenceDaysUpdater.updateUserAbsenceDays(user, 4);

        verify(user).setUnusedAbsenceDays(0);
        verify(user).setAvailableAbsenceDays(4);
        verify(repositoryCollector.getUsers()).save(user);
    }

    @Test
    public void updateUserAbsenceDaysExceedsAvailableDaysTest() {
        User user = mock(User.class);
        when(user.getAvailableAbsenceDays()).thenReturn(2);
        when(user.getUnusedAbsenceDays()).thenReturn(1);

        assertThrows(AbsenceDaysExceededException.class,
                () -> absenceDaysUpdater.updateUserAbsenceDays(user, 4));
    }

    @Test
    public void updateUserAbsenceDaysWithAbsenceTest() {
        Absence absence = mock(Absence.class);
        ContractType contractType = mock(ContractType.class);
        User user = User.builder()
                .id(1L)
                .contractType(contractType)
                .availableAbsenceDays(4)
                .unusedAbsenceDays(0)
                .build();
        when(absence.getUser()).thenReturn(user);
        when(contractType.getAbsenceDays()).thenReturn(5);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(absence.getWorkingDays()).thenReturn(4L);

        absenceDaysUpdater.updateUserAbsenceDays(absence);

        assertEquals(5, user.getAvailableAbsenceDays());
        assertEquals(3, user.getUnusedAbsenceDays());
        verify(repositoryCollector.getUsers()).save(user);
    }

    @Test
    public void updateUserAbsenceDaysUserNotFoundTest() {
        Absence absence = mock(Absence.class);
        User user = mock(User.class);
        when(absence.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> absenceDaysUpdater.updateUserAbsenceDays(absence));
    }
}