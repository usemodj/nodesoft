package com.usemodj.nodesoft.repository;

import java.util.List;

import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.domain.dto.AssetDTO;

public interface AssetRepositoryCustom {

	void deleteWithFile(Long assetId);
	
	void deleteWithFile(Long viewableId, String viewableType);
	
	List<AssetDTO> findAllProductAssets(Long productId);
}
