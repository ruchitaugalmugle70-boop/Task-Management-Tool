package com.taskmanagementtool_b72.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "time_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long issueId;

	@Column(nullable = false)
	private String userEmail;

	@Column(nullable = false)
	private Double hoursLogged;

	private String description;

	private LocalDateTime loggedAt = LocalDateTime.now();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getIssueId() { return issueId; }
	public void setIssueId(Long issueId) { this.issueId = issueId; }
	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
	public Double getHoursLogged() { return hoursLogged; }
	public void setHoursLogged(Double hoursLogged) { this.hoursLogged = hoursLogged; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public LocalDateTime getLoggedAt() { return loggedAt; }
	public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}
