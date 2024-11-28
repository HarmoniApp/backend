package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.RoleDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing roles.
 * Provides methods to retrieve role information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
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
     * @throws RuntimeException if an error occurs during update
     */
    @Transactional
    public RoleDto updateRole(long id, RoleDto roleDto) {
        try {
            Role newRole = roleDto.toEntity();
            return repositoryCollector.getRoles().findById(id)
                    .map(role -> {
                        role.setName(newRole.getName());
                        role.setSup(newRole.isSup());
                        role.setColor(newRole.getColor());
                        Role updatedRole = repositoryCollector.getRoles().save(role);
                        return RoleDto.fromEntity(updatedRole);
                    })
                    .orElseGet(() -> {
                        Role createdRole = repositoryCollector.getRoles().save(newRole);
                        return RoleDto.fromEntity(createdRole);
                    });
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the Role to be deleted
     * @throws RuntimeException if an error occurs during deletion
     */
    public void deleteRole(long id) {
        try {
            repositoryCollector.getRoles().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }
}
