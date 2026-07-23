package com.taskmanagementtool_b72.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.entity.Project;
import com.taskmanagementtool_b72.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@PostMapping
	public ResponseEntity<Project> createProject(@RequestBody Project project) {
		return ResponseEntity.ok(projectService.createProject(project));
	}

	@GetMapping
	public ResponseEntity<List<Project>> getAllProjects() {
		return ResponseEntity.ok(projectService.getAllProjects());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
		return ResponseEntity.ok(projectService.getProjectById(id));
	}

	@GetMapping("/key/{projectKey}")
	public ResponseEntity<Project> getProjectByKey(@PathVariable String projectKey) {
		return ResponseEntity.ok(projectService.getProjectByKey(projectKey));
	}
}
