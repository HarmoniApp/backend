// RoleServiceImplTest.java
package org.harmoniapp.services.profile;

import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.RoleRepository;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private RoleServiceImpl roleService;


    @Test
    public void getByIdTest() {
        long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        RoleDto result = roleService.getById(roleId);

        assertEquals(roleId, result.id());
    }

    @Test
    public void getByIdNotFoundTest() {
        long roleId = 1L;
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.getById(roleId));
    }

    @Test
    public void getUserRolesTest() {
        long userId = 1L;
        Role role = new Role(1L, "name", "#000000");
        Set<Role> roles = Set.of(role);
        User user = new User();
        user.setRoles(roles);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<RoleDto> result = roleService.getUserRoles(userId);

        assertEquals(1, result.size());
    }

    @Test
    public void getAllTest() {
        Role role = new Role(1L, "name", "#000000");
        List<Role> roles = List.of(role);
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findAll(Sort.by("name"))).thenReturn(roles);

        List<RoleDto> result = roleService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void createTest() {
        RoleDto roleDto = new RoleDto(0L, "name", "#000000");
        Role role = new Role(1L, "name", "#000000");
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto result = roleService.create(roleDto);

        assertNotNull(result);
    }

    @Test
    public void updateByIdTest() {
        long roleId = 1L;
        RoleDto roleDto = new RoleDto(1L, "name", "#000000");
        Role role = new Role(1L, "name", "#000000");
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto result = roleService.updateById(roleId, roleDto);

        assertNotNull(result);
    }

    @Test
    public void deleteByIdTest() {
        long roleId = 1L;
        Role role = new Role(1L, "name", "#000000");
        role.setId(roleId);
        Shift shift = Shift.builder().role(role).build();
        User user = User.builder().roles(new HashSet<>()).build();
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteById(roleId);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByRoles_Id(roleId)).thenReturn(List.of(user));
        when(userRepository.saveAll(anyList())).thenReturn(List.of());
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.findByRole_Id(roleId)).thenReturn(List.of(shift));
        when(shiftRepository.saveAll(anyList())).thenReturn(List.of());

        roleService.deleteById(roleId);

        verify(repositoryCollector.getRoles(), times(1)).deleteById(roleId);
    }
}