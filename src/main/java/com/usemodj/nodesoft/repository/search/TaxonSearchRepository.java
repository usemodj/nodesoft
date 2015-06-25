package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Taxon;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Taxon entity.
 */
public interface TaxonSearchRepository extends ElasticsearchRepository<Taxon, Long> {
}
