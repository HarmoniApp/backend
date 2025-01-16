package org.harmoniapp.services.importexport;

import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDataServiceImpl userDataService;

    @Test
    public void getAllUsersTest() {
        User user = mock(User.class);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIsActiveTrue(Sort.by("surname", "firstname"))).thenReturn(List.of(user));
        try (MockedStatic<UserDto> mockedStatic = mockStatic(UserDto.class)) {
            mockedStatic.when(() -> UserDto.fromEntity(user)).thenReturn(mock(UserDto.class));
        }
        List<UserDto> result = userDataService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getRolesTest() {
        UserDto user = mock(UserDto.class);
        RoleDto role = new RoleDto(1L, "role", "#000000");
        when(user.roles()).thenReturn(List.of(role));

        String result = userDataService.getRoles(user);

        assertNotNull(result);
        assertEquals("role", result);
    }

    @Test
    public void getLanguagesTest() {
        UserDto user = mock(UserDto.class);
        LanguageDto language = new LanguageDto(1L, "English", "en");
        when(user.languages()).thenReturn(List.of(language));

        String result = userDataService.getLanguages(user);

        assertNotNull(result);
        assertEquals("English", result);
    }

    @Test
    public void getSupervisorEmployeeIdTest() {
        UserDto user = mock(UserDto.class);
        when(user.supervisorId()).thenReturn(1L);
        User supervisor = mock(User.class);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(supervisor));
        when(supervisor.getEmployeeId()).thenReturn("EMP01");

        String result = userDataService.getSupervisorEmployeeId(user);

        assertNotNull(result);
        assertEquals("EMP01", result);
    }

    @Test
    public void getSupervisorEmployeeIdNullTest() {
        UserDto user = mock(UserDto.class);
        when(user.supervisorId()).thenReturn(null);

        String result = userDataService.getSupervisorEmployeeId(user);

        assertNotNull(result);
        assertEquals("", result);
    }
}