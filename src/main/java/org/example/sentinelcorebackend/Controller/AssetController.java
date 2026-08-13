package org.example.sentinelcorebackend.Controller;

import lombok.AllArgsConstructor;
import org.example.sentinelcorebackend.Dto.AssetDTO;
import org.example.sentinelcorebackend.Dto.DashboardSummaryDTO;
import org.example.sentinelcorebackend.Service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class AssetController {

    private final AssetService assetService;

    // Get all assets
    @GetMapping
    public List<AssetDTO> getAssets() {
        return assetService.getAllAssets();
    }

    // Dashboard Summary
    @GetMapping("/dashboard/summary")
    public DashboardSummaryDTO getDashboardSummary() {
        return assetService.getDashboardSummary();
    }

    // Create Asset
    @PostMapping
    public AssetDTO createAsset(@RequestBody AssetDTO dto) {
        return assetService.createAsset(dto);
    }

    // Get Asset by ID
    @GetMapping("/{id}")
    public AssetDTO getAssetById(@PathVariable Long id) {
        return assetService.getById(id);
    }

    // Update Asset
    @PutMapping("/{id}")
    public AssetDTO updateAsset(
            @PathVariable Long id,
            @RequestBody AssetDTO dto) {

        return assetService.updateAsset(id, dto);
    }

    // Delete Asset
    @DeleteMapping("/{id}")
    public String deleteAsset(@PathVariable Long id) {

        assetService.deleteAsset(id);

        return "Asset deleted successfully";
    }
}