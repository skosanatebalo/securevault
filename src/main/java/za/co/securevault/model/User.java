package za.co.securevault.model;

public class User {

    private long id;
    private String username;
    private String passwordHash;
    private Role role;

    public User(long id, String username, String passwordHAsh, Role role){
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHAsh;
        this.role = role;
    }
    public long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public Role getRole(){
        return role;
    }
}