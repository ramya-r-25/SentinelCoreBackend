package org.example.sentinelcorebackend.Controller;

import org.example.sentinelcorebackend.Dto.AlertDTO;
import org.example.sentinelcorebackend.Entity.Alert;
import org.example.sentinelcorebackend.Service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private AlertDTO alertDTO;

    @BeforeEach
    void setUp() {
        alertDTO = AlertDTO.builder()
                .id(1L)
                .assetId(100L)
                .assetName("Database Host")
                .severity(Alert.AlertSeverity.HIGH)
                .message("High latency detected")
                .status(Alert.AlertStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getOpenAlerts_shouldReturnListOfAlerts() {
        when(alertService.getOpenAlerts()).thenReturn(List.of(alertDTO));

        List<AlertDTO> result = alertController.getOpenAlerts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Database Host", result.get(0).getAssetName());

        verify(alertService, times(1)).getOpenAlerts();
    }

    @Test
    void createAlert_shouldDelegateToAlertService() {
        when(alertService.createAlert(100L, "HIGH", "High latency detected")).thenReturn(alertDTO);

        AlertDTO result = alertController.createAlert(100L, "HIGH", "High latency detected");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Alert.AlertSeverity.HIGH, result.getSeverity());

        verify(alertService, times(1)).createAlert(100L, "HIGH", "High latency detected");
    }

    @Test
    void resolveAlert_shouldDelegateToAlertService() {
        AlertDTO resolvedDTO = AlertDTO.builder()
                .id(1L)
                .status(Alert.AlertStatus.RESOLVED)
                .build();

        when(alertService.resolveAlert(1L)).thenReturn(resolvedDTO);

        AlertDTO result = alertController.resolveAlert(1L);

        assertNotNull(result);
        assertEquals(Alert.AlertStatus.RESOLVED, result.getStatus());

        verify(alertService, times(1)).resolveAlert(1L);
    }
}
