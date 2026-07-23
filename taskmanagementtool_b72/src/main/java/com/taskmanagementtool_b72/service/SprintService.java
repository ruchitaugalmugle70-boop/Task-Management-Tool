package com.taskmanagementtool_b72.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.entity.Sprint;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.enums.SprintState;
import com.taskmanagementtool_b72.repository.IssueRepository;
import com.taskmanagementtool_b72.repository.SprintRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprintService {

	@Autowired
	private SprintRepository sprintRepo;

	@Autowired
	private IssueRepository issueRepo;

	public Sprint createSprint(Sprint sprint) {
		sprint.setSprintstate(SprintState.PLANNED);
		return sprintRepo.save(sprint);
	}

	@Transactional
	public Issue assignIssueToSprint(Long sprintId, Long issueId) {

		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("sprint not found"));

		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new RuntimeException("issue not found"));

		if (sprint.getSprintstate() == SprintState.COMPLETED) {
			throw new RuntimeException("can not add task to complete spirint");
		}

		issue.setSprintId(sprintId);
		return issueRepo.save(issue);
	}

	@Transactional
	public Sprint startSprint(Long sprintId) {

		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("sprint not found"));

		if (sprint.getSprintstate() != SprintState.PLANNED) {
			throw new RuntimeException("sprint can not start");
		}

		sprint.setSprintstate(SprintState.ACTIVE);

		if (sprint.getStartDate() == null) {
			sprint.setStartDate(LocalDateTime.now());
		}

		return sprintRepo.save(sprint);
	}

	@Transactional
	public Sprint closeSrint(Long sprintId) {

		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("sprint not found"));

		sprint.setSprintstate(SprintState.COMPLETED);

		if (sprint.getEndDate() == null) {
			sprint.setEndDate(LocalDateTime.now());
		}

		List<Issue> issue = issueRepo.findBySprintId(sprintId);

		for (Issue i : issue) {
			if (!i.getIssueStatus().name().equals(IssueStatus.DONE.name())) {
				i.setSprintId(null);
				issueRepo.save(i);
			}
		}

		return sprintRepo.save(sprint);
	}

	public List<Sprint> getSprintsByProject(Long projectId) {
		return sprintRepo.findByProjectId(projectId);
	}

	public Map<String, Object> getBurndownData(Long sprintId) {

		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("sprint not found"));

		LocalDateTime startSprint = sprint.getStartDate();
		LocalDateTime closeSprint = sprint.getEndDate() != null ? sprint.getEndDate() : LocalDateTime.now();

		List<Issue> issues = issueRepo.findBySprintId(sprintId);

		int totalTasks = issues.size();

		Map<String, Integer> chart = new LinkedHashMap<>();

		LocalDateTime cursor = startSprint;

		while (!cursor.isAfter(closeSprint)) {

			int completedTask = (int) issues.stream()
					.filter(i -> i.getIssueStatus().name().equals(IssueStatus.DONE.name())).count();

			int remainingTask = totalTasks - completedTask;

			chart.put(cursor.toString(), remainingTask);

			cursor = cursor.plusDays(1);
		}

		Map<String, Object> response = new HashMap<>();

		response.put("sprintId", sprintId);
		response.put("StartDate", startSprint);
		response.put("EndDate", closeSprint);
		response.put("burnDownData", chart);

		return response;
	}
}
