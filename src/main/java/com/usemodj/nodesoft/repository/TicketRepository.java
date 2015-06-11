package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Ticket;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
public interface TicketRepository extends JpaRepository<Ticket,Long> {

    @Query("select ticket from Ticket ticket where ticket.user.email = ?#{principal.username}")
    List<Ticket> findAllForCurrentUser();

}
