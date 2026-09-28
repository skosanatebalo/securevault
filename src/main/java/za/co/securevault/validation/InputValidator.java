package za.co.securevault.validation;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InputValidator {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");
    private static final Pattern HOSTNAME = Pattern.compile("^[A-Za-z0-9]([A-Za-z0-9.-]{0,253}[A-Za-z0-9])?$");
    private static final Pattern IPV4 = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    private static final int PASSWORD_MIN_LENGTH = 10;
    private static final int PASSWORD_MAX_BYTES = 72;

    private InputValidator() {
    }

    public static void validateUsername(String username) {
        if (username == null || !USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException(
                    "Username must be 3-50 characters: letters, digits, dot, underscore or hyphen.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + PASSWORD_MIN_LENGTH + " characters.");
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > PASSWORD_MAX_BYTES) {
            throw new IllegalArgumentException(
                    "Password must be at most " + PASSWORD_MAX_BYTES + " bytes.");
        }

        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException("Password must contain at least one letter and one digit.");
        }
    }

    public static void validateHostname(String hostname) {
        if (hostname == null || !HOSTNAME.matcher(hostname).matches()) {
            throw new IllegalArgumentException(
                    "Hostname may only contain letters, digits, dots and hyphens (max 255 characters).");
        }
    }

    public static void validateIpv4(String ip) {
        Matcher matcher = ip == null ? null : IPV4.matcher(ip);

        if (matcher == null || !matcher.matches()) {
            throw new IllegalArgumentException("IP address must be a valid IPv4 address, e.g. 192.168.1.10.");
        }

        for (int group = 1; group <= 4; group++) {
            if (Integer.parseInt(matcher.group(group)) > 255) {
                throw new IllegalArgumentException("Each part of an IP address must be between 0 and 255.");
            }
        }
    }

    public static void validateText(String fieldName, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty.");
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must be at most " + maxLength + " characters.");
        }
    }
}
