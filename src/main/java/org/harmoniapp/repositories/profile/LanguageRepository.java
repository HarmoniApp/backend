package org.harmoniapp.repositories.profile;

import org.harmoniapp.entities.profile.Language;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    @Override
    @Cacheable(value = "languages")
    List<Language> findAll();

    Optional<Language> findByNameIgnoreCase(String name);
}
