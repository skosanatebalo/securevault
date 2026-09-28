package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Asset;
import za.co.securevault.repository.AssetRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssetRepositoryTest {

    @Test
    void shouldSaveFindAndDeleteAsset() throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        AssetRepository assetRepository = new AssetRepository(databaseManager);

        String hostname = "test-host-" + System.currentTimeMillis();

        Asset newAsset = new Asset(
                0,
                hostname,
                "10.0.0.5",
                "Ubuntu",
                "IT"
        );

        Asset saved = assetRepository.save(newAsset);
        assertTrue(saved.getId() > 0);

        List<Asset> allAssets = assetRepository.findAll();
        assertTrue(allAssets.stream().anyMatch(a -> a.getHostname().equals(hostname)));

        boolean deleted = assetRepository.deleteById(saved.getId());
        assertTrue(deleted);

        List<Asset> afterDelete = assetRepository.findAll();
        assertTrue(afterDelete.stream().noneMatch(a -> a.getHostname().equals(hostname)));
    }
}
