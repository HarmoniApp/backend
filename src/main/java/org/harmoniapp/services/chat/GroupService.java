package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.GroupDto;
import org.harmoniapp.contracts.user.PartialUserDto;

import java.util.List;

/**
 * Service interface for managing groups.
 */
public interface GroupService {

    /**
     * Retrieves a group by its ID.
     *
     * @param groupId the ID of the group
     * @return the group DTO
     */
    GroupDto getById(long groupId);

    /**
     * Retrieves the members of a group by the group's ID.
     *
     * @param groupId the ID of the group
     * @return a list of partial user DTOs
     */
    List<PartialUserDto> getMembersById(long groupId);

    /**
     * Creates a new group.
     *
     * @param groupDto the group DTO
     * @return the created group DTO
     */
    GroupDto create(GroupDto groupDto);

    /**
     * Adds a member to a group.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user to add
     * @return the updated group DTO
     */
    GroupDto addMember(long groupId, long userId);

    /**
     * Removes a member from a group.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user to remove
     * @return the updated group DTO
     */
    GroupDto removeMember(long groupId, long userId);

    /**
     * Deletes a group by its ID.
     *
     * @param groupId the ID of the group
     */
    void delete(long groupId);
}
