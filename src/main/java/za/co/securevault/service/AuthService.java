package za.co.securevault.service;
import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;
import za.co.securevault.security.PasswordUtils;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public Optional<User> login(String username, String plainPassword) throws SQLException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        if (!PasswordUtils.verify(plainPassword, user.getPasswordHash())){
            return Optional.empty();
    }
    return Optional.of(user);
    }
}