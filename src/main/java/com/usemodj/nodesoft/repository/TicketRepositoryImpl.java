package com.usemodj.nodesoft.repository;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.domain.Post;
import com.usemodj.nodesoft.domain.Ticket;
import com.usemodj.nodesoft.domain.Topic;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;
import com.usemodj.nodesoft.repository.search.PostSearchRepository;
import com.usemodj.nodesoft.repository.search.TicketSearchRepository;

public class TicketRepositoryImpl implements TicketRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(PostRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	
	@Inject
	private TicketRepository ticketRepository;
	@Inject
	private TicketSearchRepository ticketSearchRepository;
	@Inject
	private MessageRepository messageRepository;
	@Inject
	private AssetRepository assetRepository;
	@Inject
	private AssetSearchRepository assetSearchRepository;
	
	static String selectTicket = "SELECT ticket FROM Ticket ticket LEFT JOIN FETCH ticket.messages WHERE ticket.id = :id";

	@Override
	public void deleteWithMessages(Long ticketId) {
		Optional.ofNullable((Ticket)em.createQuery( selectTicket)
				.setParameter("id", ticketId)
				.getSingleResult()
			)
			.map(ticket -> {
				for(Message message: ticket.getMessages()){
					
					messageRepository.deleteWithAssets(message.getId());
				}
				ticketRepository.delete(ticket);
				ticketSearchRepository.delete(ticket);
				return ticket;
			});
	}

}
