package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.absence.StatusDto;
import org.harmoniapp.entities.absence.Status;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.absence.StatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusServiceImpl statusService;

    @Test
    public void getAllStatusesTest() {
        Status status1 = new Status(1L, "Status1");
        Status status2 = new Status(2L, "Status2");
        when(repositoryCollector.getStatuses()).thenReturn(statusRepository);
        when(statusRepository.findAll(Sort.by("name"))).thenReturn(List.of(status1, status2));

        List<StatusDto> result = statusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Status1", result.get(0).name());
        assertEquals(2L, result.get(1).id());
        assertEquals("Status2", result.get(1).name());
    }
}