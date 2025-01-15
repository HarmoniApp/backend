package org.harmoniapp.services.profile;

import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.ContractTypeRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContractTypeServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private ContractTypeRepository contractTypeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContractTypeServiceImpl contractTypeService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getContractTypes()).thenReturn(contractTypeRepository);
    }

    @Test
    public void getByIdTest() {
        long contractTypeId = 1L;
        ContractType contractType = new ContractType(1L, "name", 0);
        contractType.setId(contractTypeId);
        when(contractTypeRepository.findById(contractTypeId)).thenReturn(Optional.of(contractType));

        ContractTypeDto result = contractTypeService.getById(contractTypeId);

        assertEquals(contractTypeId, result.id());
    }

    @Test
    public void getByIdNotFoundTest() {
        long contractTypeId = 1L;
        when(contractTypeRepository.findById(contractTypeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contractTypeService.getById(contractTypeId));
    }

    @Test
    public void getAllTest() {

        ContractType contractType = new ContractType(1L, "name", 0);
        List<ContractType> contractTypes = List.of(contractType);
        when(contractTypeRepository.findAll()).thenReturn(contractTypes);

        List<ContractTypeDto> result = contractTypeService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void createTest() {
        ContractTypeDto contractTypeDto = new ContractTypeDto(0L, "name", 0);
        ContractType contractType = new ContractType(1L, "name", 0);
        when(contractTypeRepository.save(any(ContractType.class))).thenReturn(contractType);

        ContractTypeDto result = contractTypeService.create(contractTypeDto);

        assertNotNull(result);
        assertEquals(contractTypeDto.name(), result.name());
    }

    @Test
    public void updateByIdTest() {
        long contractTypeId = 1L;
        ContractTypeDto contractTypeDto = new ContractTypeDto(1L, "name", 0);
        ContractType contractType = new ContractType(1L, "name", 0);
        when(contractTypeRepository.findById(contractTypeId)).thenReturn(Optional.of(contractType));
        when(contractTypeRepository.save(any(ContractType.class))).thenReturn(contractType);

        ContractTypeDto result = contractTypeService.updateById(contractTypeId, contractTypeDto);

        assertNotNull(result);
    }

    @Test
    public void deleteByIdTest() {
        long contractTypeId = 1L;
        List<User> users = List.of(new User());
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByContractType_Id(contractTypeId)).thenReturn(users);
        when(userRepository.saveAll(anyList())).thenReturn(users);
        when(contractTypeRepository.existsById(contractTypeId)).thenReturn(true);
        doNothing().when(contractTypeRepository).deleteById(contractTypeId);

        contractTypeService.deleteById(contractTypeId);

        verify(contractTypeRepository, times(1)).deleteById(contractTypeId);
    }
}