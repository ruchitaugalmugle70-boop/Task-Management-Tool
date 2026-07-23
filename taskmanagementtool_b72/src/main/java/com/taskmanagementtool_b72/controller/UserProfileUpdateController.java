package com.taskmanagementtool_b72.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.dto.UserProfileUpdateDTO;
import com.taskmanagementtool_b72.service.UserProfileUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user_profile_update")
@RequiredArgsConstructor
public class UserProfileUpdateController {

	@Autowired
	private UserProfileUpdateService userProfileUpdateService;

	@PutMapping("/user_profile/update")
	public ResponseEntity<UserProfileUpdateDTO> updateUserProfile(
			@RequestBody UserProfileUpdateDTO userProfileUpdate,
			@RequestParam(required = false) String oldEmail) {
		return ResponseEntity.ok(userProfileUpdateService.updateUserProfile(userProfileUpdate, oldEmail));
	}

	@GetMapping("/{email}")
	public ResponseEntity<UserProfileUpdateDTO> getUserProfileByEmail(@PathVariable String email) {
		return ResponseEntity.ok(userProfileUpdateService.getProfileByEmail(email));
	}

	@GetMapping("/all")
	public ResponseEntity<List<UserProfileUpdateDTO>> getAllUserProfile() {
		return ResponseEntity.ok(userProfileUpdateService.getAllProfile());
	}
}
