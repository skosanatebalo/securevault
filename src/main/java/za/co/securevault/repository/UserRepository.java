package za.co.securevault.repository;

import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}