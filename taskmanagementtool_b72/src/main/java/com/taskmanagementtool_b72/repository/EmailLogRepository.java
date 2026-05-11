package com.taskmanagementtool_b72.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.EmailLog;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog,Long>{
	

}