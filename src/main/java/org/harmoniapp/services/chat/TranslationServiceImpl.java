package org.harmoniapp.services.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.harmoniapp.exception.TranslationException;
import org.harmoniapp.utils.LanguageCodeMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for handling text translations.
 */
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {
    private final OkHttpClient client = new OkHttpClient();
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();
    private final String apiKey = dotenv.get("API_MS_TRANSLATOR_KEY");
    private final String region = "westeurope";
    private final String API_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Translates the given text to the specified target language.
     *
     * @param text           the text to be translated
     * @param targetLanguage the target language code
     * @return the translated text
     * @throws TranslationException if an error occurs during translation
     */
    @Override
    public String translate(String text, String targetLanguage) throws TranslationException {
        RequestBody body = createRequestBody(text);
        HttpUrl url = parseUrl(targetLanguage);
        Request request = createRequest(url, body);
        String responseBody = sendRequest(request);
        return parseResponse(responseBody);
    }

    /**
     * Creates a JSON request body for the translation API.
     *
     * @param text the text to be translated
     * @return the request body containing the JSON payload
     * @throws TranslationException if an error occurs while creating the JSON request body
     */
    private RequestBody createRequestBody(String text) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(List.of(Map.of("Text", text)));
            MediaType mediaType = MediaType.get("application/json");
            return RequestBody.create(requestBodyJson, mediaType);
        } catch (IOException e) {
            throw new TranslationException("Error creating JSON request body: " + e.getMessage());
        }
    }

    /**
     * Constructs the URL for the translation API request with the specified target language.
     *
     * @param targetLanguage the target language code
     * @return the constructed HttpUrl with the target language query parameter
     */
    private HttpUrl parseUrl(String targetLanguage) {
        targetLanguage = LanguageCodeMapper.getApiLanguageCode(targetLanguage);
        HttpUrl url = HttpUrl.parse(API_URL);
        assert url != null;
        return url.newBuilder()
                .addQueryParameter("to", targetLanguage)
                .build();
    }

    /**
     * Creates an HTTP request for the translation API.
     *
     * @param url  the URL for the translation API
     * @param body the request body containing the JSON payload
     * @return the constructed HTTP request
     */
    private Request createRequest(HttpUrl url, RequestBody body) {
        assert apiKey != null;
        return new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", apiKey)
                .addHeader("Ocp-Apim-Subscription-Region", region)
                .addHeader("Content-type", "application/json")
                .build();
    }

    /**
     * Sends the HTTP request to the translation API and returns the response body as a string.
     *
     * @param request the HTTP request to be sent
     * @return the response body as a string
     * @throws TranslationException if an error occurs during the request
     */
    private String sendRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            throw new TranslationException(e.getMessage());
        }
    }

    /**
     * Parses the JSON response from the translation API.
     *
     * @param responseBody the response body as a JSON string
     * @return the translated text extracted from the JSON response
     * @throws TranslationException if an error occurs while parsing the JSON response
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode jsonArray = objectMapper.readTree(responseBody);
            JsonNode translations = jsonArray.get(0).get("translations");
            return translations.get(0).get("text").asText();
        } catch (IOException e) {
            throw new TranslationException("Error parsing JSON response: " + e.getMessage());
        }
    }
}
