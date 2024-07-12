package org.harmoniapp.harmonidata.entities;

import lombok.*;
import org.harmoniapp.harmonidata.enums.Role;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    private User user;
    private Role role;
}
