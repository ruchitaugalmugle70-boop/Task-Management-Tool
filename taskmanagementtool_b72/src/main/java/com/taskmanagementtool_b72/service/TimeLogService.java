package com.taskmanagementtool_b72.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taskmanagementtool_b72.entity.TimeLog;
import com.taskmanagementtool_b72.repository.TimeLogRepository;

@Service
public class TimeLogService {

	@Autowired
	private TimeLogRepository timeLogRepo;

	public TimeLog logTime(TimeLog timeLog) {
		return timeLogRepo.save(timeLog);
	}

	public List<TimeLog> getTimeLogsByIssue(Long issueId) {
		return timeLogRepo.findByIssueId(issueId);
	}

	public List<TimeLog> getTimeLogsByUser(String userEmail) {
		return timeLogRepo.findByUserEmail(userEmail);
	}

	public List<TimeLog> getAllTimeLogs() {
		return timeLogRepo.findAll();
	}
}
