package org.harmoniapp.harmoniwebapi.utils;


import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PasswordManager {

    public String generateCommonTextPassword() {
        String pwString = generateRandomSpecialCharacters(2)
                .concat(generateRandomNumbers(2))
                .concat(generateRandomAlphabet(2, true))
                .concat(generateRandomAlphabet(2, false))
                .concat(generateRandomCharacters(4));
        List<Character> pwChars = pwString.chars()
                .mapToObj(data -> (char) data)
                .collect(Collectors.toList());
        Collections.shuffle(pwChars);
        String password = pwChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }

    private String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(33, 46).get();
        return generator.generate(length);
    }

    private String generateRandomNumbers(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(48, 57).get();
        return generator.generate(length);
    }

    private String generateRandomAlphabet(int length, boolean isUppercase) {
        int lowerRange;
        int upperRange;
        if (isUppercase) {
            lowerRange = 65;
            upperRange = 90;
        } else {
            lowerRange = 97;
            upperRange = 122;
        }
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(lowerRange, upperRange).get();
        return generator.generate(length);
    }

    private String generateRandomCharacters(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(33, 125).get();
        return generator.generate(length);
    }
}
