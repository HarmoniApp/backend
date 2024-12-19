package org.harmoniapp.services.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.GroupDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Service implementation for managing groups.
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a GroupDto by its ID.
     *
     * @param groupId the ID of the group to retrieve
     * @return the GroupDto corresponding to the specified ID
     * @throws EntityNotFound if no group is found with the specified ID
     */
    @Override
    public GroupDto getById(long groupId) {
        Group group = getGroupById(groupId);
        return GroupDto.fromEntity(group);
    }

    /**
     * Retrieves the members of a group by its ID.
     *
     * @param groupId the ID of the group whose members are to be retrieved
     * @return a list of PartialUserDto representing the members of the group
     * @throws EntityNotFound if no group is found with the specified ID
     */
    @Override
    public List<PartialUserDto> getMembersById(long groupId) {
        Group group = getGroupById(groupId);
        return group.getMembers().stream()
                .map(PartialUserDto::fromEntity)
                .toList();
    }

    /**
     * Creates a new group.
     *
     * @param groupDto the data transfer object containing the details of the group to be created
     * @return the created GroupDto
     * @throws IllegalArgumentException if any user ID in the groupDto is not found or inactive
     */
    @Override
    @Transactional
    public GroupDto create(GroupDto groupDto) {
        Set<User> members = repositoryCollector.getUsers().findByIdInAndIsActiveTrue(groupDto.membersIds());
        for (Long userId : groupDto.membersIds()) {
            if (members.stream().noneMatch(user -> user.getId().equals(userId))) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }
        }

        Group group = groupDto.toEntity(members);
        return saveGroup(group);
    }

    /**
     * Adds a member to a group.
     *
     * @param groupId the ID of the group to which the member is to be added
     * @param userId  the ID of the user to be added as a member
     * @return the updated GroupDto after adding the member
     * @throws EntityNotFound if no group or user is found with the specified IDs
     */
    @Override
    @Transactional
    public GroupDto addMember(long groupId, long userId) {
        Group group = getGroupById(groupId);
        User user = getUserById(userId, true);
        group.getMembers().add(user);
        return saveGroup(group);
    }

    /**
     * Removes a member from a group.
     *
     * @param groupId the ID of the group from which the member is to be removed
     * @param userId  the ID of the user to be removed as a member
     * @return the updated GroupDto after removing the member, or null if the group is deleted
     * @throws EntityNotFound if no group or user is found with the specified IDs
     */
    @Override
    @Transactional
    public GroupDto removeMember(long groupId, long userId) {
        Group group = getGroupById(groupId);
        User user = getUserById(userId, false);
        group.getMembers().remove(user);
        if (group.getMembers().isEmpty()) {
            delete(groupId);
            return null;
        }
        return saveGroup(group);
    }

    /**
     * Deletes a group by its ID.
     *
     * @param groupId the ID of the group to delete
     * @throws EntityNotFound if no group is found with the specified ID
     */
    @Override
    @Transactional
    public void delete(long groupId) {
        if (!repositoryCollector.getGroups().existsById(groupId)) {
            throw new EntityNotFound("Group not found with ID: " + groupId);
        }
        repositoryCollector.getMessages().deleteByGroupId(groupId);
        repositoryCollector.getGroups().deleteById(groupId);
    }

    /**
     * Saves the given Group entity and returns its corresponding GroupDto.
     *
     * @param group the Group entity to be saved
     * @return the GroupDto corresponding to the saved Group entity
     */
    private GroupDto saveGroup(Group group) {
        Group savedGroup = repositoryCollector.getGroups().save(group);
        return GroupDto.fromEntity(savedGroup);
    }

    /**
     * Retrieves a User entity by its ID and active status.
     *
     * @param userId   the ID of the user to retrieve
     * @param isActive whether the user should be active
     * @return the User entity corresponding to the specified ID and active status
     * @throws EntityNotFound if no user is found with the specified ID and active status
     */
    private User getUserById(long userId, boolean isActive) {
        if (isActive) {
            return repositoryCollector.getUsers().findByIdAndIsActiveTrue(userId)
                    .orElseThrow(() -> new EntityNotFound("User not found"));
        } else {
            return repositoryCollector.getUsers().findById(userId)
                    .orElseThrow(() -> new EntityNotFound("User not found"));
        }
    }

    /**
     * Retrieves a Group entity by its ID.
     *
     * @param groupId the ID of the group to retrieve
     * @return the Group entity corresponding to the specified ID
     * @throws EntityNotFound if no group is found with the specified ID
     */
    private Group getGroupById(long groupId) {
        return repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono grupy o ID: " + groupId));
    }
}
