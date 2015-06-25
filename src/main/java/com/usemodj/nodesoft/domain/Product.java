package com.usemodj.nodesoft.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.usemodj.nodesoft.domain.dto.ProductDTO;
import com.usemodj.nodesoft.domain.util.CustomDateTimeDeserializer;
import com.usemodj.nodesoft.domain.util.CustomDateTimeSerializer;
import com.usemodj.nodesoft.domain.util.CustomLocalDateSerializer;
import com.usemodj.nodesoft.domain.util.ISO8601LocalDateDeserializer;

/**
 * A Product.
 */
@SqlResultSetMapping(name="ProductDTOMapping",
	classes={
	@ConstructorResult(
		targetClass = ProductDTO.class,
		columns = {
			@ColumnResult(name = "id", type=Long.class),
			@ColumnResult(name = "name", type=String.class),
			@ColumnResult(name = "sku", type=String.class),
			@ColumnResult(name = "price", type=BigDecimal.class),
			@ColumnResult(name = "available_date", type=PersistentLocalDate.class),
			@ColumnResult(name = "deleted_date", type=PersistentDateTime.class)
		}
	)	
})

@Entity
@Table(name = "NS_PRODUCT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="product")
public class Product extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "properties")
    private String properties;

    @Column(name = "description")
    private String description;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "available_date")
    private LocalDate availableDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "deleted_date")
    private DateTime deletedDate;

    @Column(name = "slug")
    private String slug;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "NS_PRODUCT_OPTIONTYPE",
               joinColumns = @JoinColumn(name="products_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="optiontypes_id", referencedColumnName="ID"))
    private Set<OptionType> optionTypes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "NS_PRODUCT_TAXON",
               joinColumns = @JoinColumn(name="products_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="taxons_id", referencedColumnName="ID"))
    private Set<Taxon> taxons = new HashSet<>();

    @Transient
    @JsonProperty
    private Long[] optionTypeIds;
    @Transient
    @JsonProperty
    private Long[] taxonIds;
    
    @Transient
    @JsonProperty
    //@JsonManagedReference(value="product-variants")
    private Variant variant;
 
    @Transient
    @JsonProperty
    private String sku;
    @Transient
    @JsonProperty
    private BigDecimal price;

    public Product(){
    	
    }
    
    public Product(Long id, String name, String sku, BigDecimal price, LocalDate availableDate, DateTime deletedDate){
    	this.id = id;
    	this.name = name;
    	this.sku = sku;
    	this.price = price;
    	this.availableDate = availableDate;
    	this.deletedDate = deletedDate;
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

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public DateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(DateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public Set<OptionType> getOptionTypes() {
        return optionTypes;
    }

    public void setOptionTypes(Set<OptionType> optionTypes) {
        this.optionTypes = optionTypes;
    }

    public Set<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(Set<Taxon> taxons) {
        this.taxons = taxons;
    }


	public Long[] getOptionTypeIds() {
		return optionTypeIds;
	}

	public void setOptionTypeIds(Long[] optionTypeIds) {
		this.optionTypeIds = optionTypeIds;
	}

	public Long[] getTaxonIds() {
		return taxonIds;
	}

	public void setTaxonIds(Long[] taxonIds) {
		this.taxonIds = taxonIds;
	}

	public Variant getVariant() {
		return variant;
	}

	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Product product = (Product) o;

        if ( ! Objects.equals(id, product.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", properties='" + properties + "'" +
                ", description='" + description + "'" +
                ", availableDate='" + availableDate + "'" +
                ", deletedDate='" + deletedDate + "'" +
                ", slug='" + slug + "'" +
                ", metaDescription='" + metaDescription + "'" +
                ", metaKeywords='" + metaKeywords + "'" +
                '}';
    }
}
