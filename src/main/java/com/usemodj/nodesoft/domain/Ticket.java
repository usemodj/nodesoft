package com.usemodj.nodesoft.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Ticket.
 */
@Entity
@Table(name = "NS_TICKET")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="ticket")
public class Ticket extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "status")
    private String status;

    @Column(name = "views")
    private Integer views;

    @Column(name = "replies")
    private Integer replies;

    @ManyToOne
    private User user;

    @OneToOne
    private User lastReplier;

    @OneToOne
    private Message lastReply;

    @OneToMany(mappedBy = "ticket")
    //@JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Message> messages = new HashSet<>();

    public Ticket(){
    	
    }
    
    public Ticket(String subject, String status, Integer views, Integer replies, User user){
    	this.subject = subject;
    	this.status = status;
    	this.views = views;
    	this.replies = replies;
    	this.user = user;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getReplies() {
        return replies;
    }

    public void setReplies(Integer replies) {
        this.replies = replies;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getLastReplier() {
        return lastReplier;
    }

    public void setLastReplier(User user) {
        this.lastReplier = user;
    }

    public Message getLastReply() {
        return lastReply;
    }

    public void setLastReply(Message message) {
        this.lastReply = message;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Ticket ticket = (Ticket) o;

        if ( ! Objects.equals(id, ticket.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", subject='" + subject + "'" +
                ", status='" + status + "'" +
                ", views='" + views + "'" +
                ", replies='" + replies + "'" +
                '}';
    }
}
