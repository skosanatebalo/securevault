package za.co.securevault.repository;

import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Asset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetRepository {

    private final DatabaseManager databaseManager;

    public AssetRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Asset> findAll() throws SQLException {

        String sql = """
                SELECT id, hostname, ip_address, operating_system, owner
                FROM assets
                ORDER BY id
                """;

        List<Asset> assets = new ArrayList<>();

        try (
                Connection connection = databaseManager.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                assets.add(new Asset(
                        resultSet.getLong("id"),
                        resultSet.getString("hostname"),
                        resultSet.getString("ip_address"),
                        resultSet.getString("operating_system"),
                        resultSet.getString("owner")
                ));
            }
        }

        return assets;
    }

    public Asset save(Asset asset) throws SQLException {

        String sql = """
                INSERT INTO assets (hostname, ip_address, operating_system, owner)
                VALUES (?, ?::inet, ?, ?)
                RETURNING id
                """;

        try (
                Connection connection = databaseManager.connect();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, asset.getHostname());
            statement.setString(2, asset.getIpAddress());
            statement.setString(3, asset.getOperatingSystem());
            statement.setString(4, asset.getOwner());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    long generatedId = resultSet.getLong("id");
                    return new Asset(
                            generatedId,
                            asset.getHostname(),
                            asset.getIpAddress(),
                            asset.getOperatingSystem(),
                            asset.getOwner()
                    );
                }

                throw new SQLException("Insert failed, no ID returned.");
            }
        }
    }

    public boolean deleteById(long id) throws SQLException {

        String sql = "DELETE FROM assets WHERE id = ?";

        try (
                Connection connection = databaseManager.connect();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
