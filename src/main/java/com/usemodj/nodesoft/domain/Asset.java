package com.usemodj.nodesoft.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.usemodj.nodesoft.domain.dto.AssetDTO;

/**
 * A Asset.
 */
@SqlResultSetMapping(name="AssetDTOMapping",
classes={
@ConstructorResult(
	targetClass = AssetDTO.class,
	columns = {
		@ColumnResult(name = "id", type=Long.class),
		@ColumnResult(name = "viewable_id", type=Long.class),
		@ColumnResult(name = "viewable_type", type=String.class),
		@ColumnResult(name = "file_name", type=String.class),
		@ColumnResult(name = "file_path", type=String.class),
		@ColumnResult(name = "alt", type=String.class),
		@ColumnResult(name = "position", type=Integer.class),
		@ColumnResult(name = "sku", type=String.class),
		@ColumnResult(name = "options", type=String.class),
		@ColumnResult(name = "price", type=BigDecimal.class),
		@ColumnResult(name = "cost_price", type=BigDecimal.class)
	}
)	
})

@Entity
@Table(name = "NS_ASSET")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="asset")
public class Asset extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "viewable_id")
    private Long viewableId;

    @Column(name = "viewable_type")
    private String viewableType;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "alt")
    private String alt;

    @Column(name = "position")
    private Integer position;

    public Asset(){
    	
    }
    
    public Asset(Long viewableId, String viewableType, String contentType, String fileSize, String fileName, String filePath,
    		String alt, Integer position){
    	this.viewableId = viewableId;
    	this.viewableType = viewableType;
    	this.contentType = contentType;
    	this.fileSize = fileSize;
    	this.fileName = fileName;
    	this.filePath = filePath;
    	this.alt = alt;
    	this.position = position;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Asset asset = (Asset) o;

        if ( ! Objects.equals(id, asset.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", viewableId='" + viewableId + "'" +
                ", viewableType='" + viewableType + "'" +
                ", contentType='" + contentType + "'" +
                ", fileSize='" + fileSize + "'" +
                ", fileName='" + fileName + "'" +
                ", filePath='" + filePath + "'" +
                ", alt='" + alt + "'" +
                ", position='" + position + "'" +
                '}';
    }
}
