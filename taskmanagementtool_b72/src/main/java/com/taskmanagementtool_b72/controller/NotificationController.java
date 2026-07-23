package com.taskmanagementtool_b72.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taskmanagementtool_b72.entity.Notification;
import com.taskmanagementtool_b72.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@PostMapping
	public ResponseEntity<Notification> create(@RequestBody Notification notification) {
		return ResponseEntity.ok(notificationService.createNotification(notification));
	}

	@GetMapping("/{email}")
	public ResponseEntity<List<Notification>> getNotifications(@PathVariable String email) {
		return ResponseEntity.ok(notificationService.getNotificationsForUser(email));
	}

	@GetMapping("/unread-count/{email}")
	public ResponseEntity<Long> getUnreadCount(@PathVariable String email) {
		return ResponseEntity.ok(notificationService.getUnreadCount(email));
	}

	@PutMapping("/{id}/read")
	public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
		return ResponseEntity.ok(notificationService.markAsRead(id));
	}
}
