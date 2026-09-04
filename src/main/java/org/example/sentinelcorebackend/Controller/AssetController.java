package org.example.sentinelcorebackend.Controller;

import lombok.AllArgsConstructor;

import org.example.sentinelcorebackend.Dto.AssetDTO;
import org.example.sentinelcorebackend.Dto.DashboardSummaryDTO;
import org.example.sentinelcorebackend.Service.AssetService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class AssetController {

    private final AssetService assetService;


    // ==========================================
    // GET ALL ASSETS
    // ADMIN + OPERATOR + VIEWER
    // ==========================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public List<AssetDTO> getAssets() {

        return assetService.getAllAssets();
    }


    // ==========================================
    // SEARCH AND FILTER ASSETS
    // ADMIN + OPERATOR + VIEWER
    // ==========================================

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public List<AssetDTO> searchAssets(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            String status

    ) {

        return assetService.searchAndFilter(
                search,
                status
        );
    }


    // ==========================================
    // DASHBOARD SUMMARY
    // ADMIN + OPERATOR + VIEWER
    // ==========================================

    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public DashboardSummaryDTO getDashboardSummary() {

        return assetService.getDashboardSummary();
    }


    // ==========================================
    // CREATE ASSET
    // ADMIN + OPERATOR
    // ==========================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public AssetDTO createAsset(
            @RequestBody AssetDTO dto) {

        return assetService.createAsset(dto);
    }


    // ==========================================
    // GET ASSET BY ID
    // ADMIN + OPERATOR + VIEWER
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public AssetDTO getAssetById(
            @PathVariable Long id) {

        return assetService.getById(id);
    }


    // ==========================================
    // UPDATE ASSET
    // ADMIN + OPERATOR
    // ==========================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public AssetDTO updateAsset(

            @PathVariable Long id,

            @RequestBody AssetDTO dto) {

        return assetService.updateAsset(
                id,
                dto
        );
    }


    // ==========================================
    // DELETE ASSET
    // ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAsset(
            @PathVariable Long id) {

        assetService.deleteAsset(id);

        return "Asset deleted successfully";
    }
}
