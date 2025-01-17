// GroupServiceImplTest.java
package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.GroupDto;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.chat.GroupRepository;
import org.harmoniapp.repositories.chat.MessageRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    public void getByIdTest() {
        Group group = mock(Group.class);
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(groupRepository.findById(1L)).thenReturn(java.util.Optional.of(group));

        GroupDto result;
        try (MockedStatic<GroupDto> mockedStatic = mockStatic(GroupDto.class)) {
            mockedStatic.when(() -> GroupDto.fromEntity(group)).thenReturn(mock(GroupDto.class));
            result = groupService.getById(1L);
        }

        assertNotNull(result);
    }

    @Test
    public void getMembersByIdTest() {
        Group group = mock(Group.class);
        User user = mock(User.class);
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(groupRepository.findById(1L)).thenReturn(java.util.Optional.of(group));
        when(group.getMembers()).thenReturn(Set.of(user));

        List<PartialUserDto> result;
        try (MockedStatic<GroupDto> mockedStatic = mockStatic(GroupDto.class)) {
            mockedStatic.when(() -> GroupDto.fromEntity(group)).thenReturn(mock(GroupDto.class));
            result = groupService.getMembersById(1L);
        }

        assertNotNull(result);
    }

    @Test
    public void createTest() {
        GroupDto groupDto = mock(GroupDto.class);
        Group group = mock(Group.class);
        User user = mock(User.class);
        when(groupDto.membersIds()).thenReturn(List.of(1L));
        when(user.getId()).thenReturn(1L);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdInAndIsActiveTrue(anyCollection())).thenReturn(Set.of(user));
        when(groupDto.toEntity(anySet())).thenReturn(group);
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(groupRepository.save(group)).thenReturn(group);

        GroupDto result;
        try (MockedStatic<GroupDto> mockedStatic = mockStatic(GroupDto.class)) {
            mockedStatic.when(() -> GroupDto.fromEntity(group)).thenReturn(mock(GroupDto.class));
            result = groupService.create(groupDto);
        }

        assertNotNull(result);
    }

    @Test
    public void addMemberTest() {
        Group group = mock(Group.class);
        User user = mock(User.class);
        when(group.getMembers()).thenReturn(new HashSet<>(List.of(user)));
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(user));
        when(groupRepository.save(group)).thenReturn(group);

        GroupDto result;
        try (MockedStatic<GroupDto> mockedStatic = mockStatic(GroupDto.class)) {
            mockedStatic.when(() -> GroupDto.fromEntity(group)).thenReturn(mock(GroupDto.class));
            result = groupService.addMember(1L, 1L);
        }

        assertNotNull(result);
    }

    @Test
    public void removeMemberTest() {
        Group group = mock(Group.class);
        User user = mock(User.class);
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(groupRepository.existsById(1L)).thenReturn(true);
        when(groupRepository.findById(1L)).thenReturn(java.util.Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(group.getMembers()).thenReturn(Set.of(user));
        doNothing().when(messageRepository).deleteByGroupId(1L);

        GroupDto result;
        try (MockedStatic<GroupDto> mockedStatic = mockStatic(GroupDto.class)) {
            mockedStatic.when(() -> GroupDto.fromEntity(group)).thenReturn(mock(GroupDto.class));
            result = groupService.removeMember(1L, 1L);
        }


        assertNull(result);
    }

    @Test
    public void deleteTest() {
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(groupRepository.existsById(1L)).thenReturn(true);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        doNothing().when(messageRepository).deleteByGroupId(1L);

        groupService.delete(1L);

        verify(repositoryCollector.getMessages(), times(1)).deleteByGroupId(1L);
        verify(repositoryCollector.getGroups(), times(1)).deleteById(1L);
    }
}