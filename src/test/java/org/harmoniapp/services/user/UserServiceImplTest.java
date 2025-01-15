package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.AddressRepository;
import org.harmoniapp.repositories.profile.ContractTypeRepository;
import org.harmoniapp.repositories.profile.LanguageRepository;
import org.harmoniapp.repositories.profile.RoleRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.services.profile.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordService userPasswordService;

    @Mock
    private ContractTypeRepository contractTypeRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    public void getPageTest() {
        UserSearchParamsDto searchParamsDto = new UserSearchParamsDto(null, null, null);
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userSearchService.findUsersPage(pageRequestDto, searchParamsDto)).thenReturn(userPage);

        PageDto<UserDto> result = userService.getPage(searchParamsDto, pageRequestDto);

        assertEquals(1, result.pageNumber());
        assertEquals(1, result.totalPages());
        assertEquals(1, result.content().size());
    }

    @Test
    public void getTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.get(userId);

        assertEquals(userId, result.id());
    }

    @Test
    public void getUsersSearchTest() {
        String query = "test";
        User user = new User();
        List<User> users = List.of(user);
        when(userSearchService.searchUsers(query)).thenReturn(users);

        List<UserDto> result = userService.getUsersSearch(query);

        assertEquals(1, result.size());
    }

    @Test
    public void createTest() {
        LocalDate now = LocalDate.now();
        UserDto userDto = UserDto.builder()
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .contractType(new ContractTypeDto(1L, null, 0))
                .languages(List.of(new LanguageDto(1L, null, null)))
                .roles(List.of(new RoleDto(1L, null, null)))
                .residence(AddressDto.builder().id(1L).build())
                .workAddress(AddressDto.builder().id(1L).build())
                .build();
        User user = new User();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(repositoryCollector.getContractTypes()).thenReturn(contractTypeRepository);
        when(contractTypeRepository.findById(1L)).thenReturn(Optional.of(new ContractType()));
        when(repositoryCollector.getLanguages()).thenReturn(languageRepository);
        when(languageRepository.findAll()).thenReturn(List.of(new Language(1L, null, null), new Language(2L, null, null)));
        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(roleRepository.findAll()).thenReturn(List.of(new Role(1L, null, null), new Role(2L, null, null)));
        when(repositoryCollector.getAddresses()).thenReturn(addressRepository);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(Address.builder().id(1L).build()));
        when(addressService.saveAddressEntity(any(AddressDto.class))).thenReturn(Address.builder().id(1L).build());
        when(userPasswordService.setPassword(any(User.class))).thenReturn("rawPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
    }

    @Test
    public void deleteTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setIsActive(true);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        assertFalse(user.getIsActive());
    }

    @Test
    public void updateTest() {
        long userId = 1L;
        LocalDate now = LocalDate.now();
        UserDto userDto = UserDto.builder()
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .contractType(new ContractTypeDto(1L, null, 0))
                .build();
        User user = new User();
        ContractType contractType = new ContractType(1L, null, 0);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(repositoryCollector.getContractTypes()).thenReturn(contractTypeRepository);
        when(contractTypeRepository.findById(1L)).thenReturn(Optional.of(contractType));
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(userId, userDto);

        assertNotNull(result);
    }
}
