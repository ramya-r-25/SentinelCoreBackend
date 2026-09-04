package org.example.sentinelcorebackend.Service;

import org.example.sentinelcorebackend.Entity.Asset;
import org.example.sentinelcorebackend.Repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthMonitorServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private HealthMonitorService healthMonitorService;

    private Asset criticalCpuAsset;
    private Asset warningMemoryAsset;
    private Asset normalAsset;

    @BeforeEach
    void setUp() {
        criticalCpuAsset = Asset.builder()
                .id(1L)
                .assetName("Database Server")
                .cpuUsage(95.0)
                .memoryUsage(50.0)
                .status("ONLINE")
                .build();

        warningMemoryAsset = Asset.builder()
                .id(2L)
                .assetName("App Server")
                .cpuUsage(50.0)
                .memoryUsage(85.0)
                .status("ONLINE")
                .build();

        normalAsset = Asset.builder()
                .id(3L)
                .assetName("Cache Server")
                .cpuUsage(40.0)
                .memoryUsage(60.0)
                .status("ONLINE")
                .build();
    }

    @Test
    void checkAssetHealth_shouldSetCriticalStatusAndCreateAlert_whenCpuExceedsThreshold() {
        when(assetRepository.findAll()).thenReturn(List.of(criticalCpuAsset));

        healthMonitorService.checkAssetHealth();

        assertEquals("CRITICAL", criticalCpuAsset.getStatus());
        verify(alertService, times(1)).createAlert(
                eq(1L),
                eq("CRITICAL"),
                contains("CPU usage critical: 95.0%")
        );
        verify(assetRepository, times(1)).save(criticalCpuAsset);
    }

    @Test
    void checkAssetHealth_shouldSetWarningStatusAndCreateAlert_whenMemoryExceedsThreshold() {
        when(assetRepository.findAll()).thenReturn(List.of(warningMemoryAsset));

        healthMonitorService.checkAssetHealth();

        assertEquals("WARNING", warningMemoryAsset.getStatus());
        verify(alertService, times(1)).createAlert(
                eq(2L),
                eq("MEDIUM"),
                contains("Memory usage high: 85.0%")
        );
        verify(assetRepository, times(1)).save(warningMemoryAsset);
    }

    @Test
    void checkAssetHealth_shouldSetOnlineStatus_whenMetricsAreNormal() {
        when(assetRepository.findAll()).thenReturn(List.of(normalAsset));

        healthMonitorService.checkAssetHealth();

        assertEquals("ONLINE", normalAsset.getStatus());
        verify(alertService, never()).createAlert(any(), any(), any());
        verify(assetRepository, times(1)).save(normalAsset);
    }
}
