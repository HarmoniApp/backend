package org.harmoniapp.harmoniwebapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.GroupDto;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.harmoniapp.harmoniwebapi.services.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final GroupService service;

    @GetMapping("/details/{groupId}")
    public GroupDto getGroupById(@PathVariable Long groupId) {
        return service.getGroupById(groupId);
    }

    @GetMapping("/{groupId}/members")
    public List<PartialUserWithEmpIdDto> getGroupMembersByGroupId(@PathVariable Long groupId) {
        return service.getGroupMembersByGroupId(groupId);
    }

    @GetMapping("/chat-partners")
    public List<Long> getGroupChatPartners(@RequestParam Long userId) {
        return service.getGroupChatPartners(userId);
    }

    @PostMapping
    public GroupDto createGroup(@Valid @RequestBody GroupDto groupDto) {
        return  service.createGroup(groupDto);
    }

    @PatchMapping("/{groupId}/user/{userId}/add")
    public GroupDto addMemberToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return service.addMemberToGroup(groupId, userId);
    }

    @PatchMapping("/{groupId}/user/{userId}/remove")
    public GroupDto removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return service.removeMemberFromGroup(groupId, userId);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        service.deleteGroup(groupId);
    }
}
