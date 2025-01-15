package org.harmoniapp.services.user;

import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.ContractTypeRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAbsenceServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractTypeRepository contractTypeRepository;

    @InjectMocks
    private UserAbsenceServiceImpl userAbsenceService;

    @BeforeEach
    void setUp() {
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
    }

    @Test
    void getUserAvailableAbsenceDaysSuccessfulTest() {
        long userId = 1L;
        User user = new User();
        user.setAvailableAbsenceDays(10);
        user.setUnusedAbsenceDays(5);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));

        int result = userAbsenceService.getUserAvailableAbsenceDays(userId);

        assertEquals(15, result);
    }

    @Test
    void getUserAvailableAbsenceDaysUserNotFoundTest() {
        long userId = 1L;
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userAbsenceService.getUserAvailableAbsenceDays(userId));
    }

    @Test
    void carryOverPreviousYearAbsenceDaysTest() {
        User user = new User();
        user.setAvailableAbsenceDays(10);
        user.setUnusedAbsenceDays(5);
        user.setContractType(new ContractType(1L, "Full-time", 20));

        when(repositoryCollector.getContractTypes()).thenReturn(contractTypeRepository);
        when(contractTypeRepository.findById(1L)).thenReturn(Optional.of(new ContractType(1L, "Full-time", 20)));

        userAbsenceService.carryOverPreviousYearAbsenceDays(user);

        assertEquals(20, user.getAvailableAbsenceDays());
        assertEquals(10, user.getUnusedAbsenceDays());
        assertNotNull(user.getUnusedAbsenceExpiration());
        verify(repositoryCollector.getUsers()).save(user);
    }

    @Test
    void expireUnusedAbsenceDaysTest() {
        User user = new User();
        user.setUnusedAbsenceDays(5);
        user.setUnusedAbsenceExpiration(LocalDate.now().minusDays(1));

        userAbsenceService.expireUnusedAbsenceDays(user);

        assertEquals(0, user.getUnusedAbsenceDays());
        assertEquals(LocalDate.now().plusYears(1).minusDays(1), user.getUnusedAbsenceExpiration());
        verify(repositoryCollector.getUsers()).save(user);
    }

    @Test
    void scheduledCarryOverPreviousYearAbsenceDaysTest() {
        // given
        User user1 = new User();
        user1.setAvailableAbsenceDays(10);
        user1.setUnusedAbsenceDays(5);
        user1.setContractType(new ContractType(1L, "Full-time", 20));

        User user2 = new User();
        user2.setAvailableAbsenceDays(15);
        user2.setUnusedAbsenceDays(10);
        user2.setContractType(new ContractType(2L, "Part-time", 10));

        List<User> users = List.of(user1, user2);

        ContractType contract1 = new ContractType(1L, "Full-time", 20);
        ContractType contract2 = new ContractType(2L, "Part-time", 10);

        when(repositoryCollector.getContractTypes()).thenReturn(contractTypeRepository);
        when(contractTypeRepository.findById(1L)).thenReturn(Optional.of(contract1));
        when(contractTypeRepository.findById(2L)).thenReturn(Optional.of(contract2));
        when(userRepository.findAllByIsActiveTrue()).thenReturn(users);

        // when
        userAbsenceService.scheduledCarryOverPreviousYearAbsenceDays();

        // then
        assertEquals(20, user1.getAvailableAbsenceDays());
        assertEquals(10, user1.getUnusedAbsenceDays());
        assertNotNull(user1.getUnusedAbsenceExpiration());

        assertEquals(10, user2.getAvailableAbsenceDays());
        assertEquals(15, user2.getUnusedAbsenceDays());
        assertNotNull(user2.getUnusedAbsenceExpiration());

        verify(repositoryCollector.getUsers(), times(2)).save(any(User.class));
    }
}
