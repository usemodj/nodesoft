package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Topic;

public interface TopicRepositoryCustom {
    
	Topic findOneWithPosts(Long topicId);

	Topic findOneWithRootPost(Long topicId);

	void deleteWithPosts(Long topicId);

}
