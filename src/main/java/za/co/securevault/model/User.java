package za.co.securevault.model;

import java.time.LocalDateTime;

public class User {

    private long id;
    private String username;
    private String passwordHash;
    private Role role;
    private int failedAttempts;
    private LocalDateTime lockedUntil;

    public User(long id, String username, String passwordHash, Role role,
                int failedAttempts, LocalDateTime lockedUntil) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }
}
