package com.taskmanagementtool_b72.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailLogDTO {
	
	private String recipientEmail;
	private String subject;
	private String body;
	private String sentStatus;
	private LocalDateTime sentAt=LocalDateTime.now();
	
	public String getRecipientEmail() {
		return recipientEmail;
	}
	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSentStatus() {
		return sentStatus;
	}
	public void setSentStatus(String sentStatus) {
		this.sentStatus = sentStatus;
	}
	public LocalDateTime getSentAt() {
		return sentAt;
	}
	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}
	
	
	
	

}
