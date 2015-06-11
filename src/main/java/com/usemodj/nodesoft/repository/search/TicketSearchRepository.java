package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Ticket;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Ticket entity.
 */
public interface TicketSearchRepository extends ElasticsearchRepository<Ticket, Long> {
}
