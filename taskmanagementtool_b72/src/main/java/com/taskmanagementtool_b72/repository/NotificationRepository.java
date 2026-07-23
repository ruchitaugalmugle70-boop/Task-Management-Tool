package com.taskmanagementtool_b72.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taskmanagementtool_b72.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);
	long countByRecipientEmailAndIsReadFalse(String recipientEmail);
}
