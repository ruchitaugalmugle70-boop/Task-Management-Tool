package com.taskmanagementtool_b72.entity;

import java.time.LocalDateTime;
import com.taskmanagementtool_b72.enums.TeamRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long projectId;

	@Column(nullable = false)
	private String userEmail;

	private String userName;

	@Enumerated(EnumType.STRING)
	private TeamRole role;

	private LocalDateTime joinedAt = LocalDateTime.now();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getProjectId() { return projectId; }
	public void setProjectId(Long projectId) { this.projectId = projectId; }
	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
	public String getUserName() { return userName; }
	public void setUserName(String userName) { this.userName = userName; }
	public TeamRole getRole() { return role; }
	public void setRole(TeamRole role) { this.role = role; }
	public LocalDateTime getJoinedAt() { return joinedAt; }
	public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
