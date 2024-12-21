package org.harmoniapp.contracts.chat;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data Transfer Object for translation requests.
 *
 * @param translate      whether to translate the text
 * @param targetLanguage the target language for translation, if any
 */
public record TranslationRequestDto(@RequestParam(defaultValue = "false") boolean translate,
                                    @RequestParam(required = false) String targetLanguage) {
}
