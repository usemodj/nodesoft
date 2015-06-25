package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
public interface TicketRepository extends JpaRepository<Ticket,Long>, TicketRepositoryCustom {

    @Query("select ticket from Ticket ticket where ticket.user.email = ?#{principal.username} OR 1=?#{hasRole('ROLE_ADMIN') ? 1 : 0}")
    List<Ticket> findAllForCurrentUser();

    @Query("select ticket from Ticket ticket where ticket.user.email = ?#{principal.username} OR 1=?#{hasRole('ROLE_ADMIN') ? 1 : 0}")
	Page<Ticket> findAllForCurrentUser(Pageable pageable);

    @Query("select ticket from Ticket ticket where ticket.id = ?1 AND (ticket.user.email = ?#{principal.username} OR 1=?#{hasRole('ROLE_ADMIN') ? 1 : 0})")
    Ticket findOne(Long id);

}
