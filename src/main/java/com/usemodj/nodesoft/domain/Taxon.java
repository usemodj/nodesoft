package com.usemodj.nodesoft.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Taxon.
 */
@Entity
@Table(name = "NS_TAXON")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="taxon")
public class Taxon extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position")
    private Integer position;

    @Column(name = "permalink")
    private String permalink;

    @Column(name = "lft")
    private Integer lft;

    @Column(name = "rgt")
    private Integer rgt;

    @Column(name = "description")
    private String description;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "icon_file_name")
    private String iconFileName;

    @Column(name = "icon_content_type")
    private String iconContentType;

    @Column(name = "icon_file_size")
    private String iconFileSize;

    @ManyToOne(fetch= FetchType.EAGER)
//    @JoinColumn(name="parent_id")
//    @JsonBackReference(value="parent-children")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Taxon parent;
    
//    @Transient
//    @JsonProperty
    @Column(name="parent_id",insertable=false, updatable=false)
    private Long parentId;
    
    @OneToMany(mappedBy="parent",fetch= FetchType.EAGER)
//    @JsonManagedReference(value="parent-children")
    @NotFound(action = NotFoundAction.IGNORE)
//    @Transient
//    @JsonProperty
   private Set<Taxon> children;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "taxonomy_id")
    @JsonBackReference(value="taxonomy-taxons")
    @NotFound(action = NotFoundAction.IGNORE)
    private Taxonomy taxonomy;

    public Taxon(){
    	
    }
    
    public Taxon(String name, Taxonomy taxonomy) {
		this.name = name;
		this.taxonomy = taxonomy;
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getIconFileName() {
        return iconFileName;
    }

    public void setIconFileName(String iconFileName) {
        this.iconFileName = iconFileName;
    }

    public String getIconContentType() {
        return iconContentType;
    }

    public void setIconContentType(String iconContentType) {
        this.iconContentType = iconContentType;
    }

    public String getIconFileSize() {
        return iconFileSize;
    }

    public void setIconFileSize(String iconFileSize) {
        this.iconFileSize = iconFileSize;
    }

    public Taxon getParent() {
        return parent;
    }

    public void setParent(Taxon taxon) {
        this.parent = taxon;
    }

    public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Set<Taxon> getChildren() {
		return children;
	}

	public void setChildren(Set<Taxon> children) {
		this.children = children;
	}

	public void addChild(Taxon child){
		if(this.children == null) this.children = new HashSet<Taxon>();
		this.children.add(child);
	}
	public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Taxon taxon = (Taxon) o;

        if ( ! Objects.equals(id, taxon.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Taxon{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", parentId='" + parentId + "'" +
                ", position='" + position + "'" +
                ", permalink='" + permalink + "'" +
                ", lft='" + lft + "'" +
                ", rgt='" + rgt + "'" +
                ", description='" + description + "'" +
                ", metaTitle='" + metaTitle + "'" +
                ", metaDescription='" + metaDescription + "'" +
                ", metaKeywords='" + metaKeywords + "'" +
                ", depth='" + depth + "'" +
                ", iconFileName='" + iconFileName + "'" +
                ", iconContentType='" + iconContentType + "'" +
                ", iconFileSize='" + iconFileSize + "'" +
                '}';
    }
}
