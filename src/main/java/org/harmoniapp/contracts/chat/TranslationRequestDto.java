package org.harmoniapp.contracts.chat;

import org.springframework.web.bind.annotation.RequestParam;

public record TranslationRequestDto(@RequestParam(defaultValue = "false") boolean translate,
                                    @RequestParam(required = false) String targetLanguage) {
}
