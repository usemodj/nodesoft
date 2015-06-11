package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Article entity.
 */
public interface ArticleSearchRepository extends ElasticsearchRepository<Article, Long> {
}
