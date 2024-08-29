package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
