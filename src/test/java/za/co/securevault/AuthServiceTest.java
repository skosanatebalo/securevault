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
                Role.VIEWER
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
                Role.VIEWER
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
}
