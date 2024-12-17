package org.harmoniapp.controllers.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.GroupDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.services.chat.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final GroupService service;

    @GetMapping("/details/{groupId}")
    public GroupDto getGroupById(@PathVariable Long groupId) {
        return service.getById(groupId);
    }

    @GetMapping("/{groupId}/members")
    public List<PartialUserDto> getGroupMembersByGroupId(@PathVariable Long groupId) {
        return service.getMembersById(groupId);
    }

    @PostMapping
    public GroupDto createGroup(@Valid @RequestBody GroupDto groupDto) {
        return  service.create(groupDto);
    }

    @PatchMapping("/{groupId}/user/{userId}/add")
    public GroupDto addMemberToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return service.addMember(groupId, userId);
    }

    @PatchMapping("/{groupId}/user/{userId}/remove")
    public GroupDto removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return service.removeMember(groupId, userId);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        service.delete(groupId);
    }
}
