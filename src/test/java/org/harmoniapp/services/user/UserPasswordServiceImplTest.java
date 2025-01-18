package org.harmoniapp.services.user;

import org.harmoniapp.contracts.user.UserNewPasswordDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EasyPasswordException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.utils.PasswordGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPasswordServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CompromisedPasswordChecker passwordChecker;

    @InjectMocks
    private UserPasswordServiceImpl userPasswordService;

    @Test
    public void setPasswordTest() {
        User user = new User();
        String rawPassword = "rawPassword";
        Mockito.mockStatic(PasswordGenerator.class).when(PasswordGenerator::generateCommonTextPassword).thenReturn(rawPassword);
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");

        String result = userPasswordService.setPassword(user);

        assertEquals(rawPassword, result);
        verify(passwordEncoder).encode(rawPassword);
        assertEquals("hashedPassword", user.getPassword());
        assertEquals(LocalDate.now().minusDays(1), user.getPasswordExpirationDate());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    public void setPasswordWithTemporaryFlagTest() {
        User user = new User();
        String rawPassword = "rawPassword";
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");

        String result = userPasswordService.setPassword(user);

        assertEquals(rawPassword, result);
        verify(passwordEncoder).encode(rawPassword);
        assertEquals("hashedPassword", user.getPassword());
        assertEquals(LocalDate.now().minusDays(1), user.getPasswordExpirationDate());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    public void setPasswordWithPermanentFlagTest() {
        User user = new User();
        String rawPassword = "rawPassword";
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");

        userPasswordService.setPassword(user, rawPassword, false);

        verify(passwordEncoder).encode(rawPassword);
        assertEquals("hashedPassword", user.getPassword());
        assertEquals(LocalDate.now().plusMonths(6), user.getPasswordExpirationDate());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    public void changePasswordTest() {
        long userId = 1L;
        String newPassword = "newPassword";
        UserNewPasswordDto passwordDto = new UserNewPasswordDto(newPassword);
        User user = new User();

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedNewPassword");
        when(passwordChecker.check(newPassword)).thenReturn(new CompromisedPasswordDecision(false));

        String result = userPasswordService.changePassword(userId, passwordDto);

        assertEquals("Hasło zmienione pomyślnie", result);
        verify(passwordChecker).check(newPassword);
        verify(passwordEncoder).encode(newPassword);
        verify(repositoryCollector.getUsers()).save(user);
        assertEquals("hashedNewPassword", user.getPassword());
    }

    @Test
    public void changePasswordCompromisedTest() {
        long userId = 1L;
        String newPassword = "newPassword";
        UserNewPasswordDto passwordDto = new UserNewPasswordDto(newPassword);

        when(passwordChecker.check(newPassword)).thenReturn(new CompromisedPasswordDecision(true));

        assertThrows(EasyPasswordException.class, () -> {
            userPasswordService.changePassword(userId, passwordDto);
        });
    }

    @Test
    public void generateNewPasswordTest() {
        long userId = 1L;
        User user = new User();
        String rawPassword = "rawPassword";

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");

        String result = userPasswordService.generateNewPassword(userId);

        assertEquals(rawPassword, result);
        verify(passwordEncoder).encode(rawPassword);
        verify(repositoryCollector.getUsers()).save(user);
        assertEquals("hashedPassword", user.getPassword());
        assertEquals(LocalDate.now().minusDays(1), user.getPasswordExpirationDate());
        assertEquals(0, user.getFailedLoginAttempts());
    }
}
