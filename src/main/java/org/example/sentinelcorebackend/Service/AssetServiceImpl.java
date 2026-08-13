package org.example.sentinelcorebackend.Service;

import org.example.sentinelcorebackend.Dto.AssetDTO;
import org.example.sentinelcorebackend.Dto.DashboardSummaryDTO;
import org.example.sentinelcorebackend.Entity.Asset;
import org.example.sentinelcorebackend.Repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetDTO getById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return toDTO(asset);
    }

    @Override
    public AssetDTO createAsset(AssetDTO dto) {

        Asset asset = Asset.builder()
                .assetName(dto.getAssetName())
                .assetType(dto.getAssetType())
                .ipAddress(dto.getIpAddress())
                .location(dto.getLocation())
                .status(dto.getStatus())
                .cpuUsage(dto.getCpuUsage())
                .memoryUsage(dto.getMemoryUsage())
                .diskUsage(dto.getDiskUsage())
                .networkUsage(dto.getNetworkUsage())
                .createdDate(dto.getCreatedDate())
                .build();

        Asset savedAsset = assetRepository.save(asset);

        return toDTO(savedAsset);
    }

    @Override
    public AssetDTO updateAsset(Long id, AssetDTO dto) {

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setAssetName(dto.getAssetName());
        asset.setAssetType(dto.getAssetType());
        asset.setIpAddress(dto.getIpAddress());
        asset.setLocation(dto.getLocation());
        asset.setStatus(dto.getStatus());
        asset.setCpuUsage(dto.getCpuUsage());
        asset.setMemoryUsage(dto.getMemoryUsage());
        asset.setDiskUsage(dto.getDiskUsage());
        asset.setNetworkUsage(dto.getNetworkUsage());
        asset.setCreatedDate(dto.getCreatedDate());

        Asset updatedAsset = assetRepository.save(asset);

        return toDTO(updatedAsset);
    }

    @Override
    public void deleteAsset(Long id) {

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        assetRepository.delete(asset);
    }

    private AssetDTO toDTO(Asset asset) {

        return AssetDTO.builder()
                .id(asset.getId())
                .assetName(asset.getAssetName())
                .assetType(asset.getAssetType())
                .ipAddress(asset.getIpAddress())
                .location(asset.getLocation())
                .status(asset.getStatus())
                .cpuUsage(asset.getCpuUsage())
                .memoryUsage(asset.getMemoryUsage())
                .diskUsage(asset.getDiskUsage())
                .networkUsage(asset.getNetworkUsage())
                .createdDate(asset.getCreatedDate())
                .build();
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {

        List<Asset> assets = assetRepository.findAll();

        long totalAssets = assets.size();

        double avgCpu = assets.stream()
                .mapToDouble(Asset::getCpuUsage)
                .average()
                .orElse(0);

        long criticalAlerts = assets.stream()
                .filter(asset -> asset.getCpuUsage() >= 90)
                .count();

        double uptime = 99.90;

        return new DashboardSummaryDTO(
                totalAssets,
                uptime,
                avgCpu,
                criticalAlerts
        );
    }
}