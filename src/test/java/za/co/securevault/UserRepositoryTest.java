package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}