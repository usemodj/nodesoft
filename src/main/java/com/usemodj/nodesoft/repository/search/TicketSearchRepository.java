package com.usemodj.nodesoft.repository.search;

import com.usemodj.nodesoft.domain.Ticket;

import org.elasticsearch.index.query.FuzzyLikeThisQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Ticket entity.
 */
public interface TicketSearchRepository extends ElasticsearchRepository<Ticket, Long>, TicketSearchRepositoryCustom {

//	@Query("{\"bool\":{"+
//			"	\"must\":{\"term\":{\"user.email\":\"?#{principal.username}\"}},"+
//			"   \"must\":{\"multi_match\":{\"query\":\"?0\", \"fields\":[\"subject\", \"messages.content\"]}}"+
//			"  }"+
//			"}"
//			)
//	Page<Ticket> searchForCurrentUser(String likeText,
//			Pageable generatePageRequest);
}
