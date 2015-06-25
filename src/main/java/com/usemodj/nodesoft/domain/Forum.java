package com.usemodj.nodesoft.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Forum.
 */
@Entity
@Table(name = "NS_FORUM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="forum")
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Forum extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "display")
    private Integer display;

    @Column(name = "lft")
    private Integer lft;

    @Column(name = "rgt")
    private Integer rgt;

    @Column(name = "post_count")
    private Integer postCount;

    @Column(name = "topic_count")
    private Integer topicCount;

    @Column(name = "locked")
    private Boolean locked;

    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "lasttopic_id")
    @NotFound(action = NotFoundAction.IGNORE)
    //@Field(type = FieldType.Auto, store=false)
    private Topic lastTopic;

    //@OneToOne
    @Transient
    private Post lastPost;

    //@ManyToOne(fetch=FetchType.LAZY)
    //@JoinColumn(name="parent_id")
    @Transient
    private Forum parent;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Integer getLft() {
        return lft;
    }

    public void setLft(Integer lft) {
        this.lft = lft;
    }

    public Integer getRgt() {
        return rgt;
    }

    public void setRgt(Integer rgt) {
        this.rgt = rgt;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(Integer topicCount) {
        this.topicCount = topicCount;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Topic getLastTopic() {
        return lastTopic;
    }

    public void setLastTopic(Topic topic) {
        this.lastTopic = topic;
    }

    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(Post post) {
        this.lastPost = post;
    }

    public Forum getParent() {
        return parent;
    }

    public void setParent(Forum forum) {
        this.parent = forum;
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Forum forum = (Forum) o;

        if ( ! Objects.equals(id, forum.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", display='" + display + "'" +
                ", lft='" + lft + "'" +
                ", rgt='" + rgt + "'" +
                ", postCount='" + postCount + "'" +
                ", topicCount='" + topicCount + "'" +
                ", locked='" + locked + "'" +
                '}';
    }
}
