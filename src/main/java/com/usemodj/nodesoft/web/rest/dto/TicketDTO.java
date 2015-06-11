package com.usemodj.nodesoft.web.rest.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class TicketDTO {

	private Long id;
	private String subject;
	private String content;
	private String status;
	private Integer views;
	private Integer replies;
	
	private List<MultipartFile> files;
	
	public TicketDTO(){
		
	}
	
	public TicketDTO(String subject, String content, String status, List<MultipartFile> files){
		this.subject = subject;
		this.content = content;
		this.status = status;
		this.files = files;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
	
}
