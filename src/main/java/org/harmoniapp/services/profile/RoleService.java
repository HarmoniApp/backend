package org.harmoniapp.services.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing roles.
 * Provides methods to retrieve role information.
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a RoleDto for the role with the specified ID.
     *
     * @param id the ID of the role to retrieve
     * @return a RoleDto containing the details of the role
     * @throws IllegalArgumentException if the role with the specified ID does not exist
     */
    public RoleDto getRole(long id) {
            try {
                Role role = repositoryCollector.getRoles().findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found"));

                return RoleDto.fromEntity(role);
            } catch (Exception e) {
                throw new RuntimeException("An error occurred: " + e.getMessage(), e);
            }
    }

    /**
     * Retrieves a list of RoleDto associated with a specific user.
     *
     * @param id the ID of the user whose roles are being retrieved
     * @return a list of RoleDto objects representing the user's roles
     * @throws IllegalArgumentException if the user with the specified ID is not found
     */
    public List<RoleDto> getUserRoles(long id) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getRoles().stream()
                .map(RoleDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a list of all RoleDto.
     *
     * @return a list of RoleDto containing the details of all roles
     * @throws RuntimeException if an error occurs while retrieving roles
     */
    public List<RoleDto> getAllRoles() {
        try {
            var role = repositoryCollector.getRoles().findAll(Sort.by("name"));
            return role.stream()
                    .filter(r -> !r.getName().equals("admin"))
                    .map(RoleDto::fromEntity)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new role to the database.
     *
     * @param roleDto the RoleDto containing the details of the role to create
     * @return the created RoleDto
     * @throws RuntimeException if an error occurs during creation
     */
    @CacheEvict(value = "roles", allEntries = true)
    public RoleDto createRole(RoleDto roleDto) {
        try {
            Role role = roleDto.toEntity();
            Role savedRole = repositoryCollector.getRoles().save(role);
            return RoleDto.fromEntity(savedRole);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing Role or creates a new one if it does not exist.
     *
     * @param id the ID of the role to update
     * @param roleDto the RoleDto containing the updated details of the role
     * @return the updated or created RoleDto
     * @throws IllegalArgumentException if the role to update is the admin role
     * @throws RuntimeException if an error occurs during update
     */
    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public RoleDto updateRole(long id, RoleDto roleDto) {
        Optional<Role> optionalRole = repositoryCollector.getRoles().findById(id);
        Role newRole = roleDto.toEntity();
        if (optionalRole.isEmpty()) {
            newRole = repositoryCollector.getRoles().save(newRole);
            return RoleDto.fromEntity(newRole);
        }
        Role role = optionalRole.get();
        if (role.getName().equals("admin")) {
            throw new IllegalArgumentException("Cannot update the admin role");
        }
        newRole.setId(role.getId());
        newRole = repositoryCollector.getRoles().save(newRole);
        return RoleDto.fromEntity(newRole);
    }

    /**
     * Deletes a role by its ID.
     * If the role is associated with any users, it removes the role from those users before deleting it.
     *
     * @param id the ID of the Role to be deleted
     * @throws IllegalArgumentException if the role with the specified ID does not exist or if the role is the admin role
     */
    @CacheEvict(value = "roles", allEntries = true)
    public void deleteRole(long id) {
        Role role = repositoryCollector.getRoles().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        if (role.getName().equals("admin")) {
            throw new IllegalArgumentException("Cannot update the admin role");
        }

        List<User> users = repositoryCollector.getUsers().findByRoles_Id(id);
        if(!users.isEmpty()) {
            users.forEach((user -> user.getRoles().remove(role)));
            repositoryCollector.getUsers().saveAll(users);
        }

        List<Shift> shifts = repositoryCollector.getShifts().findByRole_Id(id);
        if(!shifts.isEmpty()) {
            shifts.forEach((shift -> shift.setRole(null)));
            repositoryCollector.getShifts().saveAll(shifts);
        }

        repositoryCollector.getRoles().deleteById(id);
    }
}
