package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Topic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Topic entity.
 */
public interface TopicRepository extends JpaRepository<Topic,Long>, TopicRepositoryCustom {

    @Query("select topic from Topic topic where topic.user.email = ?#{principal.username}")
    List<Topic> findAllForCurrentUser();

    @Query("SELECT topic FROM Topic topic WHERE topic.forum.id = ?1 AND (topic.sticky = false OR topic.sticky IS NULL)")
	Page<Topic> findTopicsByForumId(Long forumId, Pageable generatePageRequest);

    @Query("SELECT topic FROM Topic topic WHERE topic.forum.id = ?1 AND topic.sticky = true")
	List<Topic> findAllByForumIdAndSticky(Long forumId);

}
