package org.harmoniapp.controllers.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.GroupDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.services.chat.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling group chat operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final GroupService service;

    /**
     * Retrieves the details of a group by its ID.
     *
     * @param groupId the ID of the group
     * @return the group details as a GroupDto
     */
    @GetMapping("/details/{groupId}")
    public GroupDto getGroupById(@PathVariable Long groupId) {
        return service.getById(groupId);
    }

    /**
     * Retrieves the members of a group by the group ID.
     *
     * @param groupId the ID of the group
     * @return a list of PartialUserDto representing the group members
     */
    @GetMapping("/{groupId}/members")
    public List<PartialUserDto> getGroupMembersByGroupId(@PathVariable Long groupId) {
        return service.getMembersById(groupId);
    }

    /**
     * Creates a new group.
     *
     * @param groupDto the group data
     * @return the created group as a GroupDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDto createGroup(@Valid @RequestBody GroupDto groupDto) {
        return service.create(groupDto);
    }

    /**
     * Adds a member to a group.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user to be added
     * @return the updated group as a GroupDto
     */
    @PatchMapping("/{groupId}/user/{userId}/add")
    public GroupDto addMemberToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return service.addMember(groupId, userId);
    }

    /**
     * Removes a member from a group.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user to be removed
     * @return the updated group as a GroupDto
     */
    @PatchMapping("/{groupId}/user/{userId}/remove")
    public GroupDto removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return service.removeMember(groupId, userId);
    }

    /**
     * Deletes a group by its ID.
     *
     * @param groupId the ID of the group
     */
    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long groupId) {
        service.delete(groupId);
    }
}
