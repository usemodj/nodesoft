package com.usemodj.nodesoft.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.usemodj.nodesoft.domain.Ticket;

public interface TicketSearchRepositoryCustom {

	Page<Ticket> searchForCurrentUser(String query,	Pageable pageable);

}
