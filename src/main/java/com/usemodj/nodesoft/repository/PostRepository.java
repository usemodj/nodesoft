package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Post;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Post entity.
 */
public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom {

    @Query("select post from Post post where post.user.email = ?#{principal.username}")
    List<Post> findAllForCurrentUser();

}
