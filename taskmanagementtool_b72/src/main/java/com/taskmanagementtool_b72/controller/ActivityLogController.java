package com.taskmanagementtool_b72.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.entity.ActivityLog;
import com.taskmanagementtool_b72.service.ActivityLogService;

@RestController
@RequestMapping("/api/activities")
public class ActivityLogController {

	@Autowired
	private ActivityLogService activityLogService;

	@GetMapping
	public ResponseEntity<List<ActivityLog>> getRecentActivities() {
		return ResponseEntity.ok(activityLogService.getRecentActivities());
	}
}
