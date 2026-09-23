package za.co.securevault.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL =
            "jdbc:postgresql://localhost:5432/securevault";

    private static final String DB_USER =
            "securevault_user";

    private static final String DB_PASSWORD =
            "mypassword";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
        );
    }
}