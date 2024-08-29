package org.harmoniapp.harmoniwebapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.harmoniapp.harmoniwebapi.services.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing addresses.
 * Provides endpoints to perform CRUD operations on addresses.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("address")
@CrossOrigin(origins = "http://localhost:3000")
public class AddressController {
    private final AddressService service;

    /**
     * Retrieves all addresses.
     *
     * @return A list of all AddressDto objects.
     */
    @GetMapping
    public List<AddressDto> getAllAddresses() {
        return service.getAllAddresses();
    }

    /**
     * Retrieves all departments.
     *
     * @return A list of all departments.
     */
    @GetMapping("/departments")
    public List<Map<String, Object>> getAllDepartments(){
        return service.getAllDepartments();
    }

    /**
     * Retrieves a specific address by its ID.
     *
     * @param id The ID of the address to retrieve.
     * @return The AddressDto object corresponding to the specified ID.
     */
    @GetMapping("/{id}")
    public AddressDto getAddress(@PathVariable long id) {
        return service.getAddressById(id);
    }

    /**
     * Creates a new address.
     *
     * @param dto The AddressDto object representing the new address.
     * @return The created AddressDto object.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto createAddress(@Valid @RequestBody AddressDto dto) {
        return service.createAddress(dto);
    }

    /**
     * Updates an existing address.
     *
     * @param id  The ID of the address to update.
     * @param dto The AddressDto object containing the updated address data.
     * @return The updated AddressDto object.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto updateAddress(@PathVariable long id, @Valid @RequestBody AddressDto dto) {
        return service.updateAddress(id, dto);
    }

    /**
     * Deletes an address by its ID.
     *
     * @param id The ID of the address to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable long id) {
        service.deleteAddress(id);
    }
}
