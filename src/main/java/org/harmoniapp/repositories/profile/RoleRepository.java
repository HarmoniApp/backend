package org.harmoniapp.repositories.profile;

import org.harmoniapp.entities.profile.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @NotNull
    @Override
    @Cacheable(value = "roles")
    List<Role> findAll();

    Role findByName(String name);
}
