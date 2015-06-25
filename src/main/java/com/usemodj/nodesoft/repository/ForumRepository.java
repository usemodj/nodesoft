package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Forum;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Forum entity.
 */
public interface ForumRepository extends JpaRepository<Forum,Long>, ForumRepositoryCustom {

}
