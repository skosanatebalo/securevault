package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.model.Asset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssetTest {

    @Test
    void shouldCreateAssetCorrectly() {

        Asset asset = new Asset(
                1,
                "web-server-01",
                "192.168.1.10",
                "Ubuntu",
                "IT"
        );

        assertEquals(1, asset.getId());
        assertEquals("web-server-01", asset.getHostname());
        assertEquals("192.168.1.10", asset.getIpAddress());
        assertEquals("Ubuntu", asset.getOperatingSystem());
        assertEquals("IT", asset.getOwner());
    }
}
