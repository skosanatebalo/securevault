package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class UserRepositoryTest {

    @Test
    void shouldFindUserByUsername() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();

        UserRepository userRepository =
                new UserRepository(databaseManager);

        User user =
                userRepository.findByUsername("admin");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals(Role.ADMIN, user.getRole());
    }
        @Test
    void shouldSaveAndFindAllUsers() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);

        User newUser = new User(
                0,
                "testuser_" + System.currentTimeMillis(),
                "somehash",
                Role.VIEWER,
                0,
                null
        );

        User saved = userRepository.save(newUser);

        assertTrue(saved.getId() > 0);

        List<User> allUsers = userRepository.findAll();

        assertTrue(allUsers.stream()
                .anyMatch(u -> u.getUsername().equals(newUser.getUsername())));
    }
}