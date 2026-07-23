package com.taskmanagementtool_b72.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.entity.IssueComment;
import com.taskmanagementtool_b72.entity.Sprint;
import com.taskmanagementtool_b72.enums.IssuePriority;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.enums.IssueType;
import com.taskmanagementtool_b72.enums.SprintState;
import com.taskmanagementtool_b72.repository.IssueCommentRepository;
import com.taskmanagementtool_b72.repository.IssueRepository;
import com.taskmanagementtool_b72.repository.SprintRepository;
import com.taskmanagementtool_b72.repository.ProjectRepository;

@Service
public class IssueService {

	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private IssueCommentRepository issueCommentRepo;

	@Autowired
	private SprintRepository sprintRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private ActivityLogService activityLogService;

	private String generatedKey(Long id, Long projectId) {
		if (projectId != null) {
			return projectRepo.findById(projectId)
					.map(p -> p.getProjectKey() + "-" + id)
					.orElse("TMT-" + id);
		}
		return "TMT-" + id;
	}

	@Transactional
	public Issue createIssue(Issue issue) {
		issue.setIssueType(issue.getIssueType() != null ? issue.getIssueType() : IssueType.TASK);
		issue.setPriority(issue.getPriority() != null ? issue.getPriority() : IssuePriority.MEDIUM);
		issue.setIssueStatus(IssueStatus.OPEN);
		issue.setIssueKey("PENDING");

		Issue saved = issueRepo.save(issue);
		saved.setIssueKey(generatedKey(saved.getId(), saved.getProjectId()));

		Issue finalSaved = issueRepo.save(saved);
		activityLogService.logActivity("ISSUE_CREATED", issue.getReporterEmail() != null ? issue.getReporterEmail() : "system", "Issue", finalSaved.getId(), "Created issue " + finalSaved.getIssueKey() + ": " + finalSaved.getIssueTitle());
		return finalSaved;
	}

	public List<Issue> findIssueByAssigneeEmail(String userOfficialEmail) {
		return issueRepo.findByAssigneeEmail(userOfficialEmail);
	}

	public Issue findIssueById(Long id) {
		return issueRepo.findById(id).orElseThrow(() -> new RuntimeException("Issue not found"));
	}

	public List<Issue> findIssueBySprint(Long sprintId) {
		return issueRepo.findBySprintId(sprintId);
	}

	@Transactional
	public Issue updateIssueStatus(Long id, IssueStatus issueStatus) {
		Issue issue = findIssueById(id);

		if (issueStatus == null) {
			throw new RuntimeException("Status cannot be null");
		}

		IssueStatus oldStatus = issue.getIssueStatus();
		issue.setIssueStatus(issueStatus);
		Issue updated = issueRepo.save(issue);

		activityLogService.logActivity("ISSUE_STATUS_CHANGED", issue.getAssigneeEmail() != null ? issue.getAssigneeEmail() : "system", "Issue", issue.getId(), "Updated status of " + issue.getIssueKey() + " from " + oldStatus + " to " + issueStatus);
		return updated;
	}

	@Transactional
	public Issue updateIssue(Long id, Issue updatedData) {
		Issue issue = findIssueById(id);
		if (updatedData.getIssueTitle() != null) issue.setIssueTitle(updatedData.getIssueTitle());
		if (updatedData.getIssueDescriptions() != null) issue.setIssueDescriptions(updatedData.getIssueDescriptions());
		if (updatedData.getIssueType() != null) issue.setIssueType(updatedData.getIssueType());
		if (updatedData.getPriority() != null) issue.setPriority(updatedData.getPriority());
		if (updatedData.getAssigneeEmail() != null) issue.setAssigneeEmail(updatedData.getAssigneeEmail());
		if (updatedData.getLabels() != null) issue.setLabels(updatedData.getLabels());

		Issue saved = issueRepo.save(issue);
		activityLogService.logActivity("ISSUE_UPDATED", "system", "Issue", saved.getId(), "Updated issue " + saved.getIssueKey());
		return saved;
	}

	@Transactional
	public void deleteIssue(Long id) {
		Issue issue = findIssueById(id);
		issueRepo.delete(issue);
		activityLogService.logActivity("ISSUE_DELETED", "system", "Issue", id, "Deleted issue " + issue.getIssueKey());
	}

	@Transactional
	public IssueComment addComment(Long issueId, String authorEmail, String body) {
		Issue issue = findIssueById(issueId);

		IssueComment comment = new IssueComment();
		comment.setIssueId(issue.getId());
		comment.setAuthorEmail(authorEmail != null ? authorEmail : "system");
		comment.setBody(body);

		IssueComment savedComment = issueCommentRepo.save(comment);
		activityLogService.logActivity("COMMENT_ADDED", comment.getAuthorEmail(), "Issue", issue.getId(), "Added comment on " + issue.getIssueKey());
		return savedComment;
	}

	public List<IssueComment> getComments(Long issueId) {
		return issueCommentRepo.findByIssueIdOrderByCreatedAtAsc(issueId);
	}

	@Transactional
	public Sprint createSprint(Sprint sprint) {
		if (sprint.getSprintstate() == null) {
			sprint.setSprintstate(SprintState.PLANNED);
		}
		return sprintRepo.save(sprint);
	}

	public List<Issue> getIssueBySprint(Long sprintId) {
		return issueRepo.findBySprintId(sprintId);
	}

	public List<Issue> search(Map<String, String> filters) {
		List<Issue> issues = issueRepo.findAll();
		if (filters.containsKey("assigneeEmail")) {
			String email = filters.get("assigneeEmail");
			issues = issues.stream().filter(i -> email.equalsIgnoreCase(i.getAssigneeEmail()))
					.collect(Collectors.toList());
		}

		if (filters.containsKey("issueStatus")) {
			try {
				IssueStatus status = IssueStatus.valueOf(filters.get("issueStatus"));
				return issueRepo.findByIssueStatus(status);
			} catch (Exception e) {
				throw new RuntimeException("invalid status in filter");
			}
		}

		if (filters.containsKey("sprint")) {
			Long sprintId = Long.valueOf(filters.get("sprint"));
			return getIssueBySprint(sprintId);
		}

		return issues;
	}
}
