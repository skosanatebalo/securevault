package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @Test 
    void shouldCreateUserCorrectly(){
        User user = new User(1, "Tebalo", "mypassword", Role.ANALYST, 0, null);
    
    assertEquals(1, user.getId());
    assertEquals("Tebalo", user.getUsername());
    assertEquals("mypassword", user.getPasswordHash());
    assertEquals(Role.ANALYST, user.getRole());
    }
}