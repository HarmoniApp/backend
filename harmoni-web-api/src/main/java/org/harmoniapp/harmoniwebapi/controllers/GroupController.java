package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.GroupDto;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.harmoniapp.harmoniwebapi.services.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("group")
@CrossOrigin(origins = "http://localhost:3000")
public class GroupController {
    private final GroupService service;

    @GetMapping("/{id}")
    public List<PartialUserWithEmpIdDto> getGroupMembersByGroupId(@PathVariable("id") Long groupId) {
        return service.getGroupMembersByGroupId(groupId);
    }

    @PostMapping
    public GroupDto createGroup(@RequestBody GroupDto groupDto) {
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

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable("id") Long groupId) {
        service.deleteGroup(groupId);
    }
}
