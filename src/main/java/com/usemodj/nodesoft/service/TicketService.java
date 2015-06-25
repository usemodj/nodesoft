package com.usemodj.nodesoft.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usemodj.nodesoft.domain.Ticket;
import com.usemodj.nodesoft.repository.TicketRepository;


@Service
@Transactional
public class TicketService {
    private final Logger log = LoggerFactory.getLogger(TicketService.class);
    @Inject
    private TicketRepository ticketRepository;

//    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
//    @PostFilter( "hasRole('ROLE_ADMIN') || filterObject.content.user.email == principal.name")
    public Page<Ticket> getUserTickets(Pageable pageable){
		return ticketRepository.findAllForCurrentUser(pageable);
    }
}
