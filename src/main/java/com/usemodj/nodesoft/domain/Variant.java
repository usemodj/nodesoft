package com.usemodj.nodesoft.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
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
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.joda.time.DateTime;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.usemodj.nodesoft.domain.dto.VariantDTO;
import com.usemodj.nodesoft.domain.util.CustomDateTimeDeserializer;
import com.usemodj.nodesoft.domain.util.CustomDateTimeSerializer;

/**
 * A Variant.
 */
@SqlResultSetMapping(name="VariantDTOMapping",
	classes={
	@ConstructorResult(
		targetClass = VariantDTO.class,
		columns = {
			@ColumnResult(name = "id", type=Long.class),
			@ColumnResult(name = "sku", type=String.class),
			@ColumnResult(name = "price", type=BigDecimal.class),
			@ColumnResult(name = "cost_price", type=BigDecimal.class),
			@ColumnResult(name = "position", type=Integer.class),
			@ColumnResult(name = "product_id", type=Long.class),
			@ColumnResult(name = "options", type=String.class),
			@ColumnResult(name = "deleted_date", type=PersistentDateTime.class)
		}
	)	
})

@Entity
@Table(name = "NS_VARIANT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="variant")
public class Variant extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "is_master")
    private Boolean isMaster;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "deleted_date", nullable = false)
    private DateTime deletedDate;

    @Column(name = "price", precision=10, scale=2, nullable = false)
    private BigDecimal price;

    @Column(name = "cost_price", precision=10, scale=2, nullable = false)
    private BigDecimal costPrice;

    @Column(name = "cost_currency")
    private String costCurrency;

    @Column(name = "position")
    private Integer position;

    @Column(name = "weight")
    private String weight;

    @Column(name = "height")
    private String height;

    @Column(name = "width")
    private String width;

    @Column(name = "depth")
    private String depth;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @JsonBackReference(value="product-variants")
    private Product product;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "NS_VARIANT_OPTIONVALUE",
               joinColumns = @JoinColumn(name="variants_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="optionvalues_id", referencedColumnName="ID"))
    private Set<OptionValue> optionValues = new HashSet<>();

    @Transient
    @JsonProperty
    private Map<Long, Long> optionValueMap;

    @Transient
    @JsonProperty
    private Long productId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Boolean getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(Boolean isMaster) {
        this.isMaster = isMaster;
    }

    public DateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(DateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public String getCostCurrency() {
        return costCurrency;
    }

    public void setCostCurrency(String costCurrency) {
        this.costCurrency = costCurrency;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}


	public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Set<OptionValue> getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(Set<OptionValue> optionValues) {
        this.optionValues = optionValues;
    }

    public Map getOptionValueMap() {
		return optionValueMap;
	}

	public void setOptionValueMap(Map<Long, Long> optionValueMap) {
		this.optionValueMap = optionValueMap;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Variant variant = (Variant) o;

        if ( ! Objects.equals(id, variant.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Variant{" +
                "id=" + id +
                ", sku='" + sku + "'" +
                ", isMaster='" + isMaster + "'" +
                ", deletedDate='" + deletedDate + "'" +
                ", price='" + price + "'" +
                ", costPrice='" + costPrice + "'" +
                ", costCurrency='" + costCurrency + "'" +
                ", position='" + position + "'" +
                ", weight='" + weight + "'" +
                ", height='" + height + "'" +
                ", width='" + width + "'" +
                ", depth='" + depth + "'" +
                '}';
    }
}
