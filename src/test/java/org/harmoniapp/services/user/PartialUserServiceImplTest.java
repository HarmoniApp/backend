package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PartialUserServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private PartialUserServiceImpl partialUserServiceImpl;

    @Test
    public void getUserSuccessTest() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .roles(new HashSet<>())
                .languages(new HashSet<>())
                .build();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));

        PartialUserDto result = partialUserServiceImpl.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        verify(repositoryCollector.getUsers(), times(1)).findByIdAndIsActiveTrue(userId);
    }

    @Test
    public void getUserNotFoundTest() {
        long userId = 1L;
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partialUserServiceImpl.getUser(userId));
        verify(repositoryCollector.getUsers(), times(1)).findByIdAndIsActiveTrue(userId);
    }

    @Test
    public void getPageSuccessTest() {
        UserSearchParamsDto searchParamsDto = new UserSearchParamsDto(List.of(1L), null, null);
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, null, null);
        Page<User> userPage = mock(Page.class);
        when(userSearchService.findUsersPage(pageRequestDto, searchParamsDto)).thenReturn(userPage);

        PageDto<PartialUserDto> result = partialUserServiceImpl.getPage(searchParamsDto, pageRequestDto);

        assertNotNull(result);
        verify(userSearchService, times(1)).findUsersPage(pageRequestDto, searchParamsDto);
    }

    @Test
    public void getPageWithNullSearchParamsTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, null, null);
        Page<User> userPage = mock(Page.class);
        when(userSearchService.findUsersPage(pageRequestDto, null)).thenReturn(userPage);

        PageDto<PartialUserDto> result = partialUserServiceImpl.getPage(null, pageRequestDto);

        assertNotNull(result);
        verify(userSearchService, times(1)).findUsersPage(pageRequestDto, null);
    }

    @Test
    void getUsersSearchTest() {
        String query = "test";
        User user = User.builder()
                .id(1L)
                .roles(new HashSet<>())
                .languages(new HashSet<>())
                .build();
        when(userSearchService.searchUsers(query)).thenReturn(List.of(user));

        List<PartialUserDto> result = partialUserServiceImpl.getUsersSearch(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(userSearchService, times(1)).searchUsers(query);
    }
}
