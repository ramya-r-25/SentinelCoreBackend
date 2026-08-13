package org.example.sentinelcorebackend.Service;

import org.example.sentinelcorebackend.Dto.AssetDTO;
import org.example.sentinelcorebackend.Dto.DashboardSummaryDTO;

import java.util.List;

public interface AssetService {

    AssetDTO createAsset(AssetDTO dto);

    List<AssetDTO> getAllAssets();

    AssetDTO getById(Long id);

    AssetDTO updateAsset(Long id, AssetDTO dto);

    void deleteAsset(Long id);

    DashboardSummaryDTO getDashboardSummary();
}