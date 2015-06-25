package com.usemodj.nodesoft.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class AssetDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//a.id, a.viewable_id, a.viewable_type, a.file_name, a.file_path, a.alt, a.position, 
	//v.sku, v.options, v.price, v.cost_price
	private Long id;
	private Long viewableId;
	private String viewableType;
	private String fileName;
	private String filePath;
	private String alt;
	private Integer position;
	private String sku;
	private String options;
	private BigDecimal price;
	private BigDecimal costPrice;
	
	public AssetDTO(){
		
	}
	public AssetDTO(
			Long id,
			Long viewableId,
			String viewableType,
			String fileName,
			String filePath,
			String alt,
			Integer position,
			String sku,
			String options,
			BigDecimal price,
			BigDecimal costPrice
			){
		this.id = id;	 
		this.viewableId = viewableId;
		this.viewableType = viewableType;
		this.fileName = fileName;
		this.filePath = filePath;
		this.alt = alt;
		this.position = position;
		this.sku = sku;
		this.options = options;
		this.price = price;
		this.costPrice = costPrice;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getViewableId() {
		return viewableId;
	}
	public void setViewableId(Long viewableId) {
		this.viewableId = viewableId;
	}
	public String getViewableType() {
		return viewableType;
	}
	public void setViewableType(String viewableType) {
		this.viewableType = viewableType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
