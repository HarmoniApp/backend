package org.harmoniapp.services.user;

import org.harmoniapp.contracts.user.UserNewPassword;
import org.harmoniapp.entities.user.User;

/**
 * Service interface for managing user passwords.
 */
public interface UserPasswordService extends FindUser {

    /**
     * Sets a new password for the given user.
     *
     * @param user the user for whom the password is to be set
     * @return the new password
     */
    String setPassword(User user);

    /**
     * Sets a new password for the given user with an option to mark it as temporary.
     *
     * @param user        the user for whom the password is to be set
     * @param pwd         the new password
     * @param isTemporary whether the password is temporary
     */
    void setPassword(User user, String pwd, boolean isTemporary);

    /**
     * Changes the password for the user with the given ID.
     *
     * @param id  the ID of the user
     * @param pwd the new password details
     * @return the new password
     */
    String changePassword(long id, UserNewPassword pwd);

    /**
     * Generates a new password for the user with the given ID.
     *
     * @param id the ID of the user
     * @return the generated password
     */
    String generateNewPassword(long id);
}
