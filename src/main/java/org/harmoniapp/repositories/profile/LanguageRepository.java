package org.harmoniapp.repositories.profile;

import org.harmoniapp.entities.profile.Language;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    @NotNull
    @Override
    @Cacheable(value = "languages")
    List<Language> findAll();

    boolean existsByNameIgnoreCase(String name);
}
