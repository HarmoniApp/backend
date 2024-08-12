package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.OfficeDto;
import org.harmoniapp.harmoniwebapi.services.OfficeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO: Update after DB modify
@RestController
@RequestMapping("/office")
@RequiredArgsConstructor
public class OfficeController {
    private final OfficeService service;

    @GetMapping
    public List<OfficeDto> getAllOffices() {
        return service.getAllOffices();
    }

    @GetMapping("/{id}")
    public OfficeDto getOffice(@PathVariable Long id) {
        return service.getOfficeById(id);
    }

    @PostMapping
    public OfficeDto createOffice(@RequestBody OfficeDto dto) {
        return service.createOffice(dto);
    }

    @PutMapping("/{id}")
    public OfficeDto updateOffice(@PathVariable Long id, @RequestBody OfficeDto dto) {
        return service.updateOffice(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffice(@PathVariable Long id) {
        service.deleteOffice(id);
    }
}
