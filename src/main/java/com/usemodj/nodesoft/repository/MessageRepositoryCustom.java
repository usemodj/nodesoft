package com.usemodj.nodesoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.usemodj.nodesoft.domain.Message;

public interface MessageRepositoryCustom {
    //@Query("SELECT m FROM Message m LEFT JOIN FETCH m.assets a WHERE m.ticket.id = ?1 AND a.viewableType='Message' ")
    List<Message> findAllWithAssets(Long ticketId);

	void deleteWithAssets(Long messageId);

}
