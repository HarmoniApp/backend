package org.harmoniapp.utils;

import java.util.HashMap;
import java.util.Map;

public class LanguageCodeMapper {
    private static final Map<String, String> languageCodeMap = new HashMap<>();

    static {
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

    public static String getApiLanguageCode(String dbLanguageCode) {
        return languageCodeMap.getOrDefault(dbLanguageCode, dbLanguageCode);
    }
}
