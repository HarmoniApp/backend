package org.harmoniapp.harmoniwebapi.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.entities.UserLanguage;
import org.harmoniapp.harmonidata.entities.UserRole;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper implements MapEntityDto<User, UserDto> {
    private final MapEntityDto<Address, AddressDto> addressMapper;

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                user.getEmail(),
                null,
                user.getContractType(),
                user.getContractSignature(),
                user.getContractExpiration(),
                addressMapper.toDto(user.getResidence()),
                addressMapper.toDto(user.getWorkAddress()),
                getSupervisor(user),
                user.getPhoneNumber(),
                user.getEmployeeId(),
                user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()),
                user.getLanguages().stream().map(UserLanguage::getLanguage).collect(Collectors.toSet())
        );
    }

    @Override
    public User toEntity(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.firstname(),
                userDto.surname(),
                userDto.email(),
                userDto.password(),
                userDto.contractType(),
                userDto.contractSignature(),
                userDto.contractExpiration(),
                addressMapper.toEntity(userDto.residence()),
                addressMapper.toEntity(userDto.workAddress()),
                null,
                userDto.phoneNumber(),
                userDto.employeeId(),
                null,
                null
        );
    }

    private long getSupervisor(User user) {
        long response = -1;
        if (user.getSupervisor() != null) {
            response = user.getSupervisor().getId();
        }
        return response;
    }
}
