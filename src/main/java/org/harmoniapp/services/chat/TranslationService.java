package org.harmoniapp.services.chat;

import org.harmoniapp.exception.TranslationException;

public interface TranslationService {
    String translate(String text, String targetLanguage) throws TranslationException;
}
