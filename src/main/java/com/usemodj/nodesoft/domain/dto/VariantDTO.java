package com.usemodj.nodesoft.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.DateTime;

public class VariantDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String sku;
	private BigDecimal price;
	private BigDecimal costPrice;
	private Integer position;
	private Long productId;
	private String options;
	private DateTime deletedDate;

	public VariantDTO(){
		
	}
	
	public VariantDTO(Long id, String sku, BigDecimal price, BigDecimal costPrice, Integer position, Long productId, String options, DateTime deletedDate){
		this.id = id;
		this.sku = sku;
		this.price = price;
		this.costPrice = costPrice;
		this.position = position;
		this.productId = productId;
		this.options = options;
		this.deletedDate = deletedDate;
	}

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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DateTime getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(DateTime deletedDate) {
		this.deletedDate = deletedDate;
	}
	
	
}
