package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.PredefineShiftDto;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.schedule.PredefineShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PredefineShiftServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;
    
    @Mock
    private PredefineShiftRepository predefineShiftRepository;

    @InjectMocks
    private PredefineShiftServiceImpl predefineShiftService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getPredefineShifts()).thenReturn(predefineShiftRepository);
    }
    
    @Test
    public void getByIdTest() {
        long id = 1L;
        PredefineShift predefineShift = new PredefineShift();
        predefineShift.setId(id);
        when(predefineShiftRepository.findById(id)).thenReturn(Optional.of(predefineShift));

        PredefineShiftDto result = predefineShiftService.getById(id);

        assertEquals(id, result.id());
    }

    @Test
    public void getByIdNotFoundTest() {
        long id = 1L;
        when(predefineShiftRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> predefineShiftService.getById(id));
    }

    @Test
    public void getAllTest() {
        PredefineShift predefineShift = PredefineShift.builder().id(1L).build();
        List<PredefineShift> predefineShifts = List.of(predefineShift);
        when(predefineShiftRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(predefineShifts);

        List<PredefineShiftDto> result = predefineShiftService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void createTest() {
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder().id(1L).build();
        PredefineShift predefineShift = PredefineShift.builder().id(1L).build();
        when(predefineShiftRepository.save(any(PredefineShift.class))).thenReturn(predefineShift);

        PredefineShiftDto result = predefineShiftService.create(predefineShiftDto);

        assertNotNull(result);
    }

    @Test
    public void updateByIdTest() {
        long id = 1L;
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder().id(1L).build();
        PredefineShift predefineShift = PredefineShift.builder().id(1L).build();
        when(predefineShiftRepository.findById(id)).thenReturn(Optional.of(predefineShift));
        when(predefineShiftRepository.save(any(PredefineShift.class))).thenReturn(predefineShift);

        PredefineShiftDto result = predefineShiftService.updateById(id, predefineShiftDto);

        assertNotNull(result);
    }

    @Test
    public void deleteByIdTest() {
        long id = 1L;
        doNothing().when(predefineShiftRepository).deleteById(id);

        predefineShiftService.deleteById(id);

        verify(predefineShiftRepository, times(1)).deleteById(id);
    }
}