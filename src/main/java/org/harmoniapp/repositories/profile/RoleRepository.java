package org.harmoniapp.repositories.profile;

import org.harmoniapp.entities.profile.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
