package com.taskmanagementtool_b72.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.entity.IssueComment;
import com.taskmanagementtool_b72.entity.Sprint;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.service.IssueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

	@Autowired
	private IssueService issueService;

	@PostMapping("/create")
	public ResponseEntity<Issue> createIssue(@RequestBody Issue issue) {
		return ResponseEntity.ok(issueService.createIssue(issue));
	}

	@GetMapping("/assignee/{assigneeEmail}")
	public ResponseEntity<List<Issue>> getIssueByAssigneedEmail(@PathVariable String assigneeEmail) {
		return ResponseEntity.ok(issueService.findIssueByAssigneeEmail(assigneeEmail));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Issue> getIssueById(@PathVariable Long id) {
		return ResponseEntity.ok(issueService.findIssueById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Issue> updateIssue(@PathVariable Long id, @RequestBody Issue issue) {
		return ResponseEntity.ok(issueService.updateIssue(id, issue));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteIssue(@PathVariable Long id) {
		issueService.deleteIssue(id);
		return ResponseEntity.ok("Issue deleted");
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Issue> updateIssueStatus(@RequestParam IssueStatus issueStatus, @PathVariable Long id) {
		return ResponseEntity.ok(issueService.updateIssueStatus(id, issueStatus));
	}

	@GetMapping("/{id}/comments")
	public ResponseEntity<List<IssueComment>> getComments(@PathVariable Long id) {
		return ResponseEntity.ok(issueService.getComments(id));
	}

	@PostMapping("/addComments/{issueId}")
	public ResponseEntity<IssueComment> addCommentsLegacy(@PathVariable Long issueId,
			@RequestParam String authorEmail,
			@RequestBody String body) {
		return ResponseEntity.ok(issueService.addComment(issueId, authorEmail, body));
	}

	@PostMapping("/{id}/comments")
	public ResponseEntity<IssueComment> addComment(@PathVariable Long id,
			@RequestBody Map<String, String> payload) {
		String authorEmail = payload.getOrDefault("authorEmail", "admin.lead@taskmanagement.com");
		String body = payload.getOrDefault("body", "");
		return ResponseEntity.ok(issueService.addComment(id, authorEmail, body));
	}

	@PostMapping("/sprint")
	public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint) {
		return ResponseEntity.ok(issueService.createSprint(sprint));
	}

	@GetMapping("/sprint/{sprintId}")
	public ResponseEntity<List<Issue>> getIssueBySprint(@PathVariable Long sprintId) {
		return ResponseEntity.ok(issueService.findIssueBySprint(sprintId));
	}

	@PostMapping("/search")
	public ResponseEntity<List<Issue>> search(@RequestBody Map<String, String> filters) {
		return ResponseEntity.ok(issueService.search(filters));
	}
}
