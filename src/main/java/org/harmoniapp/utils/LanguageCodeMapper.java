package org.harmoniapp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping database language codes to API language codes.
 */
public class LanguageCodeMapper {
    private static final Map<String, String> languageCodeMap = new HashMap<>();

    static {
        // Initialize the language code map with predefined mappings
        languageCodeMap.put("ae", "ar");
        languageCodeMap.put("bd", "bn");
        languageCodeMap.put("gb", "en");
        languageCodeMap.put("fr", "fr");
        languageCodeMap.put("de", "de");
        languageCodeMap.put("in", "hi");
        languageCodeMap.put("it", "it");
        languageCodeMap.put("jp", "ja");
        languageCodeMap.put("kr", "ko");
        languageCodeMap.put("cn", "zh-Hans");
        languageCodeMap.put("ir", "fa");
        languageCodeMap.put("pl", "pl");
        languageCodeMap.put("pt", "pt");
        languageCodeMap.put("ru", "ru");
        languageCodeMap.put("es", "es");
        languageCodeMap.put("tr", "tr");
        languageCodeMap.put("vn", "vi");
    }

    /**
     * Retrieves the Microsoft Translator API language code corresponding to the given database language code.
     *
     * @param dbLanguageCode the database language code
     * @return the corresponding Microsoft Translator API language code, or the original code if no mapping is found
     */
    public static String getApiLanguageCode(String dbLanguageCode) {
        return languageCodeMap.getOrDefault(dbLanguageCode, dbLanguageCode);
    }
}
