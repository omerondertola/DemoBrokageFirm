package com.example.demo.assets.service;

import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.AssetDto;
import org.springframework.stereotype.Service;

@Service
public class AssetMapper {

    public Asset toAsset(AssetDto dto) {
        return Asset.builder()
                .assetName(dto.assetName())
                .size(dto.assetSize())
                .usableSize(dto.assetSize()) // at creation usable size must be equal to size
                .customerId(dto.customerId())
                .build();

    }

}
