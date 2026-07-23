package com.taskmanagementtool_b72.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taskmanagementtool_b72.entity.TimeLog;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
	List<TimeLog> findByIssueId(Long issueId);
	List<TimeLog> findByUserEmail(String userEmail);
}
