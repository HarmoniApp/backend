package org.harmoniapp.services.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.harmoniapp.utils.LanguageCodeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TranslationServiceImplTest {

    @Mock
    private OkHttpClient client;

    @Mock
    private Dotenv dotenv;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TranslationServiceImpl translationService;

    @Test
    public void translateTest() throws IOException {
        String text = "Hello";
        String targetLanguage = "es";
        RequestBody requestBody = mock(RequestBody.class);
        HttpUrl url = mock(HttpUrl.class);
        try (MockedStatic<LanguageCodeMapper> mockedStatic = mockStatic(LanguageCodeMapper.class)) {
            mockedStatic.when(() -> LanguageCodeMapper.getApiLanguageCode(targetLanguage)).thenReturn("es");
        }
        try (MockedStatic<HttpUrl> mockedStatic = mockStatic(HttpUrl.class)) {
            mockedStatic.when(() -> HttpUrl.parse(anyString())).thenReturn(url);
        }
        try (MockedStatic<RequestBody> mockedStatic = mockStatic(RequestBody.class)) {
            mockedStatic.when(() -> RequestBody.create(anyString(), any(MediaType.class))).thenReturn(requestBody);
        }

        String result = translationService.translate(text, targetLanguage);

        assertNotNull(result);
        assertEquals("Hola", result);
    }
}