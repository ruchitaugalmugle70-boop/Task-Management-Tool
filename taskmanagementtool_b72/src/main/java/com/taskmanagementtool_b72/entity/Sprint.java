package com.taskmanagementtool_b72.entity;

import java.time.LocalDateTime;

import com.taskmanagementtool_b72.enums.SprintState;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sprints")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Sprint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String sprintName;

	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@Enumerated(EnumType.STRING)
	private SprintState sprintstate;

	@Column(length = 5000)
	private String goal;

	private LocalDateTime creaedat = LocalDateTime.now();

	private Long projectId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSprintName() {
		return sprintName;
	}

	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public SprintState getSprintstate() {
		return sprintstate;
	}

	public void setSprintstate(SprintState sprintstate) {
		this.sprintstate = sprintstate;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public LocalDateTime getCreaedat() {
		return creaedat;
	}

	public void setCreaedat(LocalDateTime creaedat) {
		this.creaedat = creaedat;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
