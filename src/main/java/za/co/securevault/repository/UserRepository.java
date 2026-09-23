package za.co.securevault.repository;

import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final DatabaseManager databaseManager;

    public  UserRepository(DatabaseManager databaseManager){
            this.databaseManager = databaseManager;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = """
                SELECT id, username, password_hash, role
                FROM users
                WHERE username = ?
                """;
        
        try (
            Connection connection = databaseManager.connect();
            PreparedStatement statement = connection.prepareStatement(sql)

        ){
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        Role.valueOf(resultSet.getString("role"))
                    );
                }
                return null;
            }
        }
    }
    public User save(User user) throws SQLException{
        String sql = """
                INSERT INTO users (username, password_hash, role)
                VALUES (?, ?, ?)
                RETURNING id
                """;
        
        try (
            Connection connection = databaseManager.connect();
            PreparedStatement statement = connection.prepareStatement(sql)

        ){
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole().name());

            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    long generatedId = resultSet.getLong("Id");
                    return new User(
                        generatedId, 
                        user.getUsername(), 
                        user.getPasswordHash(), 
                        user.getRole()
                    );
                }
                throw new SQLException("Insert failed, no ID returned.");
            }
        }
    }
        public List<User> findAll() throws SQLException {

        String sql = """
                SELECT id, username, password_hash, role
                FROM users
                ORDER BY id
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = databaseManager.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        Role.valueOf(resultSet.getString("role"))
                ));
            }
        }

        return users;
    }
}