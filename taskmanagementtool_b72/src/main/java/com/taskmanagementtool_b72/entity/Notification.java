package com.taskmanagementtool_b72.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String recipientEmail;

	@Column(nullable = false)
	private String message;

	private String type; // ASSIGNMENT, STATUS_CHANGE, COMMENT, SYSTEM

	private boolean isRead = false;

	private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getRecipientEmail() { return recipientEmail; }
	public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public boolean isRead() { return isRead; }
	public void setRead(boolean isRead) { this.isRead = isRead; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
