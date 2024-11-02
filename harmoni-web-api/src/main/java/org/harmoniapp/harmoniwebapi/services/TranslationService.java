package org.harmoniapp.harmoniwebapi.services;

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.utils.LanguageCodeMapper;
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
    private final String apiKey =  dotenv.get("API_KEY");
    private final String region = "westeurope";
    private final String API_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0";

    public String translate(String text, String targetLanguage) {
        MediaType mediaType = MediaType.get("application/json");

        String requestBodyJson = new Gson().toJson(List.of(Map.of("Text", text)));
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
            JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
            JsonObject responseObject = jsonArray.get(0).getAsJsonObject();
            JsonArray translations = responseObject.getAsJsonArray("translations");
            JsonObject translatedTextObject = translations.get(0).getAsJsonObject();
            return translatedTextObject.get("text").getAsString();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
