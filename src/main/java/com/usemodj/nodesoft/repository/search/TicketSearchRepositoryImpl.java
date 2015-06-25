package com.usemodj.nodesoft.repository.search;

import javax.inject.Inject;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.search.MultiMatchQuery.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.usemodj.nodesoft.domain.Authority;
import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.domain.Ticket;
import com.usemodj.nodesoft.repository.ForumRepositoryImpl;
import com.usemodj.nodesoft.service.UserService;

public class TicketSearchRepositoryImpl implements TicketSearchRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(TicketSearchRepositoryImpl.class);

	@Inject
	private ElasticsearchTemplate elasticsearchTemplate;
	@Inject
	private UserService userService;
	
	@Override
	public Page<Ticket> searchForCurrentUser(String query, Pageable pageable) {
		elasticsearchTemplate.createIndex(Ticket.class);
		elasticsearchTemplate.createIndex(Message.class);
		boolean isAdmin = userService.getUserWithAuthorities().getAuthorities().contains(new Authority("ROLE_ADMIN"));
		String email = userService.getUserWithAuthorities().getEmail();
		SearchQuery searchQuery = null;
		if(isAdmin){
			searchQuery = new NativeSearchQueryBuilder()
	        .withQuery(QueryBuilders.boolQuery()
	        		.should(QueryBuilders.fuzzyLikeThisQuery("subject", "status").likeText(query))
	        		.should(QueryBuilders.nestedQuery("messages", QueryBuilders.fuzzyLikeThisQuery("messages.content").likeText(query)))
	        		)
	        .build();
		} else {
			searchQuery = new NativeSearchQueryBuilder()
	        .withQuery(QueryBuilders.boolQuery()
	        		.should(QueryBuilders.fuzzyLikeThisQuery("subject", "status").likeText(query))
	        		.should(QueryBuilders.nestedQuery("messages", QueryBuilders.fuzzyLikeThisQuery("messages.content").likeText(query)))
	        		//.must(QueryBuilders.nestedQuery("user", QueryBuilders.termQuery("user.email", userService.getUserWithAuthorities().getEmail())))
	        		)
	        .withFilter(FilterBuilders.nestedFilter("user", FilterBuilders.termFilter("user.email", email)))
	        .build();
		}
		if(searchQuery.getQuery() != null) log.debug("search query: {}", searchQuery.getQuery().toString());
		if( searchQuery.getFilter() != null) log.debug("search filter: {}", searchQuery.getFilter().toString());
        Page<Ticket> ticket = elasticsearchTemplate.queryForPage(searchQuery, Ticket.class);
    
		return ticket;
	}

}
