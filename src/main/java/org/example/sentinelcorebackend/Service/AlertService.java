package org.example.sentinelcorebackend.Service;

import org.example.sentinelcorebackend.Dto.AlertDTO;
import org.example.sentinelcorebackend.Entity.Alert;
import org.example.sentinelcorebackend.Entity.Asset;
import org.example.sentinelcorebackend.Repository.AlertRepository;
import org.example.sentinelcorebackend.Repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AssetRepository assetRepository;

    // Email notification service
    private final NotificationService notificationService;

    // Twilio SMS notification service
    private final TwilioSmsService twilioSmsService;


    // ============================================================
    // CREATE A NEW ALERT
    // ============================================================
    public AlertDTO createAlert(Long assetId, String severity, String message) {

        // Find the asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new RuntimeException("Asset not found: " + assetId)
                );

        // Create alert
        Alert alert = Alert.builder()
                .asset(asset)
                .severity(Alert.AlertSeverity.valueOf(severity))
                .message(message)
                .status(Alert.AlertStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        // Save alert to database
        Alert savedAlert = alertRepository.save(alert);


        // ========================================================
        // SEND NOTIFICATIONS FOR HIGH / CRITICAL ALERTS
        // ========================================================
        if (alert.getSeverity() == Alert.AlertSeverity.CRITICAL ||
                alert.getSeverity() == Alert.AlertSeverity.HIGH) {

            // ----------------------------------------------------
            // 1. SEND EMAIL
            // ----------------------------------------------------
            notificationService.sendAlertEmail(
                    "ramyaravin2006@gmail.com",
                    asset.getAssetName(),
                    alert.getSeverity().name(),
                    alert.getMessage()
            );


            // ----------------------------------------------------
            // 2. SEND SMS USING TWILIO
            // ----------------------------------------------------
            twilioSmsService.sendSms(
                    "+917540067926",
                    "SentinelCore Alert: "
                            + alert.getSeverity().name()
                            + " on "
                            + asset.getAssetName()
                            + ": "
                            + alert.getMessage()
            );
        }

        return toDTO(savedAlert);
    }


    // ============================================================
    // RESOLVE AN ALERT
    // ============================================================
    public AlertDTO resolveAlert(Long alertId) {
        System.out.println("=== RESOLVING ALERT ID: " + alertId + " ===");

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found: " + alertId)
                );

        alert.setStatus(Alert.AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());

        Alert resolvedAlert = alertRepository.save(alert);

        // Send email notification on resolution
        notificationService.sendAlertResolvedEmail(
                "ramyaravin2006@gmail.com",
                resolvedAlert.getAsset() != null ? resolvedAlert.getAsset().getAssetName() : "Unknown Asset",
                resolvedAlert.getSeverity() != null ? resolvedAlert.getSeverity().name() : "INFO",
                resolvedAlert.getMessage()
        );

        return toDTO(resolvedAlert);
    }


    // ============================================================
    // GET ALL OPEN ALERTS
    // ============================================================
    public List<AlertDTO> getOpenAlerts() {

        return alertRepository
                .findByStatus(Alert.AlertStatus.OPEN)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    // ============================================================
    // CONVERT ALERT ENTITY → ALERT DTO
    // ============================================================
    private AlertDTO toDTO(Alert alert) {

        return AlertDTO.builder()
                .id(alert.getId())
                .assetId(alert.getAsset().getId())
                .assetName(alert.getAsset().getAssetName())
                .severity(alert.getSeverity())
                .message(alert.getMessage())
                .status(alert.getStatus())
                .createdAt(alert.getCreatedAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }
}