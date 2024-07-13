package org.harmoniapp.harmonidata.entities;

import lombok.Data;
import org.harmoniapp.harmonidata.enums.Role;

import java.io.Serializable;

@Data
public class UserRoleId implements Serializable {
    private User user;
    private Role role;
}
