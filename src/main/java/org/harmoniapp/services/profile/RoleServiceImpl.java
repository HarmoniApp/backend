package org.harmoniapp.services.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.AdminRoleModificationException;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service implementation for managing roles.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RepositoryCollector repositoryCollector;
    private static final String CACHE_NAME = "roles";

    /**
     * Retrieves a RoleDto by its ID.
     *
     * @param id the ID of the role to retrieve
     * @return the RoleDto representing the role
     * @throws EntityNotFoundException if the role with the specified ID is not found
     */
    @Override
    public RoleDto getById(long id) {
        Role role = getRoleById(id);
        return RoleDto.fromEntity(role);
    }

    /**
     * Retrieves the roles of a user by their ID.
     *
     * @param id the ID of the user whose roles are to be retrieved
     * @return a list of RoleDto representing the user's roles
     * @throws EntityNotFoundException if the user with the specified ID is not found
     */
    @Override
    public List<RoleDto> getUserRoles(long id) {
        Set<Role> roles = getUserRoleSet(id);
        return roles.stream()
                .map(RoleDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves all roles sorted by name.
     *
     * @return a list of RoleDto representing all roles
     */
    @Override
    public List<RoleDto> getAll() {
        return repositoryCollector.getRoles()
                .findAll(Sort.by("name"))
                .stream()
                .map(RoleDto::fromEntity)
                .toList();
    }

    /**
     * Creates a new Role.
     * Evicts all entries from the "roles" cache.
     *
     * @param roleDto the RoleDto containing the details of the role to create
     * @return the created RoleDto
     * @throws AdminRoleModificationException if the role is the admin role
     */
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public RoleDto create(RoleDto roleDto) {
        Role role = roleDto.toEntity();
        checkRoleIsAdmin(role);
        Role savedRole = repositoryCollector.getRoles().save(role);
        return RoleDto.fromEntity(savedRole);
    }

    /**
     * Updates an existing Role.
     * If the role with the specified ID is not found, creates a new Role.
     * Evicts all entries from the "roles" cache.
     *
     * @param id      the ID of the role to update
     * @param roleDto the RoleDto containing the updated details of the role
     * @return the updated RoleDto
     * @throws IllegalArgumentException if the role is the admin role
     */
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public RoleDto updateById(long id, RoleDto roleDto) {
        try {
            Role role = getRoleById(id);
            checkRoleIsAdmin(role);
            role = updateRole(role, roleDto);
            return RoleDto.fromEntity(role);
        } catch (EntityNotFoundException e) {
            return create(roleDto);
        }
    }

    /**
     * Deletes a Role by its ID.
     * Evicts all entries from the cacheName cache.
     *
     * @param id the ID of the role to delete
     * @throws EntityNotFoundException                 if the role with the specified ID is not found
     * @throws AdminRoleModificationException if the role is the admin role
     */
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteById(long id) {
        Role role = getRoleById(id);
        checkRoleIsAdmin(role);
        removeRoleFromUsers(role);
        removeRoleFromShifts(role);
        repositoryCollector.getRoles().deleteById(role.getId());
    }

    /**
     * Retrieves a Role entity by its ID.
     *
     * @param id the ID of the role to retrieve
     * @return the Role entity
     * @throws EntityNotFoundException if the role with the specified ID is not found
     */
    private Role getRoleById(long id) {
        return repositoryCollector.getRoles().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono roli o ID: %d".formatted(id)));
    }

    /**
     * Retrieves the set of roles for a user by their ID.
     *
     * @param id the ID of the user whose roles are to be retrieved
     * @return a set of Role entities representing the user's roles
     * @throws EntityNotFoundException if the user with the specified ID is not found
     */
    private Set<Role> getUserRoleSet(long id) {
        return repositoryCollector.getUsers()
                .findById(id)
                .map(User::getRoles)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o ID: %d".formatted(id)));
    }

    /**
     * Checks if the given role is the admin role.
     * Throws an exception if the role is the admin role.
     *
     * @param role the Role to check
     * @throws AdminRoleModificationException if the role is the admin role
     */
    private void checkRoleIsAdmin(Role role) {
        if (role.getName().equalsIgnoreCase("admin")) {
            throw new AdminRoleModificationException("Nie można edytować roli admin");
        }
    }

    /**
     * Updates the given role with the details from the provided RoleDto.
     *
     * @param role    the existing Role to update
     * @param roleDto the RoleDto containing the updated details
     * @return the updated Role entity
     */
    private Role updateRole(Role role, RoleDto roleDto) {
        Role newRole = roleDto.toEntity();
        newRole.setId(role.getId());
        return repositoryCollector.getRoles().save(newRole);
    }

    /**
     * Removes the specified role from all users who have it.
     *
     * @param role the Role to be removed from users
     */
    private void removeRoleFromUsers(Role role) {
        List<User> users = repositoryCollector.getUsers().findByRoles_Id(role.getId());
        if (!users.isEmpty()) {
            users.forEach((user -> user.getRoles().remove(role)));
            repositoryCollector.getUsers().saveAll(users);
        }
    }

    /**
     * Removes the specified role from all shifts that have it.
     *
     * @param role the Role to be removed from shifts
     */
    private void removeRoleFromShifts(Role role) {
        List<Shift> shifts = repositoryCollector.getShifts().findByRole_Id(role.getId());
        if (!shifts.isEmpty()) {
            shifts.forEach((shift -> shift.setRole(null)));
            repositoryCollector.getShifts().saveAll(shifts);
        }
    }
}
