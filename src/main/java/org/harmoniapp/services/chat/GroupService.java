package org.harmoniapp.services.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.GroupDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final RepositoryCollector repositoryCollector;

    public GroupDto getGroupById(Long groupId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        return GroupDto.fromEntity(group);
    }

    public List<PartialUserDto> getGroupMembersByGroupId(Long groupId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        return group.getMembers().stream()
                .map(PartialUserDto::fromEntity)
                .toList();
    }

    @Transactional
    public GroupDto createGroup(GroupDto groupDto) {
        Set<User> members = repositoryCollector.getUsers().findByIdInAndIsActiveTrue(groupDto.membersIds());
        for (Long userId : groupDto.membersIds()) {
            if (members.stream().noneMatch(user -> user.getId().equals(userId))) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }
        }

        Group group = groupDto.toEntity(members);
        Group savedGroup = repositoryCollector.getGroups().save(group);
        return GroupDto.fromEntity(savedGroup);
    }

    @Transactional
    public GroupDto addMemberToGroup(Long groupId, Long userId) {
        Group group = repositoryCollector.getGroups().findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User user = repositoryCollector.getUsers().findByIdAndIsActive(userId, true)
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
