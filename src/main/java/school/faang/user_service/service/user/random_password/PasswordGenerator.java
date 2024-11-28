package school.faang.user_service.service.user.random_password;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

@Component
public class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private static final String ALL = UPPER + LOWER + DIGITS + PUNCTUATION;

    private final Random random = new SecureRandom();

    public String generatePassword(int length, boolean useLower, boolean useUpper, boolean useDigits, boolean usePunctuation) {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid password length");
        }

        StringBuilder password = new StringBuilder(length);
        String charCategories = (useLower ? LOWER : "") +
                (useUpper ? UPPER : "") +
                (useDigits ? DIGITS : "") +
                (usePunctuation ? PUNCTUATION : "");

        if (useUpper) {
            password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        }
        if (useLower) {
            password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        }
        if (useDigits) {
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        if (usePunctuation) {
            password.append(PUNCTUATION.charAt(random.nextInt(PUNCTUATION.length())));
        }

        for (int i = password.length(); i < length; i++) {
            password.append(charCategories.charAt(random.nextInt(charCategories.length())));
        }

        return new String(password);
    }
}
