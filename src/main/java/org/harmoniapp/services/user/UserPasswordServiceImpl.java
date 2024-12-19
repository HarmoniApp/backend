package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.user.UserNewPassword;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EasyPasswordException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.utils.PasswordManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service implementation for managing user passwords.
 */
@Service
@RequiredArgsConstructor
public class UserPasswordServiceImpl implements UserPasswordService {
    private final RepositoryCollector repositoryCollector;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker passwordChecker;

    /**
     * Sets a new password for the given user.
     *
     * @param user the user for whom the password is being set
     * @return the raw password that was generated and set
     */
    @Override
    public String setPassword(User user) {
        String rawPwd = PasswordManager.generateCommonTextPassword();
        setPassword(user, rawPwd, true);
        return rawPwd;
    }

    /**
     * Sets the password for the given user.
     *
     * @param user        the user for whom the password is being set
     * @param pwd         the raw password to be set
     * @param isTemporary whether the password is temporary or not
     */
    @Override
    public void setPassword(User user, String pwd, boolean isTemporary) {
        String hashedPwd = passwordEncoder.encode(pwd);
        user.setPassword(hashedPwd);
        user.setPasswordExpirationDate(
                (isTemporary) ? LocalDate.now().minusDays(1) : LocalDate.now().plusMonths(6));
        user.setFailedLoginAttempts(0);
    }

    /**
     * Changes the password for the user with the given ID.
     *
     * @param id  the ID of the user whose password is being changed
     * @param pwd the new password details
     * @return a confirmation message indicating the password was changed successfully
     * @throws EasyPasswordException if the new password is compromised
     */
    @Override
    public String changePassword(long id, UserNewPassword pwd) throws EasyPasswordException {
        validatePassword(pwd.newPassword());
        User user = getUserById(id);
        updatePassword(user, pwd.newPassword());
        return "Hasło zmienione pomyślnie";
    }

    /**
     * Generates a new password for the user with the given ID.
     *
     * @param id the ID of the user for whom the new password is being generated
     * @return the raw password that was generated and set
     */
    @Override
    public String generateNewPassword(long id) {
        User user = getUserById(id);
        String rawPass = setPassword(user);
        repositoryCollector.getUsers().save(user);
        return rawPass;
    }

    private User getUserById(long id) {
        return repositoryCollector.getUsers().findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Validates the given password to ensure it is not compromised.
     *
     * @param newPassword the new password to validate
     * @throws EasyPasswordException if the password is found to be compromised
     */
    private void validatePassword(String newPassword) throws EasyPasswordException {
        if (passwordChecker.check(newPassword).isCompromised()) {
            throw new EasyPasswordException();
        }
    }

    /**
     * Updates the password for the given user.
     *
     * @param user        the user whose password is being updated
     * @param newPassword the new password to set for the user
     */
    private void updatePassword(User user, String newPassword) {
        setPassword(user, newPassword, false);
        repositoryCollector.getUsers().save(user);
    }
}
