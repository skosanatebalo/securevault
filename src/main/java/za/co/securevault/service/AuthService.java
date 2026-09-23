package za.co.securevault.service;

import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;
import za.co.securevault.security.PasswordUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String username, String plainPassword) throws SQLException {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return Optional.empty();
        }

        if (user.isLocked()) {
            return Optional.empty();
        }

        if (!PasswordUtils.verify(plainPassword, user.getPasswordHash())) {
            handleFailedAttempt(user);
            return Optional.empty();
        }

        if (user.getFailedAttempts() > 0 || user.getLockedUntil() != null) {
            userRepository.resetFailedAttempts(user.getId());
        }

        return Optional.of(user);
    }

    private void handleFailedAttempt(User user) throws SQLException {

        int newFailedAttempts = user.getFailedAttempts() + 1;

        LocalDateTime lockedUntil = null;

        if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockedUntil = LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES);
        }

        userRepository.recordFailedAttempt(user.getId(), newFailedAttempts, lockedUntil);
    }
}

