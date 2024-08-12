package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.OfficeDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: Update after DB modify
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class OfficeService {
    private final RepositoryCollector repositories;

    public List<OfficeDto> getAllOffices() {
        List<Long> addressIds = List.of(1L, 2L, 3L, 4L, 5L);
        List<Address> officeAddresses = repositories.getAddresses().findAllById(addressIds);

        return officeAddresses.stream().map(OfficeDto::fromEntity).toList();
    }

    public OfficeDto getOfficeById(Long id) {
        Address officeAddress = repositories.getAddresses().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Office with ID " + id + " not found"));

        return OfficeDto.fromEntity(officeAddress);
    }

    public OfficeDto createOffice(OfficeDto dto) {
        //TODO
        return null;
    }

    public OfficeDto updateOffice(Long id, OfficeDto dto) {
        //TODO
        return null;
    }

    public void deleteOffice(Long id) {
        //TODO
    }
}
