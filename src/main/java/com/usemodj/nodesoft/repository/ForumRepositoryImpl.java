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

import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.domain.Post;
import com.usemodj.nodesoft.domain.Topic;
import com.usemodj.nodesoft.repository.search.ForumSearchRepository;

public class ForumRepositoryImpl implements ForumRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(ForumRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	
	Environment env;
	
	@Inject
	private ForumRepository forumRepository;
	@Inject
	private ForumSearchRepository forumSearchRepository;
	@Inject
	private TopicRepository topicRepository;

	static String selectTopics = "SELECT topic FROM Topic topic WHERE topic.forum.id = :id";
	@Override
	@Transactional
	public void deleteWithTopics(Long forumId) {
		Optional.ofNullable((List<Topic>)em.createQuery( selectTopics)
				.setParameter("id", forumId)
				.getResultList()
			)
			.map(topics -> {
				for(Topic topic: topics){
					topicRepository.deleteWithPosts(topic.getId());
				}
				forumRepository.delete(forumId);
				forumSearchRepository.delete(forumId);
				return topics;
			});

	}

}
