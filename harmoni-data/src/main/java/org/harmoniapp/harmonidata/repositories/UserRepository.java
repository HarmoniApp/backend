package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
