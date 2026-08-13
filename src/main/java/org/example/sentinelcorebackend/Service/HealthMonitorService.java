package org.example.sentinelcorebackend.Service;

import lombok.RequiredArgsConstructor;
import org.example.sentinelcorebackend.Entity.Asset;
import org.example.sentinelcorebackend.Repository.AssetRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthMonitorService {
    private final AssetRepository assetRepository;
    private final AlertService alertService;

    // Threshold values
    private static final double CPU_CRITICAL_THRESHOLD = 90.0;
    private static final double MEMORY_WARNING_THRESHOLD = 80.0;

    // Runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void checkAssetHealth() {

        List<Asset> assets = assetRepository.findAll();

        for (Asset asset : assets) {

            // Check CPU
            if (asset.getCpuUsage() != null &&
                    asset.getCpuUsage() >= CPU_CRITICAL_THRESHOLD) {

                asset.setStatus("CRITICAL");

                alertService.createAlert(
                        asset.getId(),
                        "CRITICAL",
                        "CPU usage critical: "
                                + asset.getCpuUsage() + "%"
                );

            }

            // Check Memory
            else if (asset.getMemoryUsage() != null &&
                    asset.getMemoryUsage() >= MEMORY_WARNING_THRESHOLD) {

                asset.setStatus("WARNING");

                alertService.createAlert(
                        asset.getId(),
                        "MEDIUM",
                        "Memory usage high: "
                                + asset.getMemoryUsage() + "%"
                );

            }

            // Everything is normal
            else {

                asset.setStatus("ONLINE");
            }

            assetRepository.save(asset);
        }
    }
}