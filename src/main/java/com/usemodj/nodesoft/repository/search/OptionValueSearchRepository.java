package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.OptionValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the OptionValue entity.
 */
public interface OptionValueSearchRepository extends ElasticsearchRepository<OptionValue, Long> {
}
