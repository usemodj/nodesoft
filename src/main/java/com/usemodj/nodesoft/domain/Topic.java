package com.usemodj.nodesoft.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * A Topic.
 */
@Entity
@Table(name = "NS_TOPIC")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="topic")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Topic extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "views")
    private Integer views;

    @Column(name = "replies")
    private Integer replies;

    @Column(name = "locked")
    private Boolean locked;

    @Column(name = "sticky")
    private Boolean sticky;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "forum_id")
    private Forum forum;

    @ManyToOne
    private User user;

    @OneToOne(fetch=FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    //@Transient
    //@JsonProperty
    private Post lastPost;

    @OneToMany(mappedBy = "topic", fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonManagedReference(value="topic-posts")
    private List<Post> posts;

    @Transient
    @JsonProperty
    private Post post;

    public Topic(){
    	
    }
    
    public Topic(String name, Boolean locked, Boolean sticky, User user, Forum forum){
    	this.name = name;
    	this.locked = locked;
    	this.sticky = sticky;
    	this.user = user;
    	this.forum = forum;
    	this.views = 0;
    	this.replies = 0;
    	this.setCreatedDate(DateTime.now());
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(Post post) {
        this.lastPost = post;
    }

    public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Topic topic = (Topic) o;

        if ( ! Objects.equals(id, topic.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", views='" + views + "'" +
                ", replies='" + replies + "'" +
                ", locked='" + locked + "'" +
                ", sticky='" + sticky + "'" +
                '}';
    }
}
