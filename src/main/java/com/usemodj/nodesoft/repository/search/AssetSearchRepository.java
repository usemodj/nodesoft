package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Asset;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Asset entity.
 */
public interface AssetSearchRepository extends ElasticsearchRepository<Asset, Long> {
}
