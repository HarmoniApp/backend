package org.harmoniapp.harmoniwebapi.contracts;

import java.util.List;

/**
 * Data Transfer Object for user import response.
 *
 * @param users      the list of successfully imported users.
// * @param failedRows the list of row numbers that failed to import.
 */
public record UserImportResponseDto(List<UserDto> users, List<Integer> failedRows) {
}
