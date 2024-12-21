package org.harmoniapp.utils;


import org.apache.commons.text.RandomStringGenerator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for generating random passwords.
 * <p>
 * This component provides methods to generate random passwords with a mix of special characters, numbers, and alphabets.
 * </p>
 */
public class PasswordGenerator {

    /**
     * Generates a common text password with a mix of special characters, numbers, and alphabets.
     *
     * @return a randomly generated password as a {@link String}.
     */
    public static String generateCommonTextPassword() {
        String pwString = generateRandomPassword();
        List<Character> pwChars = convertStringToList(pwString);
        Collections.shuffle(pwChars);
        return pwChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    /**
     * Generates a random password consisting of a mix of special characters, numbers,
     * uppercase and lowercase alphabets, and additional random characters.
     *
     * @return a randomly generated password as a {@link String}.
     */
    private static String generateRandomPassword() {
        return generateRandomSpecialCharacters(2)
                .concat(generateRandomNumbers(2))
                .concat(generateRandomAlphabet(2, true))
                .concat(generateRandomAlphabet(2, false))
                .concat(generateRandomCharacters(4));
    }

    /**
     * Converts a given string to a list of characters.
     *
     * @param pwString the string to convert.
     * @return a list of characters representing the input string.
     */
    private static List<Character> convertStringToList(String pwString) {
        return pwString.chars()
                .mapToObj(data -> (char) data)
                .collect(Collectors.toList());
    }

    /**
     * Generates a random string consisting of special characters.
     *
     * @param length the number of special characters to generate.
     * @return a random string of special characters.
     */
    public static String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(33, 46).get();
        return generator.generate(length);
    }

    /**
     * Generates a random string consisting of numbers.
     *
     * @param length the number of digits to generate.
     * @return a random string of numbers.
     */
    public static String generateRandomNumbers(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(48, 57).get();
        return generator.generate(length);
    }

    /**
     * Generates a random string consisting of alphabetic characters.
     * The method can generate either uppercase or lowercase letters based on the provided flag.
     *
     * @param length the number of alphabets to generate.
     * @param isUppercase {@code true} for uppercase letters, {@code false} for lowercase letters.
     * @return a random string of alphabetic characters.
     */
    public static String generateRandomAlphabet(int length, boolean isUppercase) {
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

    /**
     * Generates a random string consisting of printable ASCII characters.
     *
     * @param length the number of characters to generate.
     * @return a random string of printable ASCII characters.
     */
    public static String generateRandomCharacters(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(33, 125).get();
        return generator.generate(length);
    }
}
