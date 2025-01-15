// LanguageServiceImplTest.java
package org.harmoniapp.services.profile;

import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.LanguageRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getLanguages()).thenReturn(languageRepository);
    }

    @Test
    public void getAllTest() {
        Language language = new Language();
        List<Language> languages = List.of(language);
        when(languageRepository.findAll(Sort.by("name"))).thenReturn(languages);

        List<LanguageDto> result = languageService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void getByIdTest() {
        long languageId = 1L;
        Language language = new Language();
        language.setId(languageId);
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));

        LanguageDto result = languageService.getById(languageId);

        assertEquals(languageId, result.id());
    }

    @Test
    public void getByIdNotFoundTest() {
        long languageId = 1L;
        when(languageRepository.findById(languageId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> languageService.getById(languageId));
    }

    @Test
    public void createTest() {
        LanguageDto languageDto = new LanguageDto(0L, "name", "code");
        Language language = new Language();
        when(languageRepository.existsByNameIgnoreCase(languageDto.name())).thenReturn(false);
        when(languageRepository.save(any(Language.class))).thenReturn(language);

        LanguageDto result = languageService.create(languageDto);

        assertNotNull(result);
    }

    @Test
    public void updateByIdTest() {
        long languageId = 1L;
        LanguageDto languageDto = new LanguageDto(1L, "name", "code");
        Language language = new Language();
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));
        when(languageRepository.existsByNameIgnoreCase(languageDto.name())).thenReturn(false);
        when(languageRepository.save(any(Language.class))).thenReturn(language);

        LanguageDto result = languageService.updateById(languageId, languageDto);

        assertNotNull(result);
    }

    @Test
    public void DeleteByIdTest() {
        long languageId = 1L;
        Language language = new Language();
        language.setId(languageId);
        User user = User.builder().languages(new HashSet<>()).build();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByLanguages_Id(languageId)).thenReturn(List.of(user));
        when(userRepository.saveAll(anyList())).thenReturn(List.of(user));
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));
        doNothing().when(languageRepository).deleteById(languageId);

        languageService.deleteById(languageId);

        verify(repositoryCollector.getLanguages(), times(1)).deleteById(languageId);
    }
}