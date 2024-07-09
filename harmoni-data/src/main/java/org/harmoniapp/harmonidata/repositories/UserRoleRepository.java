package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
