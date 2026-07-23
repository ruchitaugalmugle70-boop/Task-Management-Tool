package com.taskmanagementtool_b72.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.entity.ActivityLog;
import com.taskmanagementtool_b72.repository.ActivityLogRepository;

@Service
public class ActivityLogService {

	@Autowired
	private ActivityLogRepository activityLogRepo;

	public ActivityLog logActivity(String action, String performedBy, String entityType, Long entityId, String details) {
		ActivityLog log = new ActivityLog();
		log.setAction(action);
		log.setPerformedBy(performedBy != null ? performedBy : "system");
		log.setEntityType(entityType);
		log.setEntityId(entityId);
		log.setDetails(details);
		return activityLogRepo.save(log);
	}

	public List<ActivityLog> getRecentActivities() {
		return activityLogRepo.findTop20ByOrderByTimestampDesc();
	}
}
