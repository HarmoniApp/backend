package org.harmoniapp.harmoniwebapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Group;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.GroupDto;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class GroupService {
    private final RepositoryCollector repositoryCollector;

    public GroupDto getGroupById(Long groupId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        return GroupDto.fromEntity(group);
    }

    public List<PartialUserWithEmpIdDto> getGroupMembersByGroupId(Long groupId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        return group.getMembers().stream()
                .map(user -> new PartialUserWithEmpIdDto(user.getId(), user.getFirstname(), user.getSurname(), user.getPhoto(), user.getEmployeeId()))
                .toList();
    }

    public List<Long> getGroupChatPartners(Long userId){
        return repositoryCollector.getMessages().findGroupChatPartners(userId);
    }

    @Transactional
    public GroupDto createGroup(GroupDto groupDto) {
        Set<User> members = groupDto.membersIds().stream()
                .map(memberId -> repositoryCollector.getUsers().findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + memberId)))
                .collect(Collectors.toSet());

        Group group = groupDto.toEntity(members);
        Group savedGroup = repositoryCollector.getGroups().save(group);
        return GroupDto.fromEntity(savedGroup);
    }

    @Transactional
    public GroupDto addMemberToGroup(Long groupId, Long userId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User user = repositoryCollector.getUsers().findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        group.getMembers().add(user);
        Group savedGroup = repositoryCollector.getGroups().save(group);
        return GroupDto.fromEntity(savedGroup);
    }


    @Transactional
    public GroupDto removeMemberFromGroup(Long groupId, Long userId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User user = repositoryCollector.getUsers().findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        group.getMembers().remove(user);
        Group savedGroup = repositoryCollector.getGroups().save(group);
        return GroupDto.fromEntity(savedGroup);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        try {
            repositoryCollector.getMessages().deleteByGroupId(groupId);
            repositoryCollector.getGroups().deleteById(groupId);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }
}
