package com.example.demo.assets;

import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.AssetNames;
import com.example.demo.assets.model.DepositMoneyDto;
import com.example.demo.assets.model.WithdrawMoneyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssetControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private AssetController assetController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testContextLoaded() {

    }

    @Test
    public void testAssetControllerWired() {
        assertThat(assetController).isNotNull();
    }

    @Test
    public void testAdminCanListAllAssets() {
        ResponseEntity<Asset[]> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/assets", Asset[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Asset[] assets = response.getBody();
        assertNotNull(assets);
        assertEquals(4, assets.length);
    }

    @Test
    public void testAdminCanGetAssetsOfOnder() {
        ResponseEntity<Asset[]> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/assets/1", Asset[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Asset[] assets = response.getBody();
        assertNotNull(assets);
        assertEquals(2, assets.length);
        if(assets[0].getId() == 1) {
            assertEqualsTRYAssetOnder(assets[0]);
        }
        if(assets[1].getId() == 1) {
            assertEqualsTRYAssetOnder(assets[1]);
        }
        if(assets[0].getId() == 2) {
            assertEqualsISYATAssetOnder(assets[0]);
        }
        if(assets[1].getId() == 2) {
            assertEqualsISYATAssetOnder(assets[1]);
        }
    }

    @Test
    public void testOnderCanGetAssetsOfOnder() {
        ResponseEntity<Asset[]> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/assets/1", Asset[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Asset[] assets = response.getBody();
        assertNotNull(assets);
        assertEquals(2, assets.length);
        if(assets[0].getId() == 1) {
            assertEqualsTRYAssetOnder(assets[0]);
        }
        if(assets[1].getId() == 1) {
            assertEqualsTRYAssetOnder(assets[1]);
        }
        if(assets[0].getId() == 2) {
            assertEqualsISYATAssetOnder(assets[0]);
        }
        if(assets[1].getId() == 2) {
            assertEqualsISYATAssetOnder(assets[1]);
        }
    }

    @Test
    public void testOnderForbiddenOfDefnesAssets() {
        ResponseEntity<Asset> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/assets/2", Asset.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testOnderForbiddenOfIremsAssets() {
        ResponseEntity<Asset> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/assets/3", Asset.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testAdminCanDepositAndWithdrawMoneyOfOnder() {
        DepositMoneyDto deposit = new DepositMoneyDto(1000.0);
        ResponseEntity<Double> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .postForEntity("http://localhost:" + port +
                        "/apis/v1/assets/deposit/1", deposit, Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(11000.0, response.getBody());

        WithdrawMoneyDto withdraw = new WithdrawMoneyDto(1000.0, "TR320006400000144440777777");
        ResponseEntity<Double> response2 = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .postForEntity("http://localhost:" + port +
                        "/apis/v1/assets/withdraw/1", withdraw, Double.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(10000.0, response2.getBody());
    }

    @Test
    public void testOnderCanDepositAndWithdrawMoneyOfOnder() {
        DepositMoneyDto deposit = new DepositMoneyDto(1000.0);
        ResponseEntity<Double> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .postForEntity("http://localhost:" + port +
                        "/apis/v1/assets/deposit/1", deposit, Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(11000.0, response.getBody());

        WithdrawMoneyDto withdraw = new WithdrawMoneyDto(1000.0, "TR320006400000144440777777");
        ResponseEntity<Double> response2 = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .postForEntity("http://localhost:" + port +
                        "/apis/v1/assets/withdraw/1", withdraw, Double.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(10000.0, response2.getBody());
    }

    @Test
    public void testOnderCannotDepositMoneyOfDefne() {
        DepositMoneyDto deposit = new DepositMoneyDto(1000.0);
        ResponseEntity response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .postForEntity("http://localhost:" + port +
                        "/apis/v1/assets/deposit/2", deposit, Object.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testOnderCannotWithdrawMoneyOfDefne() {
        WithdrawMoneyDto withdraw = new WithdrawMoneyDto(1000.0, "TR320006400000144440777777");
        ResponseEntity response2 = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .postForEntity("http://localhost:" + port +
                        "/apis/v1/assets/withdraw/2", withdraw, Object.class);
        assertEquals(HttpStatus.FORBIDDEN, response2.getStatusCode());
    }


    private void assertEqualsTRYAssetOnder(Asset asset) {
        assertEquals(1, asset.getId());
        assertEquals(1, asset.getCustomerId());
        assertEquals(AssetNames.TRY.name(), asset.getAssetName());
        assertEquals(10000, asset.getSize());
        assertEquals(9882.67, asset.getUsableSize());
    }

    private void assertEqualsISYATAssetOnder(Asset asset) {
        assertEquals(2, asset.getId());
        assertEquals(1, asset.getCustomerId());
        assertEquals("ISYAT", asset.getAssetName());
        assertEquals(10000, asset.getSize());
        assertEquals(10000, asset.getUsableSize());
    }

}