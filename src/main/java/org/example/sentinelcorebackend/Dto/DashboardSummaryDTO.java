package org.example.sentinelcorebackend.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {

    private Long totalAssets;
    private Double uptimePercentage;
    private Double avgCpuUsage;
    private Long criticalAlerts;

}