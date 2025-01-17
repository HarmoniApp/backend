package org.harmoniapp.controllers.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.services.profile.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing roles.
 * Provides endpoints to retrieve role information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    /**
     * Retrieves a role information by shift ID.
     *
     * @param id the ID of the role to retrieve
     * @return the RoleDto containing the details of the role
     */
    @GetMapping("/{id}")
    public RoleDto getRole(@PathVariable long id) {
        return roleService.getById(id);
    }

    /**
     * Retrieves a list of RoleDto for a specific user by their ID.
     *
     * @param id the ID of the user whose roles are being retrieved
     * @return a list of RoleDto objects representing the user's roles
     */
    @GetMapping("/user/{id}")
    public List<RoleDto> getUserRoles(@PathVariable long id) {
        return roleService.getUserRoles(id);
    }

    /**
     * Retrieves a list of all RoleDto.
     *
     * @return a list of RoleDto containing the details of all roles
     */
    @GetMapping
    public List<RoleDto> getAllRoles() {
        return roleService.getAll();
    }

    /**
     * Creates a new role.
     *
     * @param roleDto the RoleDto containing the details of the role to create
     * @return the created RoleDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto createRole(@Valid @RequestBody RoleDto roleDto) {
        return roleService.create(roleDto);
    }

    /**
     * Updates an existing role or creates a new one if it does not exist.
     *
     * @param id      the ID of the role to update
     * @param roleDto the RoleDto containing the updated details of the role
     * @return the updated or created RoleDto
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto updateRole(@PathVariable long id, @Valid @RequestBody RoleDto roleDto) {
        return roleService.updateById(id, roleDto);
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the role to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable long id) {
        roleService.deleteById(id);
    }
}
