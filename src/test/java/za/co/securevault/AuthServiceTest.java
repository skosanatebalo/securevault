package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;
import za.co.securevault.security.PasswordUtils;
import za.co.securevault.service.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthServiceTest {

    @Test
    void shouldLoginWithCorrectCredentials() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AuthService authService = new AuthService(userRepository);

        String plainPassword = "CorrectHorse123!";
        String username = "authtest_" + System.currentTimeMillis();

        User newUser = new User(
                0,
                username,
                PasswordUtils.hash(plainPassword),
                Role.VIEWER,
                0,
                null
        );

        userRepository.save(newUser);

        Optional<User> result = authService.login(username, plainPassword);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldRejectWrongPassword() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AuthService authService = new AuthService(userRepository);

        String username = "authtest2_" + System.currentTimeMillis();

        User newUser = new User(
                0,
                username,
                PasswordUtils.hash("RealPassword123!"),
                Role.VIEWER,
                0,
                null
        );

        userRepository.save(newUser);

        Optional<User> result = authService.login(username, "WrongPassword");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldRejectUnknownUsername() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AuthService authService = new AuthService(userRepository);

        Optional<User> result = authService.login("no_such_user_xyz", "irrelevant");

        assertFalse(result.isPresent());
    }
    @Test
    void shouldLockAccountAfterFiveFailedAttempts() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AuthService authService = new AuthService(userRepository);

        String username = "lockouttest_" + System.currentTimeMillis();

        User newUser = new User(
                0,
                username,
                PasswordUtils.hash("RealPassword123!"),
                Role.VIEWER,
                0,
                null
        );

        userRepository.save(newUser);

        // 5 wrong attempts should trip the lockout
        for (int i = 0; i < 5; i++) {
            authService.login(username, "WrongPassword");
        }

        // Even the CORRECT password should now be rejected, since the account is locked
        Optional<User> result = authService.login(username, "RealPassword123!");

        assertFalse(result.isPresent());

        // Confirm the lock is actually recorded in the database
        User lockedUser = userRepository.findByUsername(username);
        assertTrue(lockedUser.isLocked());
    }
}
