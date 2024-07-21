package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.harmoniapp.harmoniwebapi.services.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("address")
public class AddressController {
    private final AddressService service;

    @GetMapping
    public List<AddressDto> getAllAddresses() {
        return service.getAllAddresses();
    }

    @GetMapping("/{id}")
    public AddressDto getAddress(@PathVariable long id) {
        return service.getAddressById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto createAddress(@RequestBody AddressDto dto) {
        return service.createAddress(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto updateAddress(@PathVariable long id, @RequestBody AddressDto dto) {
        return service.updateAddress(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable long id) {
        service.deleteAddress(id);
    }
}
