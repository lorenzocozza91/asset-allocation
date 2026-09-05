package com.lorenzocozza.assetallocation.asset;

import com.lorenzocozza.assetallocation.domain.AssetState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AssetStateController {

    private final AssetStateService assetStateService;

    public AssetStateController(AssetStateService assetStateService) {
        this.assetStateService = assetStateService;
    }

    @GetMapping("/{assetId}/state")
    public ResponseEntity<AssetState> getState(@PathVariable UUID assetId) {
        return assetStateService.getState(assetId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
