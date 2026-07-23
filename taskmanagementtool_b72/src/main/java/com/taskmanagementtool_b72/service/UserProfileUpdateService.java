package com.taskmanagementtool_b72.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.dto.UserProfileUpdateDTO;
import com.taskmanagementtool_b72.entity.UserProfileUpdate;
import com.taskmanagementtool_b72.repository.UserProfileUpdateRepository;

@Service
public class UserProfileUpdateService {

	@Autowired
	private UserProfileUpdateRepository userProfileUpdateRepo;

	public UserProfileUpdateDTO updateUserProfile(UserProfileUpdateDTO userProfile) {
		return updateUserProfile(userProfile, null);
	}

	public UserProfileUpdateDTO updateUserProfile(UserProfileUpdateDTO userProfile, String oldEmail) {
		String lookupEmail = (oldEmail != null && !oldEmail.trim().isEmpty()) ? oldEmail : userProfile.getUserEmail();

		UserProfileUpdate profile = userProfileUpdateRepo.findByUserEmail(lookupEmail).orElse(null);

		if (profile == null) {
			// Create new if profile doesn't exist
			profile = new UserProfileUpdate();
		} else if (oldEmail != null && !oldEmail.equalsIgnoreCase(userProfile.getUserEmail())) {
			// Email is changing: verify new email isn't taken by another account
			if (userProfileUpdateRepo.findByUserEmail(userProfile.getUserEmail()).isPresent()) {
				throw new RuntimeException("Email already in use: " + userProfile.getUserEmail());
			}
		}

		profile.setUserName(userProfile.getUserName());
		profile.setUserEmail(userProfile.getUserEmail());
		profile.setDepartment(userProfile.getDepartment());
		profile.setDesignation(userProfile.getDesignation());
		profile.setOrganizationName(userProfile.getOrganizationName());
		profile.setActive(true);

		userProfileUpdateRepo.save(profile);
		return toDto(profile);
	}

	public UserProfileUpdateDTO getProfileByEmail(String userEmail) {
		UserProfileUpdate user = userProfileUpdateRepo.findByUserEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("profile not found"));
		return toDto(user);
	}

	public List<UserProfileUpdateDTO> getAllProfile() {
		return userProfileUpdateRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	private UserProfileUpdateDTO toDto(UserProfileUpdate user) {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setUserName(user.getUserName());
		dto.setUserEmail(user.getUserEmail());
		dto.setDepartment(user.getDepartment());
		dto.setDesignation(user.getDesignation());
		dto.setOrganizationName(user.getOrganizationName());
		dto.setActive(user.isActive());
		return dto;
	}
}
