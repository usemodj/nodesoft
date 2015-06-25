package com.usemodj.nodesoft.repository;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;
import com.usemodj.nodesoft.repository.search.PostSearchRepository;

public class PostRepositoryImpl implements PostRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(PostRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	
	@Inject
	private PostRepository postRepository;
	@Inject
	private PostSearchRepository postSearchRepository;
	@Inject
	private AssetRepository assetRepository;
	@Inject
	private AssetSearchRepository assetSearchRepository;
	
	static String selectAssets = "SELECT asset FROM Asset asset WHERE asset.viewableId = :id AND asset.viewableType='Post'";
	static String deleteAsset = "DELETE FROM Asset asset WHERE asset.id = :id";
	static String deletePost = "DELETE FROM Post post WHERE post.id = :id";

	@Override
	public void deleteWithAssets(Long postId) {
		String uploadPath = env.getProperty("uploadPath");
		
		Optional.ofNullable((List<Asset>)em.createQuery(selectAssets)
					.setParameter("id", postId)
					.getResultList()
				)
				.map(assets -> {
					for(Asset asset: assets){
						File file = new File(uploadPath + asset.getFilePath());
						file.delete();
						log.info(" file deleted {}", file.getAbsolutePath());
//						em.createQuery(deleteAsset)
//						  .setParameter("id", asset.getId())
//						  .executeUpdate();
						assetRepository.delete(asset.getId());
						assetSearchRepository.delete(asset.getId());
					}
					return assets;
				});
		
//		em.createQuery(deletePost)
//		.setParameter("id", postId)
//		.executeUpdate();
		postRepository.delete(postId);
		postSearchRepository.delete(postId);
	}

}
