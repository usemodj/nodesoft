package com.usemodj.nodesoft.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.usemodj.nodesoft.domain.Product;
import com.usemodj.nodesoft.domain.util.CustomDateTimeDeserializer;
import com.usemodj.nodesoft.domain.util.CustomDateTimeSerializer;
import com.usemodj.nodesoft.domain.util.CustomLocalDateSerializer;
import com.usemodj.nodesoft.domain.util.ISO8601LocalDateDeserializer;


public class ProductDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String sku;
	private BigDecimal price;
	
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate availableDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime deletedDate;

	public ProductDTO(){
    	
    }
    
    public ProductDTO(Long id, String name, String sku, BigDecimal price, LocalDate availableDate, DateTime deletedDate){
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

    
}
