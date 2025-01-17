package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSearchServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSearchServiceImpl userSearchService;

    @Test
    public void searchUsersTest() {
        String query = "test query";
        List<String> qSplit = List.of("TEST", "QUERY");
        List<User> expectedUsers = List.of(new User(), new User());

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllActiveBySearchName(qSplit)).thenReturn(expectedUsers);

        List<User> result = userSearchService.searchUsers(query);

        assertEquals(expectedUsers, result);
    }

    @Test
    public void findUsersPageTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, "desc", "surname");
        List<User> users = List.of(new User(), new User());
        Page<User> expectedPage = new PageImpl<>(users);

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = userSearchService.findUsersPage(pageRequestDto);

        assertEquals(expectedPage, result);
    }

    @Test
    public void findUsersPageWithSearchParamsTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, "asc", "surname");
        UserSearchParamsDto searchParamsDto = new UserSearchParamsDto(List.of(1L), List.of(1L), List.of(1L));
        List<User> users = List.of(new User(), new User());
        Page<User> expectedPage = new PageImpl<>(users);

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllByContractAndRoleAndLanguageAndIsActive(
                eq(searchParamsDto.contracts()), eq(searchParamsDto.roles()), eq(searchParamsDto.language()), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<User> result = userSearchService.findUsersPage(pageRequestDto, searchParamsDto);

        assertEquals(expectedPage, result);
    }

    @Test
    public void createPageableTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, "asc", "surname");

        Pageable pageable = userSearchService.createPageable(pageRequestDto);

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableNullPageNumberTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(null, 10, "asc", "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableNegativePageNumberTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(-1, 10, "asc", "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableNullPageSizeTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, null, "asc", "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableNegativePageSizeTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, -1, "asc", "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableNullOrderTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, null, "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableEmptyOrderTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, "", "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableDescOrderTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, "desc", "surname");
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void createPageableNullSortByTest() {
        PageRequestDto pageRequestDto = new PageRequestDto(1, 10, "asc", null);
        Pageable pageable = userSearchService.createPageable(pageRequestDto);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void retrieveUsersNullSearchParamsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(new User(), new User());
        Page<User> expectedPage = new PageImpl<>(users);

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<User> result = userSearchService.retrieveUsers(pageable, null);

        assertEquals(expectedPage, result);
    }

    @Test
    public void retrieveUsersEmptySearchParamsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        UserSearchParamsDto searchParamsDto = new UserSearchParamsDto(List.of(), List.of(), List.of());
        List<User> users = List.of(new User(), new User());
        Page<User> expectedPage = new PageImpl<>(users);

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllByIsActiveTrue(pageable)).thenReturn(expectedPage);

        Page<User> result = userSearchService.retrieveUsers(pageable, searchParamsDto);

        assertEquals(expectedPage, result);
    }

    @Test
    public void retrieveUsersWithSearchParamsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        UserSearchParamsDto searchParamsDto = new UserSearchParamsDto(List.of(1L), List.of(1L), List.of(1L));
        List<User> users = List.of(new User(), new User());
        Page<User> expectedPage = new PageImpl<>(users);

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllByContractAndRoleAndLanguageAndIsActive(
                eq(searchParamsDto.contracts()), eq(searchParamsDto.roles()), eq(searchParamsDto.language()), eq(pageable)))
                .thenReturn(expectedPage);

        Page<User> result = userSearchService.retrieveUsers(pageable, searchParamsDto);

        assertEquals(expectedPage, result);
    }
}
