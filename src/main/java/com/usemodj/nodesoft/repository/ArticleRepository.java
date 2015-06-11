package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Article;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Article entity.
 */
public interface ArticleRepository extends JpaRepository<Article,Long> {

    @Query("select article from Article article where article.user.login = ?#{principal.username}")
    List<Article> findAllForCurrentUser();

}
