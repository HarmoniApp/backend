package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.Group;

import java.util.List;

public record GroupDto(
        Long id,
        String name,
        List<PartialUserWithEmpIdDto> members
) {
    public static GroupDto fromEntity(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getMembers() != null
                        ? group.getMembers().stream()
                        .map(user -> new PartialUserWithEmpIdDto(user.getId(), user.getFirstname(), user.getSurname(), user.getPhoto(), user.getEmployeeId()))
                        .toList()
                        : List.of());
    }

    public Group toEntity() {
        return new Group(
                this.id,
                this.name,
                null
        );
    }
}
