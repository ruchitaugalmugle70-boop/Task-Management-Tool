package com.taskmanagementtool_b72.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taskmanagementtool_b72.entity.Notification;
import com.taskmanagementtool_b72.repository.NotificationRepository;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepo;

	public Notification createNotification(Notification notification) {
		return notificationRepo.save(notification);
	}

	public List<Notification> getNotificationsForUser(String recipientEmail) {
		return notificationRepo.findByRecipientEmailOrderByCreatedAtDesc(recipientEmail);
	}

	public long getUnreadCount(String recipientEmail) {
		return notificationRepo.countByRecipientEmailAndIsReadFalse(recipientEmail);
	}

	public Notification markAsRead(Long id) {
		Notification n = notificationRepo.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
		n.setRead(true);
		return notificationRepo.save(n);
	}
}
