package com.taskmanagementtool_b72.entity;

import java.time.LocalDateTime;

import com.taskmanagementtool_b72.enums.IssuePriority;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.enums.IssueType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "issue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String issueKey;

	@Column(nullable = false)
	private String issueTitle;

	@Column(length = 2000)
	private String issueDescriptions;

	@Enumerated(EnumType.STRING)
	private IssueType issueType;

	@Enumerated(EnumType.STRING)
	private IssuePriority priority;

	@Enumerated(EnumType.STRING)
	private IssueStatus issueStatus;

	private String assigneeEmail;
	private String reporterEmail;

	private Long epicId;
	private Long sprintId;
	private Long projectId;
	private Long parentIssueId;
	private Integer backLogPosition;
	private Long workFlowId;
	private String labels;

	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();

	private LocalDateTime dueDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public String getIssueTitle() {
		return issueTitle;
	}

	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}

	public String getIssueDescriptions() {
		return issueDescriptions;
	}

	public void setIssueDescriptions(String issueDescriptions) {
		this.issueDescriptions = issueDescriptions;
	}

	public IssueType getIssueType() {
		return issueType;
	}

	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	public IssuePriority getPriority() {
		return priority;
	}

	public void setPriority(IssuePriority priority) {
		this.priority = priority;
	}

	public IssueStatus getIssueStatus() {
		return issueStatus;
	}

	public void setIssueStatus(IssueStatus issueStatus) {
		this.issueStatus = issueStatus;
	}

	public String getAssigneeEmail() {
		return assigneeEmail;
	}

	public void setAssigneeEmail(String assigneeEmail) {
		this.assigneeEmail = assigneeEmail;
	}

	public String getReporterEmail() {
		return reporterEmail;
	}

	public void setReporterEmail(String reporterEmail) {
		this.reporterEmail = reporterEmail;
	}

	public Long getEpicId() {
		return epicId;
	}

	public void setEpicId(Long epicId) {
		this.epicId = epicId;
	}

	public Long getSprintId() {
		return sprintId;
	}

	public void setSprintId(Long sprintId) {
		this.sprintId = sprintId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getParentIssueId() {
		return parentIssueId;
	}

	public void setParentIssueId(Long parentIssueId) {
		this.parentIssueId = parentIssueId;
	}

	public Integer getBackLogPosition() {
		return backLogPosition;
	}

	public void setBackLogPosition(Integer backLogPosition) {
		this.backLogPosition = backLogPosition;
	}

	public Long getWorkFlowId() {
		return workFlowId;
	}

	public void setWorkFlowId(Long workFlowId) {
		this.workFlowId = workFlowId;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}
}
