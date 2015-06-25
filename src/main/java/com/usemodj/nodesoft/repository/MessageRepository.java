package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.domain.Asset;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Message entity.
 */
public interface MessageRepository extends JpaRepository<Message,Long>, MessageRepositoryCustom {

    @Query("select message from Message message where message.user.email = ?#{principal.username}")
    List<Message> findAllForCurrentUser();

    List<Message> findAllByTicketIdOrderByIdDesc(Long ticketId);

}
