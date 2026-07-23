package com.taskmanagementtool_b72.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("/burnDownData/{sprintId}")
	public ResponseEntity<Map<String, Object>> getBurnDownDataReport(@PathVariable Long sprintId) {
		return ResponseEntity.ok(reportService.burnDownData(sprintId));
	}

	@GetMapping("/velocity/{projectId}")
	public ResponseEntity<Map<String, Object>> getVelocityReport(@PathVariable Long projectId) {
		return ResponseEntity.ok(reportService.velocity(projectId));
	}

	@GetMapping("/sprintReport/{sprintId}")
	public ResponseEntity<Map<String, Object>> getSprintReport(@PathVariable Long sprintId) {
		return ResponseEntity.ok(reportService.sprintReport(sprintId));
	}

	@GetMapping("/epicReport/{epicId}")
	public ResponseEntity<Map<String, Object>> getEpicReport(@PathVariable Long epicId) {
		return ResponseEntity.ok(reportService.epicProgressReport(epicId));
	}

	@GetMapping("/workLoadReport/{sprintId}")
	public ResponseEntity<Map<String, Object>> getWorkLoadReport(@PathVariable Long sprintId) {
		return ResponseEntity.ok(reportService.workLoadReport(sprintId));
	}

	@GetMapping("/flowDiagram/{sprintId}")
	public ResponseEntity<Map<String, Object>> getFlowDiagramAboutReport(@PathVariable Long sprintId) {
		return ResponseEntity.ok(reportService.flowDiagramForReport(sprintId));
	}
}
