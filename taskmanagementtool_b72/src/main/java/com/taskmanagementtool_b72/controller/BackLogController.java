package com.taskmanagementtool_b72.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.service.BackLogService;

@RestController
@RequestMapping("/api/backLog")
public class BackLogController {

	@Autowired
	private BackLogService backLogService;

	@GetMapping("/{projectId}")
	public ResponseEntity<List<Issue>> getBackLog(@PathVariable Long projectId) {
		return ResponseEntity.ok(backLogService.getBackLog(projectId));
	}

	@PostMapping("/{projectId}/record")
	public ResponseEntity<String> recoder(@PathVariable Long projectId, @RequestBody List<Long> orderedIssueId) {
		backLogService.recorderBackLog(projectId, orderedIssueId);
		return ResponseEntity.ok("Backlog recorded");
	}

	@PutMapping("/add-to-sprint/{issueId}/{sprintId}")
	public ResponseEntity<Issue> addIssueToSprint(@PathVariable Long issueId, @PathVariable Long sprintId) {
		return ResponseEntity.ok(backLogService.addIssueToSprint(issueId, sprintId));
	}

	@GetMapping("/{projectId}/hieracrchy")
	public ResponseEntity<Map<String, Object>> getHierarchy(@PathVariable Long projectId) {
		return ResponseEntity.ok(backLogService.getBackLogHierArchy(projectId));
	}
}
