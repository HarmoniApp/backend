package org.harmoniapp.services.chat;

import org.harmoniapp.exception.TranslationFailsException;

/**
 * TranslationService provides an interface for translating text to a specified target language.
 */
public interface TranslationService {

    /**
     * Translates the given text to the specified target language.
     *
     * @param text           the text to be translated
     * @param targetLanguage the language to translate the text into
     * @return the translated text
     * @throws TranslationFailsException if an error occurs during translation
     */
    String translate(String text, String targetLanguage) throws TranslationFailsException;
}
