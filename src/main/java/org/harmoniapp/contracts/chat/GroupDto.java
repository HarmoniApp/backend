package org.harmoniapp.contracts.chat;

import jakarta.validation.constraints.NotEmpty;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.user.User;

import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object for Group.
 *
 * @param id         the unique identifier of the group
 * @param name       the name of the group
 * @param membersIds the IDs of the group members
 */
public record GroupDto(
        Long id,

        @NotEmpty(message = "Nazwa grupy jest wymagana")
        String name,

        @NotEmpty(message = "Grupa musi zawierać co najmniej jednego członka")
        List<Long> membersIds) {

    /**
     * Converts a Group entity to a GroupDto.
     *
     * @param group the Group entity
     * @return the corresponding GroupDto
     */
    public static GroupDto fromEntity(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                getMembersIds(group.getMembers()));
    }

    /**
     * Converts this GroupDto to a Group entity.
     *
     * @param users the set of User entities
     * @return the corresponding Group entity
     */
    public Group toEntity(Set<User> users) {
        return new Group(this.id, this.name, users);
    }

    /**
     * Extracts member ids from a set of User entities.
     *
     * @param members the set of User entities
     * @return the list of member ids
     */
    private static List<Long> getMembersIds(Set<User> members) {
        return members != null
                ? members.stream()
                .map(User::getId)
                .toList()
                : List.of();
    }
}
