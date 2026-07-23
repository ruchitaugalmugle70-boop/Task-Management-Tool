package com.taskmanagementtool_b72.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findTop20ByOrderByTimestampDesc();
}
