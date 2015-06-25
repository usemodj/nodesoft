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
import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;
import com.usemodj.nodesoft.repository.search.MessageSearchRepository;

public class MessageRepositoryImpl implements MessageRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(ArticleRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	@Inject
	private MessageRepository messageRepository;
	@Inject
	private MessageSearchRepository messageSearchRepository;
	@Inject
	private AssetRepository assetRepository;
	@Inject
	private AssetSearchRepository assetSearchRepository;

	static String selectMessages = "SELECT m FROM Message m  WHERE m.ticket.id = :id";
	static String selectAssets = "SELECT a FROM Asset a WHERE a.viewableId = :id AND a.viewableType='Message'";

	@Override
	public List<Message> findAllWithAssets(Long ticketId) {
		return Optional.ofNullable((List<Message>)em.createQuery(selectMessages)
				.setParameter("id", ticketId)
				.getResultList()
			)
			.map(messages -> {
				for(Message message: messages){
					List<Asset> assets = em.createQuery( selectAssets)
							.setParameter("id", message.getId())
							.getResultList();
					message.setAssets(assets);
				}
				return messages;
			})
			.get();

	}

	@Override
	public void deleteWithAssets(Long messageId) {
		String uploadPath = env.getProperty("uploadPath");
		
		Optional.ofNullable((List<Asset>)em.createQuery(selectAssets)
					.setParameter("id", messageId)
					.getResultList()
				)
				.map(assets -> {
					for(Asset asset: assets){
						File file = new File(uploadPath + asset.getFilePath());
						file.delete();
						log.info(" file deleted {}", file.getAbsolutePath());
						assetRepository.delete(asset);
						assetSearchRepository.delete(asset);
					}
					return assets;
				});
		
		messageRepository.delete(messageId);
		messageSearchRepository.delete(messageId);
	}

}
