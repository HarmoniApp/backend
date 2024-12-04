package org.harmoniapp.contracts;

import jakarta.validation.constraints.NotEmpty;
import org.harmoniapp.entities.Group;
import org.harmoniapp.entities.User;

import java.util.List;
import java.util.Set;

public record GroupDto(
        Long id,

        @NotEmpty(message = "Name is required")
        String name,

        @NotEmpty(message = "Members are required")
        List<Long> membersIds
) {
    public static GroupDto fromEntity(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getMembers() != null
                        ? group.getMembers().stream()
                        .map(User::getId)
                        .toList()
                        : List.of());
    }

    public Group toEntity(Set<User> users) {
        return new Group(
                this.id,
                this.name,
                users
        );
    }
}
