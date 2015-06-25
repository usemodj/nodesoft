package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Article;
import com.usemodj.nodesoft.domain.Message;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Article entity.
 */
public interface ArticleRepository extends JpaRepository<Article,Long>, ArticleRepositoryCustom {

    @Query("select article from Article article where article.user.email = ?#{principal.username}")
    List<Article> findAllForCurrentUser();

    //@Query("SELECT a FROM Article a LEFT JOIN FETCH a.assets s  WHERE a.id = ?1 ")
    //Article findOneWithAssets(Long articleId);

}
