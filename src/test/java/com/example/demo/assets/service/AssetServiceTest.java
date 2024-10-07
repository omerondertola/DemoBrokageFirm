package com.example.demo.assets.service;

import com.example.demo.assets.AssetRepo;
import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.AssetNames;
import com.example.demo.assets.model.DepositMoneyDto;
import com.example.demo.assets.model.WithdrawMoneyDto;
import com.example.demo.customers.service.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Mock
    private AssetRepo assetRepo;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShallReturnAssetsOfACustomer() {
        // Given
        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(100_000)
                .usableSize(100_000)
                .build();

        Asset kcholAsset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        List<Asset> expectedAssets = List.of(
                tryAsset, kcholAsset);

        // Mock the calls
        Mockito.when(assetRepo.findAllByCustomerId(1))
                .thenReturn(expectedAssets);

        // When
        List<Asset> returnValue = null;

        try {
            returnValue = assetService.getAssetsOfCustomer(1);
        } catch (CustomerNotFoundException e) {
            fail("shall not throw exception");
            e.printStackTrace();
        }

        // Then
        assertNotNull(returnValue);
        checkEquality(tryAsset, returnValue);
    }

    private static void checkEquality(Asset tryAsset, List<Asset> returnValue) {
        assertEquals(tryAsset.getId(), returnValue.get(0).getId());
        assertEquals(tryAsset.getCustomerId(), returnValue.get(0).getCustomerId());
        assertEquals(tryAsset.getAssetName(), returnValue.get(0).getAssetName());
        assertEquals(tryAsset.getSize(), returnValue.get(0).getSize());
        assertEquals(tryAsset.getUsableSize(), returnValue.get(0).getUsableSize());
    }


    @Test
    public void testShallDepositMoney() {
        // Given
        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(100_000)
                .usableSize(100_000)
                .build();

        Asset kcholAsset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset tryAssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(101_000)
                .usableSize(101_000)
                .build();


        List<Asset> expectedAssets = List.of(
                tryAsset, kcholAsset);

        DepositMoneyDto depositMoneyDto = new DepositMoneyDto(1_000);

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(tryAssetExpected);

        // When
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.depositMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(tryAssetExpected.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(remainingAmount);
        assertEquals(101_000, remainingAmount);
    }

    @Test
    public void testShallThrowAssetNotFoundException() {
        // Given
        Asset kcholAsset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        DepositMoneyDto depositMoneyDto = new DepositMoneyDto(1_000);

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.empty());

        // When
        boolean exceptionThrown = false;
        try {
            var remaining = assetService.depositMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            exceptionThrown = true;
        }

        // Then
        assertTrue(exceptionThrown);
    }

    @Test
    public void testShallWithdrawMoney() {
        // Given
        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(100_000)
                .usableSize(100_000)
                .build();

        Asset kcholAsset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset tryAssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(99_000)
                .usableSize(99_000)
                .build();


        List<Asset> expectedAssets = List.of(
                tryAsset, kcholAsset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(1_000,"TR320006400000147790577588");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(tryAssetExpected);

        // When
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception.");
            e.printStackTrace();
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(tryAssetExpected.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(remainingAmount);
        assertEquals(99_000, remainingAmount);
    }

    @Test
    public void testShallThrowNotEnoughMoneyExceptionWhenSo() {
        // Given
        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(999)
                .usableSize(999)
                .build();

        Asset kcholAsset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset tryAssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(999)
                .usableSize(999)
                .build();


        List<Asset> expectedAssets = List.of(
                tryAsset, kcholAsset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(1_000,"TR320006400000147790577588");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(tryAssetExpected);

        // When
        boolean notEnoughMoneyExceptionThrown = false;
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        } catch (NotEnoughMoneyException e) {
            notEnoughMoneyExceptionThrown = true;
            e.printStackTrace();
        }

        // Then
        assertNull(remainingAmount);
        assertTrue(notEnoughMoneyExceptionThrown);
    }

    @Test
    public void testShallNotThrowNotEnoughMoneyExceptionOnBoundary() {
        // Given
        Asset tryAsset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset kcholAsset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset tryAssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(1000)
                .usableSize(0)
                .build();


        List<Asset> expectedAssets = List.of(
                tryAsset, kcholAsset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(1_000,"TR320006400000147790577588");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(tryAsset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(tryAssetExpected);

        // When
        boolean notEnoughMoneyExceptionThrown = false;
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
            notEnoughMoneyExceptionThrown = true;
            e.printStackTrace();
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(tryAssetExpected.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(remainingAmount);
        assertFalse(notEnoughMoneyExceptionThrown);
    }

    @Test
    public void testShallThrowAssetNotFoundExceptionWhileWithdraw() {
        // Given
        Asset kcholAsset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("KCHOL")
                .size(1000)
                .usableSize(1000)
                .build();

        List<Asset> expectedAssets = List.of(kcholAsset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(1_000,"TR320006400000147790577588");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.empty());

        Mockito.when(assetRepo.save(null))
                .thenReturn(null);

        // When
        boolean assetNotFoundExceptionThrown = false;
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            assetNotFoundExceptionThrown = true;
        } catch (NotEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
            e.printStackTrace();
        }

        // Then
        assertNull(remainingAmount);
        assertTrue(assetNotFoundExceptionThrown);
    }


}