package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.absence.AbsenceTypeDto;
import org.harmoniapp.entities.absence.AbsenceType;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.absence.AbsenceTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbsenceTypeServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private AbsenceTypeRepository absenceTypeRepository;

    @InjectMocks
    private AbsenceTypeServiceImpl absenceTypeService;

    @Test
    public void getAbsenceTypeTest() {
        AbsenceType absenceType = mock(AbsenceType.class);
        when(repositoryCollector.getAbsenceTypes()).thenReturn(absenceTypeRepository);
        when(absenceTypeRepository.findById(1L)).thenReturn(Optional.of(absenceType));
        when(absenceType.getId()).thenReturn(1L);
        when(absenceType.getName()).thenReturn("Sick Leave");

        AbsenceTypeDto result = absenceTypeService.getAbsenceType(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Sick Leave", result.name());
    }

    @Test
    public void getAbsenceTypeNotFoundTest() {
        when(repositoryCollector.getAbsenceTypes()).thenReturn(absenceTypeRepository);
        when(absenceTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> absenceTypeService.getAbsenceType(1L));
    }

    @Test
    public void getAllAbsenceTypesTest() {
        AbsenceType absenceType1 = mock(AbsenceType.class);
        AbsenceType absenceType2 = mock(AbsenceType.class);
        when(repositoryCollector.getAbsenceTypes()).thenReturn(absenceTypeRepository);
        when(absenceTypeRepository.findAll()).thenReturn(List.of(absenceType1, absenceType2));
        when(absenceType1.getId()).thenReturn(1L);
        when(absenceType1.getName()).thenReturn("Sick Leave");
        when(absenceType2.getId()).thenReturn(2L);
        when(absenceType2.getName()).thenReturn("Vacation");

        List<AbsenceTypeDto> result = absenceTypeService.getAllAbsenceTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Sick Leave", result.get(0).name());
        assertEquals(2L, result.get(1).id());
        assertEquals("Vacation", result.get(1).name());
    }
}