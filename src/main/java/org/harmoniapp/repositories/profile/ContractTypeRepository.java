package org.harmoniapp.repositories.profile;

import org.harmoniapp.entities.profile.ContractType;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractTypeRepository extends JpaRepository<ContractType, Long> {

    @Override
    @Cacheable("contractTypes")
    List<ContractType> findAll();

    boolean existsById(@NotNull Long id);
}
