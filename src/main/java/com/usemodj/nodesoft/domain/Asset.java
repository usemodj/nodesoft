package com.usemodj.nodesoft.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Asset.
 */
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
