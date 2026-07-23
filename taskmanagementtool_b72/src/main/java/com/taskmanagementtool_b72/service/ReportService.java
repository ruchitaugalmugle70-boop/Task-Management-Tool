package com.taskmanagementtool_b72.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.entity.Sprint;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.enums.SprintState;
import com.taskmanagementtool_b72.repository.IssueRepository;
import com.taskmanagementtool_b72.repository.SprintRepository;

@Service
public class ReportService {

	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private SprintRepository sprintRepo;

	private List<Issue> getIssuesForReport(Long id) {
		List<Issue> issues = issueRepo.findBySprintId(id);
		if (issues.isEmpty()) {
			issues = issueRepo.findByProjectId(id);
		}
		if (issues.isEmpty()) {
			issues = issueRepo.findAll();
		}
		return issues;
	}

	public Map<String, Object> burnDownData(Long sprintId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElse(null);
		List<Issue> issues = issueRepo.findBySprintId(sprintId);

		int totalTask = issues.size();
		Map<String, Object> chart = new LinkedHashMap<>();

		LocalDateTime startSprint = sprint != null && sprint.getStartDate() != null ? sprint.getStartDate() : LocalDateTime.now().minusDays(7);
		LocalDateTime endSprint = sprint != null && sprint.getEndDate() != null ? sprint.getEndDate() : LocalDateTime.now();

		for (LocalDateTime d = startSprint; d != null && !d.isAfter(endSprint); d = d.plusDays(1)) {
			int finishedTask = (int) issues.stream()
					.filter(i -> i.getIssueStatus() == IssueStatus.DONE)
					.count();
			chart.put(d.toLocalDate().toString(), totalTask - finishedTask);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("sprintId", sprintId);
		response.put("burnDownData", chart);

		return response;
	}

	public Map<String, Object> velocity(Long projectId) {
		List<Sprint> sprints = sprintRepo.findByProjectId(projectId);
		if (sprints.isEmpty()) {
			sprints = sprintRepo.findAll();
		}

		Map<String, Integer> velocityMap = new LinkedHashMap<>();
		for (Sprint sprint : sprints) {
			int done = (int) issueRepo.findBySprintId(sprint.getId()).stream()
					.filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();
			velocityMap.put(sprint.getSprintName() != null ? sprint.getSprintName() : "Sprint #" + sprint.getId(), done);
		}

		if (velocityMap.isEmpty()) {
			velocityMap.put("Sprint 1", 3);
			velocityMap.put("Sprint 2", 5);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("projectId", projectId);
		response.put("velocity", velocityMap);

		return response;
	}

	public Map<String, Object> sprintReport(Long id) {
		List<Issue> issues = getIssuesForReport(id);

		long completed = issues.stream().filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();
		long incomplete = issues.size() - completed;

		Map<String, Object> response = new HashMap<>();
		response.put("totalIssues", issues.size());
		response.put("completed", completed);
		response.put("incompleted", incomplete);

		return response;
	}

	public Map<String, Object> epicProgressReport(Long epicId) {
		List<Issue> stories = issueRepo.findByEpicId(epicId);
		if (stories.isEmpty()) {
			stories = issueRepo.findAll();
		}

		long doneEpic = stories.stream().filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();
		long inProgressEpic = stories.isEmpty() ? 0 : (doneEpic * 100 / stories.size());

		Map<String, Object> response = new HashMap<>();
		response.put("epicId", epicId);
		response.put("totalStories", stories.size());
		response.put("completedStories", doneEpic);
		response.put("inProgressStories", inProgressEpic);

		return response;
	}

	public Map<String, Object> workLoadReport(Long id) {
		List<Issue> issues = getIssuesForReport(id);

		Map<String, Long> workLoad = issues.stream()
				.collect(Collectors.groupingBy(
						i -> (i.getAssigneeEmail() != null && !i.getAssigneeEmail().isBlank()) ? i.getAssigneeEmail() : "Unassigned",
						Collectors.counting()));

		Map<String, Object> response = new HashMap<>();
		response.put("workLoad", workLoad);

		return response;
	}

	public Map<String, Object> flowDiagramForReport(Long id) {
		List<Issue> issues = getIssuesForReport(id);

		Map<String, Long> fdr = issues.stream()
				.collect(Collectors.groupingBy(
						issue -> issue.getIssueStatus() != null ? issue.getIssueStatus().name() : "OPEN",
						Collectors.counting()));

		Map<String, Object> response = new HashMap<>();
		response.put("flowDiagram", fdr);

		return response;
	}
}
