package org.example.sentinelcorebackend.Service;

import org.example.sentinelcorebackend.Dto.AlertDTO;
import org.example.sentinelcorebackend.Entity.Alert;
import org.example.sentinelcorebackend.Entity.Asset;
import org.example.sentinelcorebackend.Repository.AlertRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TwilioSmsService twilioSmsService;

    @InjectMocks
    private AlertService alertService;

    private Asset asset;
    private Alert openAlert;

    @BeforeEach
    void setUp() {
        asset = Asset.builder()
                .id(1L)
                .assetName("Web Server 01")
                .assetType("Server")
                .ipAddress("192.168.1.100")
                .location("Chennai")
                .status("ACTIVE")
                .build();

        openAlert = Alert.builder()
                .id(10L)
                .asset(asset)
                .severity(Alert.AlertSeverity.CRITICAL)
                .message("High CPU usage")
                .status(Alert.AlertStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createAlert_shouldCreateAndSendNotifications_whenSeverityIsCritical() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert a = invocation.getArgument(0);
            a.setId(10L);
            return a;
        });

        AlertDTO result = alertService.createAlert(1L, "CRITICAL", "High CPU usage");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getAssetId());
        assertEquals("Web Server 01", result.getAssetName());
        assertEquals(Alert.AlertSeverity.CRITICAL, result.getSeverity());
        assertEquals(Alert.AlertStatus.OPEN, result.getStatus());

        verify(notificationService, times(1)).sendAlertEmail(
                eq("ramyaravin2006@gmail.com"),
                eq("Web Server 01"),
                eq("CRITICAL"),
                eq("High CPU usage")
        );
        verify(twilioSmsService, times(1)).sendSms(
                eq("+917540067926"),
                contains("Web Server 01")
        );
    }

    @Test
    void createAlert_shouldCreateWithoutNotifications_whenSeverityIsLow() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert a = invocation.getArgument(0);
            a.setId(11L);
            return a;
        });

        AlertDTO result = alertService.createAlert(1L, "LOW", "Minor info log");

        assertNotNull(result);
        assertEquals(11L, result.getId());
        assertEquals(Alert.AlertSeverity.LOW, result.getSeverity());

        verify(notificationService, never()).sendAlertEmail(anyString(), anyString(), anyString(), anyString());
        verify(twilioSmsService, never()).sendSms(anyString(), anyString());
    }

    @Test
    void createAlert_shouldThrowException_whenAssetDoesNotExist() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> alertService.createAlert(99L, "CRITICAL", "High CPU")
        );

        assertTrue(exception.getMessage().contains("Asset not found: 99"));
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void resolveAlert_shouldUpdateStatusToResolved_whenAlertExists() {
        when(alertRepository.findById(10L)).thenReturn(Optional.of(openAlert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertDTO result = alertService.resolveAlert(10L);

        assertNotNull(result);
        assertEquals(Alert.AlertStatus.RESOLVED, result.getStatus());
        assertNotNull(result.getResolvedAt());

        verify(alertRepository, times(1)).findById(10L);
        verify(alertRepository, times(1)).save(openAlert);
        verify(notificationService, times(1)).sendAlertResolvedEmail(
                eq("ramyaravin2006@gmail.com"),
                eq("Web Server 01"),
                eq("CRITICAL"),
                eq("High CPU usage")
        );
    }

    @Test
    void resolveAlert_shouldThrowException_whenAlertDoesNotExist() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> alertService.resolveAlert(99L)
        );

        assertTrue(exception.getMessage().contains("Alert not found: 99"));
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void getOpenAlerts_shouldReturnListOfOpenAlerts() {
        when(alertRepository.findByStatus(Alert.AlertStatus.OPEN)).thenReturn(List.of(openAlert));

        List<AlertDTO> results = alertService.getOpenAlerts();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Web Server 01", results.get(0).getAssetName());
        assertEquals(Alert.AlertStatus.OPEN, results.get(0).getStatus());

        verify(alertRepository, times(1)).findByStatus(Alert.AlertStatus.OPEN);
    }
}
