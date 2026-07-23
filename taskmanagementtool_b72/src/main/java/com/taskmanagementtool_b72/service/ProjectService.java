package com.taskmanagementtool_b72.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.entity.Project;
import com.taskmanagementtool_b72.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepo;

	public Project createProject(Project project) {
		if (project.getProjectKey() != null && projectRepo.findByProjectKey(project.getProjectKey()).isPresent()) {
			throw new RuntimeException("Project key already exists: " + project.getProjectKey());
		}
		return projectRepo.save(project);
	}

	public List<Project> getAllProjects() {
		return projectRepo.findAll();
	}

	public Project getProjectById(Long id) {
		return projectRepo.findById(id).orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
	}

	public Project getProjectByKey(String projectKey) {
		return projectRepo.findByProjectKey(projectKey)
				.orElseThrow(() -> new RuntimeException("Project not found with key: " + projectKey));
	}
}
