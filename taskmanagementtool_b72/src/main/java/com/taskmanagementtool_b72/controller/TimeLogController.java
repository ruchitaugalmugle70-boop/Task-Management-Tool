package com.taskmanagementtool_b72.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taskmanagementtool_b72.entity.TimeLog;
import com.taskmanagementtool_b72.service.TimeLogService;

@RestController
@RequestMapping("/api/timelogs")
public class TimeLogController {

	@Autowired
	private TimeLogService timeLogService;

	@PostMapping
	public ResponseEntity<TimeLog> logTime(@RequestBody TimeLog timeLog) {
		return ResponseEntity.ok(timeLogService.logTime(timeLog));
	}

	@GetMapping("/issue/{issueId}")
	public ResponseEntity<List<TimeLog>> getTimeLogsByIssue(@PathVariable Long issueId) {
		return ResponseEntity.ok(timeLogService.getTimeLogsByIssue(issueId));
	}

	@GetMapping("/user/{userEmail}")
	public ResponseEntity<List<TimeLog>> getTimeLogsByUser(@PathVariable String userEmail) {
		return ResponseEntity.ok(timeLogService.getTimeLogsByUser(userEmail));
	}

	@GetMapping
	public ResponseEntity<List<TimeLog>> getAllTimeLogs() {
		return ResponseEntity.ok(timeLogService.getAllTimeLogs());
	}
}
