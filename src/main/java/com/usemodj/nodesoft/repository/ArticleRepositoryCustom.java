package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Article;

public interface ArticleRepositoryCustom {
	void deleteWithAssets(Long articleId);
	
    Article findOneWithAssets(Long articleId);

}
