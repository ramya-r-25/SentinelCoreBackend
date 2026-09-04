package org.example.sentinelcorebackend.Service;



import org.example.sentinelcorebackend.Entity.Asset;
import org.example.sentinelcorebackend.Repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {


    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetServiceImpl assetService;

    private Asset asset;

    @BeforeEach
    void setUp() {

        asset = Asset.builder()
                .id(1L)
                .assetName("Test Server")
                .assetType("Server")
                .ipAddress("192.168.1.10")
                .location("Chennai")
                .status("ACTIVE")
                .cpuUsage(45.0)
                .memoryUsage(60.0)
                .diskUsage(50.0)
                .networkUsage(30.0)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllAssets_shouldReturnAllAssets() {

        when(assetRepository.findAll())
                .thenReturn(List.of(asset));

        var result = assetService.getAllAssets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Server", result.get(0).getAssetName());

        verify(assetRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnAsset_whenAssetExists() {

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        var result = assetService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Server", result.getAssetName());
        assertEquals("Server", result.getAssetType());

        verify(assetRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowException_whenAssetDoesNotExist() {

        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> assetService.getById(99L)
        );

        assertEquals("Asset not found", exception.getMessage());

        verify(assetRepository, times(1)).findById(99L);
    }

    @Test
    void createAsset_shouldSaveAndReturnAsset() {

        var dto = org.example.sentinelcorebackend.Dto.AssetDTO.builder()
                .assetName("New Server")
                .assetType("Server")
                .ipAddress("192.168.1.20")
                .location("Coimbatore")
                .status("ACTIVE")
                .cpuUsage(30.0)
                .memoryUsage(40.0)
                .diskUsage(20.0)
                .networkUsage(15.0)
                .createdDate(LocalDateTime.now())
                .build();

        Asset savedAsset = Asset.builder()
                .id(2L)
                .assetName("New Server")
                .assetType("Server")
                .ipAddress("192.168.1.20")
                .location("Coimbatore")
                .status("ACTIVE")
                .cpuUsage(30.0)
                .memoryUsage(40.0)
                .diskUsage(20.0)
                .networkUsage(15.0)
                .createdDate(dto.getCreatedDate())
                .build();

        when(assetRepository.save(any(Asset.class)))
                .thenReturn(savedAsset);

        var result = assetService.createAsset(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Server", result.getAssetName());
        assertEquals("Coimbatore", result.getLocation());

        verify(assetRepository, times(1))
                .save(any(Asset.class));
    }

    @Test
    void updateAsset_shouldUpdateAndReturnAsset() {

        var dto = org.example.sentinelcorebackend.Dto.AssetDTO.builder()
                .assetName("Updated Server")
                .assetType("Server")
                .ipAddress("192.168.1.30")
                .location("Bangalore")
                .status("ACTIVE")
                .cpuUsage(55.0)
                .memoryUsage(65.0)
                .diskUsage(70.0)
                .networkUsage(35.0)
                .createdDate(LocalDateTime.now())
                .build();

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        when(assetRepository.save(any(Asset.class)))
                .thenReturn(asset);

        var result = assetService.updateAsset(1L, dto);

        assertNotNull(result);
        assertEquals("Updated Server", result.getAssetName());
        assertEquals("Bangalore", result.getLocation());
        assertEquals(55.0, result.getCpuUsage());

        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void updateAsset_shouldThrowException_whenAssetDoesNotExist() {

        var dto = org.example.sentinelcorebackend.Dto.AssetDTO.builder()
                .assetName("Updated Server")
                .build();

        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> assetService.updateAsset(99L, dto)
        );

        assertEquals("Asset not found", exception.getMessage());

        verify(assetRepository, times(1)).findById(99L);
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void deleteAsset_shouldDeleteAsset_whenAssetExists() {

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        assetService.deleteAsset(1L);

        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).delete(asset);
    }

    @Test
    void deleteAsset_shouldThrowException_whenAssetDoesNotExist() {

        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> assetService.deleteAsset(99L)
        );

        assertEquals("Asset not found", exception.getMessage());

        verify(assetRepository, times(1)).findById(99L);
        verify(assetRepository, never()).delete(any(Asset.class));
    }
}
