package org.harmoniapp.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.entities.User;

/**
 * Data Transfer Object for User and their associated employee ID.
 *
 * @param id         the unique identifier of the user
 * @param firstname  the first name of the user
 * @param surname    the surname of the user
 * @param employeeId the employee id of user
 */
public record PartialUserWithEmpIdDto(long id,
                                      String firstname,
                                      String surname,
                                      String photo,
                                      @JsonProperty("employee_id") String employeeId) {

    /**
     * Converts a User entity to a PartialUserWithEmpIdDto.
     *
     * @param user the User entity to convert
     * @return the resulting PartialUserWithEmpIdDto
     */
    public static PartialUserWithEmpIdDto fromEntity(User user) {
        return new PartialUserWithEmpIdDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getEmployeeId()
        );
    }
}
