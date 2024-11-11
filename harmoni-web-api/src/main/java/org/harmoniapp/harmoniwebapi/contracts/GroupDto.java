package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.Group;
import org.harmoniapp.harmonidata.entities.User;

import java.util.List;
import java.util.Set;

public record GroupDto(
        Long id,
        String name,
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
