package com.usemodj.nodesoft.repository;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.domain.dto.AssetDTO;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;

public class AssetRepositoryImpl implements AssetRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(AssetRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	@Inject
	private AssetRepository assetRepository;
	@Inject
	private AssetSearchRepository assetSearchRepository;

	static String selectAsset = "SELECT asset FROM Asset asset WHERE asset.id = :id";
	static String deleteAsset = "DELETE FROM Asset asset WHERE asset.id = :id";
	static String selectAssets = "SELECT asset FROM Asset asset WHERE asset.viewableId = :viewableId AND asset.viewableType = :viewableType";
	
	static String selectProductAssets = "SELECT a.id, a.viewable_id, a.viewable_type, a.file_name, a.file_path, a.alt, a.position, v.sku, v.options, v.price, v.cost_price FROM NS_ASSET a " +
            " INNER JOIN (SELECT va.id, va.price, va.cost_price, va.sku, va.product_id, va.position, va.is_master, va.deleted_date, r.options " +
            " FROM NS_VARIANT va LEFT JOIN " +
            " (SELECT o.id, o.product_id, GROUP_CONCAT(o.options) AS options " +
            " FROM (SELECT v.id, v.product_id, concat(t.presentation,':', o.presentation) AS options " +
            "   FROM NS_VARIANT v, NS_VARIANT_OPTIONVALUE vo, NS_OPTIONVALUE o, NS_OPTIONTYPE t " +
            "   WHERE v.id = vo.variants_id and vo.optionvalues_id = o.id and o.optiontype_id = t.id) o " +
            " GROUP BY o.id) r  ON va.id = r.id) v " +
            " ON a.viewable_id = v.id " +
            " WHERE v.deleted_date IS NULL AND a.viewable_type = 'Variant' AND v.product_id = :productId " +
            " ORDER BY a.position, a.id";
			
	@Override
	public void deleteWithFile(Long assetId) {
		String uploadPath = env.getProperty("uploadPath");
		Asset asset = (Asset) em.createQuery(selectAsset)
					.setParameter("id", assetId)
					.getSingleResult();
		File file = new File(uploadPath + asset.getFilePath());
		file.delete();
		log.info("File deleted {}", file.getAbsolutePath());
		
//		em.createQuery(deleteAsset)
//		  .setParameter("id", assetId)
//		  .executeUpdate();
		assetRepository.delete(assetId);
		assetSearchRepository.delete(assetId);
		
	}

	@Override
	public void deleteWithFile(Long viewableId, String viewableType) {
		String uploadPath = env.getProperty("uploadPath");
		Optional.ofNullable((List<Asset>)em.createQuery(selectAssets)
				.setParameter("viewableId", viewableId)
				.setParameter("viewableType", viewableType)
				.getResultList()
			)
			.map(assets -> {
				for(Asset asset: assets){
					File file = new File(uploadPath + asset.getFilePath());
					file.delete();
					log.info("File deleted {}", file.getAbsolutePath());
					
//					em.createQuery(deleteAsset)
//					  .setParameter("id", asset.getId())
//					  .executeUpdate();
					assetRepository.delete(asset.getId());
					assetSearchRepository.delete(asset.getId());
				}
				return assets;
			});
		
	}

	@Override
	public List<AssetDTO> findAllProductAssets(Long productId) {
		
		return Optional.ofNullable(em.createNativeQuery(selectProductAssets, "AssetDTOMapping")
					.setParameter("productId", productId)
					.getResultList()
				)
				.orElse(Collections.emptyList());
	}

}
