package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Taxonomy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Taxonomy entity.
 */
public interface TaxonomySearchRepository extends ElasticsearchRepository<Taxonomy, Long> {
}
