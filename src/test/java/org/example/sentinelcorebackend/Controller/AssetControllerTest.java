package org.example.sentinelcorebackend.Controller;

import org.example.sentinelcorebackend.Dto.AssetDTO;
import org.example.sentinelcorebackend.Dto.DashboardSummaryDTO;
import org.example.sentinelcorebackend.Service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    private AssetDTO assetDTO;

    @BeforeEach
    void setUp() {
        assetDTO = AssetDTO.builder()
                .assetName("App Node 1")
                .assetType("Container")
                .ipAddress("10.0.0.5")
                .location("Bangalore")
                .status("ONLINE")
                .cpuUsage(12.5)
                .memoryUsage(45.0)
                .build();
    }

    @Test
    void getAssets_shouldReturnAllAssets() {
        when(assetService.getAllAssets()).thenReturn(List.of(assetDTO));

        List<AssetDTO> result = assetController.getAssets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("App Node 1", result.get(0).getAssetName());

        verify(assetService, times(1)).getAllAssets();
    }

    @Test
    void searchAssets_shouldReturnFilteredAssets() {
        when(assetService.searchAndFilter("App", "ONLINE")).thenReturn(List.of(assetDTO));

        List<AssetDTO> result = assetController.searchAssets("App", "ONLINE");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(assetService, times(1)).searchAndFilter("App", "ONLINE");
    }

    @Test
    void getDashboardSummary_shouldReturnSummary() {
        DashboardSummaryDTO summaryDTO = new DashboardSummaryDTO(10L, 99.5, 35.0, 1L);

        when(assetService.getDashboardSummary()).thenReturn(summaryDTO);

        DashboardSummaryDTO result = assetController.getDashboardSummary();

        assertNotNull(result);
        assertEquals(10L, result.getTotalAssets());
        assertEquals(99.5, result.getUptimePercentage());
        assertEquals(35.0, result.getAvgCpuUsage());
        assertEquals(1L, result.getCriticalAlerts());

        verify(assetService, times(1)).getDashboardSummary();
    }

    @Test
    void createAsset_shouldReturnCreatedAsset() {
        when(assetService.createAsset(assetDTO)).thenReturn(assetDTO);

        AssetDTO result = assetController.createAsset(assetDTO);

        assertNotNull(result);
        assertEquals("App Node 1", result.getAssetName());

        verify(assetService, times(1)).createAsset(assetDTO);
    }

    @Test
    void getAssetById_shouldReturnAsset() {
        when(assetService.getById(5L)).thenReturn(assetDTO);

        AssetDTO result = assetController.getAssetById(5L);

        assertNotNull(result);
        assertEquals("Bangalore", result.getLocation());

        verify(assetService, times(1)).getById(5L);
    }

    @Test
    void updateAsset_shouldReturnUpdatedAsset() {
        when(assetService.updateAsset(5L, assetDTO)).thenReturn(assetDTO);

        AssetDTO result = assetController.updateAsset(5L, assetDTO);

        assertNotNull(result);
        assertEquals("10.0.0.5", result.getIpAddress());

        verify(assetService, times(1)).updateAsset(5L, assetDTO);
    }

    @Test
    void deleteAsset_shouldCallServiceAndDelete() {
        doNothing().when(assetService).deleteAsset(5L);

        String message = assetController.deleteAsset(5L);

        assertEquals("Asset deleted successfully", message);
        verify(assetService, times(1)).deleteAsset(5L);
    }
}
