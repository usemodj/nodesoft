package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.OptionType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the OptionType entity.
 */
public interface OptionTypeSearchRepository extends ElasticsearchRepository<OptionType, Long> {
}
