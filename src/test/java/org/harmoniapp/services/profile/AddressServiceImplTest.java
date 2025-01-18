package org.harmoniapp.services.profile;

import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.DepartmentDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.AddressRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getAddresses()).thenReturn(addressRepository);
    }

    @Test
    public void getByIdTest() {
        long addressId = 1L;
        Address address = new Address();
        address.setId(addressId);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        AddressDto result = addressService.getById(addressId);

        assertEquals(addressId, result.id());
    }

    @Test
    public void getAllTest() {
        Address address = Address.builder().id(1L).build();
        List<Address> addresses = List.of(address);
        when(addressRepository.findAll()).thenReturn(addresses);

        List<AddressDto> result = addressService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void getAllDepartmentsTest() {
        Address address = Address.builder().id(1L).build();
        address.setDepartmentName("Department");
        List<Address> addresses = List.of(address);
        when(addressRepository.findByDepartmentNameNotNull()).thenReturn(addresses);

        List<DepartmentDto> result = addressService.getAllDepartments();

        assertEquals(1, result.size());
    }

    @Test
    public void getAllDepartmentsAddressTest() {
        Address address = Address.builder().id(1L).build();
        address.setDepartmentName("Department");
        List<Address> addresses = List.of(address);
        when(addressRepository.findByDepartmentNameNotNull()).thenReturn(addresses);

        List<AddressDto> result = addressService.getAllDepartmentsAddress();

        assertEquals(1, result.size());
    }

    @Test
    public void createTest() {
        AddressDto addressDto = AddressDto.builder().id(1L).build();
        Address address = Address.builder().id(1L).build();
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.create(addressDto);

        assertNotNull(result);
    }

    @Test
    public void saveAddressEntityTest() {
        AddressDto addressDto = AddressDto.builder().build();
        Address address = new Address();
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        Address result = addressService.saveAddressEntity(addressDto);

        assertNotNull(result);
    }

    @Test
    public void updateByIdTest() {
        long addressId = 1L;
        AddressDto addressDto = AddressDto.builder().build();
        Address address = Address.builder().id(1L).build();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.updateById(addressId, addressDto);

        assertNotNull(result);
    }

    @Test
    public void updateTest() {
        Address existingAddress = new Address();
        AddressDto addressDto = AddressDto.builder().build();
        Address address = new Address();
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        Address result = addressService.update(existingAddress, addressDto);

        assertNotNull(result);
    }

    @Test
    public void deleteByIdTest() {
        long addressId = 1L;
        long userId = 1L;
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByResidence_IdOrWorkAddress_Id(userId)).thenReturn(List.of(new User()));
        when(addressRepository.existsById(addressId)).thenReturn(true);
        doNothing().when(addressRepository).deleteById(addressId);

        addressService.deleteById(addressId);

        verify(addressRepository, times(1)).deleteById(addressId);
    }
}
