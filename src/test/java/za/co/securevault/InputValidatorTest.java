package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.validation.InputValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InputValidatorTest {

    @Test
    void shouldAcceptValidUsername() {
        assertDoesNotThrow(() -> InputValidator.validateUsername("tebalo_01"));
        assertDoesNotThrow(() -> InputValidator.validateUsername("j.smith-2"));
    }

    @Test
    void shouldRejectInvalidUsernames() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUsername(null));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUsername(""));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUsername("ab"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUsername("x".repeat(51)));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUsername("has space"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUsername("admin' OR '1'='1"));
    }

    @Test
    void shouldAcceptStrongPassword() {
        assertDoesNotThrow(() -> InputValidator.validatePassword("Secret12345"));
    }

    @Test
    void shouldRejectWeakPasswords() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validatePassword(null));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validatePassword("abc123"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validatePassword("onlyletterspassword"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validatePassword("1234567890"));
    }

    @Test
    void shouldRejectPasswordsOverBcryptLimit() {
        String tooLong = "a1".repeat(37);
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validatePassword(tooLong));
    }

    @Test
    void shouldAcceptValidIpv4() {
        assertDoesNotThrow(() -> InputValidator.validateIpv4("192.168.1.10"));
        assertDoesNotThrow(() -> InputValidator.validateIpv4("0.0.0.0"));
        assertDoesNotThrow(() -> InputValidator.validateIpv4("255.255.255.255"));
    }

    @Test
    void shouldRejectInvalidIpv4() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateIpv4(null));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateIpv4(""));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateIpv4("256.1.1.1"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateIpv4("1.2.3"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateIpv4("1.2.3.4.5"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateIpv4("not-an-ip"));
    }

    @Test
    void shouldValidateHostnames() {
        assertDoesNotThrow(() -> InputValidator.validateHostname("web-server-01"));
        assertDoesNotThrow(() -> InputValidator.validateHostname("db.internal"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateHostname("-bad"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateHostname("has space"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateHostname(""));
    }

    @Test
    void shouldValidateTextFields() {
        assertDoesNotThrow(() -> InputValidator.validateText("Title", "Outdated OpenSSH", 255));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateText("Title", "   ", 255));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateText("Title", "x".repeat(256), 255));
    }
}
