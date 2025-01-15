package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.SupervisorDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupervisorServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private SupervisorServiceImpl supervisorService;

    @Test
    void getAllSupervisorsTest() {
        // given
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = User.builder().id(1L).roles(new HashSet<>()).languages(new HashSet<>()).build();
        User user2 = User.builder().id(2L).roles(new HashSet<>()).languages(new HashSet<>()).build();
        List<User> userList = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(userList);
        when(userSearchService.createPageable(pageRequestDto)).thenReturn(pageable);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findSupervisors(pageable)).thenReturn(userPage);

        // when
        PageDto<SupervisorDto> result = supervisorService.getAllSupervisors(pageRequestDto);

        // than
        assertNotNull(result);
        assertEquals(2, result.content().size());
        verify(userSearchService).createPageable(pageRequestDto);
        verify(repositoryCollector.getUsers()).findSupervisors(pageable);
    }
}
