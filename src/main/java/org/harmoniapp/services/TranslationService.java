package org.harmoniapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.utils.LanguageCodeMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class TranslationService {

    private final OkHttpClient client = new OkHttpClient();
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();
    private final String apiKey =  dotenv.get("API_MS_TRANSLATOR_KEY");
    private final String region = "westeurope";
    private final String API_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String translate(String text, String targetLanguage) {
        MediaType mediaType = MediaType.get("application/json");

        String requestBodyJson;
        try {
            requestBodyJson = objectMapper.writeValueAsString(List.of(Map.of("Text", text)));
        } catch (IOException e) {
            throw new RuntimeException("Error creating JSON request body: " +  e.getMessage(), e);
        }

        RequestBody body = RequestBody.create(requestBodyJson, mediaType);

        targetLanguage = LanguageCodeMapper.getApiLanguageCode(targetLanguage);

        HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("to", targetLanguage)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", apiKey)
                .addHeader("Ocp-Apim-Subscription-Region", region)
                .addHeader("Content-type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonArray = objectMapper.readTree(responseBody);
            JsonNode translations = jsonArray.get(0).get("translations");
            return translations.get(0).get("text").asText();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
