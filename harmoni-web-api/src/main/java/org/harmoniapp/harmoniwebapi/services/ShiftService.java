package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing shifts.
 * Provides methods to retrieve shift information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class ShiftService {
    private final RepositoryCollector repositoryCollector;
    private final int page_size = 5;

    /**
     * Retrieves a ShiftDto for the shift with the specified ID.
     *
     * @param id the ID of the shift to retrieve
     * @return a ShiftDto containing the details of the shift
     * @throws IllegalArgumentException if the shift with the specified ID does not exist
     */
    public ShiftDto getShift(long id) {
        try {
            var shiftOptional = repositoryCollector.getShifts().findById(id);
            if (shiftOptional.isEmpty()) {
                throw new IllegalArgumentException();
            }
            var shift = shiftOptional.get();
            return ShiftDto.fromEntity(shift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

//    /**
//     * Retrieves a paginated list of ShiftDto.
//     *
//     * @param page the page number to retrieve
//     * @return a list of ShiftDto containing the details of shifts for the specified page
//     * @throws RuntimeException if an error occurs while retrieving shift page
//     */
//    public List<ShiftDto> getShiftPage(int page) {
//        try {
//            List<Shift> shifts = repositoryCollector.getShifts().findAll();
//            List<List<Shift>> pagedUsers = Lists.partition(shifts, page_size);
//
//            return pagedUsers.get(page).stream()
//                    .map(ShiftDto::fromEntity)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
//        }
//    }

    public void deleteShift(long id) {
        try {
            repositoryCollector.getShifts().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
