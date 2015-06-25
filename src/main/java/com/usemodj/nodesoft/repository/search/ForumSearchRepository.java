package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Forum;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Forum entity.
 */
public interface ForumSearchRepository extends ElasticsearchRepository<Forum, Long> {
}
