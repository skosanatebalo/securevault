package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.security.PasswordUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordUtilsTest {

    @Test
    void shouldHashAndVerifyCorrectly() {

        String plainPassword = "MySecret123!";

        String hashed = PasswordUtils.hash(plainPassword);

        assertNotEquals(plainPassword, hashed);
        assertTrue(PasswordUtils.verify(plainPassword, hashed));
        assertFalse(PasswordUtils.verify("WrongPassword", hashed));
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {

        String plainPassword = "MySecret123!";

        String hash1 = PasswordUtils.hash(plainPassword);
        String hash2 = PasswordUtils.hash(plainPassword);

        assertNotEquals(hash1, hash2);
    }
}
