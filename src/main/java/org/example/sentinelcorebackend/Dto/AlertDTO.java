package org.example.sentinelcorebackend.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sentinelcorebackend.Entity.Alert;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {

    private Long id;

    private Long assetId;

    private String assetName;

    private Alert.AlertSeverity severity;

    private String message;

    private Alert.AlertStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}