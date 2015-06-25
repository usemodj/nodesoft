package com.usemodj.nodesoft.repository;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.domain.Post;
import com.usemodj.nodesoft.domain.Topic;
import com.usemodj.nodesoft.repository.search.TopicSearchRepository;

public class TopicRepositoryImpl implements TopicRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(TopicRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	
	Environment env;
	
	@Inject
	private ForumRepository forumRepository;
	@Inject
	private TopicRepository topicRepository;
	@Inject
	private TopicSearchRepository topicSearchRepository;
	@Inject
	private PostRepository postRepository;

	static String selectTopic = "SELECT topic FROM Topic topic LEFT JOIN FETCH topic.posts WHERE topic.id = :id";
	static String selectAssets = "SELECT asset FROM Asset asset WHERE asset.viewableId = :id AND asset.viewableType='Post'";
	
	static String selectTopicRoot = "SELECT topic FROM Topic topic WHERE topic.id = :id";
	static String selectPostRoot = "SELECT post FROM Post post WHERE post.topic.id = :id AND post.root = true";

	@Override
	public Topic findOneWithPosts(Long topicId) {
		return Optional.ofNullable((Topic)em.createQuery( selectTopic)
				.setParameter("id", topicId)
				.getSingleResult()
			)
			.map(topic -> {
				for(Post post: topic.getPosts()){
					try {
						List<Asset> assets = em.createQuery( selectAssets)
							.setParameter("id", post.getId())
							.getResultList();
						//log.debug("post : {}", post.toString());
						//log.debug(" -->assets: {}", assets.size());
						post.setAssets(assets);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				return topic;
			})
			.get();
	}

	@Override
	public Topic findOneWithRootPost(Long topicId) {
		return Optional.ofNullable((Topic)em.createQuery( selectTopicRoot)
				.setParameter("id", topicId)
				.getSingleResult()
			)
			.map(topic -> {
				Optional.ofNullable((Post)em.createQuery( selectPostRoot)
						.setParameter("id", topicId)
						.getSingleResult()
					)
					.map(post -> {
						List<Asset> assets = em.createQuery( selectAssets)
								.setParameter("id", post.getId())
								.getResultList();
						post.setAssets(assets);
						topic.setPost(post);
						return topic;
					});

				return topic;
			})
			.get();
	}

	@Override
	@Transactional
	public void deleteWithPosts(Long topicId) {
		Optional.ofNullable((Topic)em.createQuery( selectTopic)
				.setParameter("id", topicId)
				.getSingleResult()
			)
			.map(topic -> {
				int postSize = topic.getPosts().size();
				for(Post post: topic.getPosts()){
					postRepository.deleteWithAssets(post.getId());
				}
				Forum forum = topic.getForum();
				//log.debug("Topic : {}", topic.toString());
				//log.debug("forum lastTopic : {}", forum.getLastTopic().toString());
				if(topic.equals(forum.getLastTopic())){
					
					forum.setLastTopic(null);
				}
				int postCount = forum.getPostCount().intValue();
				postCount = Integer.max(postCount - postSize, 0);
				forum.setPostCount(postCount);
				int topicCount = Integer.max(forum.getTopicCount() - 1, 0);
				forum.setTopicCount(topicCount);
				forumRepository.save(forum);
				topicRepository.delete(topic);
				topicSearchRepository.delete(topic);
				return topic;
			});
	}

}
