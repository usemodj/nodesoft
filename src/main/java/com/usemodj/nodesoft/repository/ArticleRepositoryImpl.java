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

import com.usemodj.nodesoft.domain.Article;
import com.usemodj.nodesoft.domain.Asset;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(ArticleRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;

	static String selectArticle = "SELECT a FROM Article a  WHERE a.id = :id";
	static String selectAssets = "SELECT a FROM Asset a WHERE a.viewableId = :id AND a.viewableType='Article'";
	static String deleteAsset = "DELETE FROM Asset a WHERE a.id = :id";
	static String deleteArticle = "DELETE FROM Article a WHERE a.id = :id";
	@Override
	public void deleteWithAssets(Long articleId) {
		String uploadPath = env.getProperty("uploadPath");
		
		Optional.ofNullable((List<Asset>)em.createQuery(selectAssets)
					.setParameter("id", articleId)
					.getResultList()
				)
				.map(assets -> {
					for(Asset asset: assets){
						File file = new File(uploadPath + asset.getFilePath());
						file.delete();
						em.createQuery(deleteAsset)
						  .setParameter("id", asset.getId())
						  .executeUpdate();
					}
					return assets;
				});
		
		em.createQuery(deleteArticle)
			.setParameter("id", articleId)
			.executeUpdate();
	}

	@Override
	public Article findOneWithAssets(Long articleId) {
		
		return Optional.ofNullable((Article)em.createQuery( selectArticle)
					.setParameter("id", articleId)
					.getSingleResult()
				)
				.map(article -> {
					List<Asset> assets = em.createQuery( selectAssets)
						.setParameter("id", articleId)
						.getResultList();
					article.setAssets(assets);
					return article;
				})
				.get();
	}

}
