package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.database.DatabaseManager;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseConnectionTest {

    @Test
    void shouldConnectToDatabase() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();

        Connection connection = databaseManager.connect();

        assertNotNull(connection);

        connection.close();
    }
}