package com.taskmanagementtool_b72.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.dto.AuthResponseDTO;
import com.taskmanagementtool_b72.dto.LoginRequestDTO;
import com.taskmanagementtool_b72.dto.RegisterRequestDTO;

import com.taskmanagementtool_b72.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user_auth")
@RequiredArgsConstructor
public class UserAuthController {
	
	@Autowired
	private UserAuthService userService;
	
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO>register(@RequestBody RegisterRequestDTO register){
		return ResponseEntity.ok(userService.register(register));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO login) {
		String token = userService.login(login);
		return ResponseEntity.ok(new AuthResponseDTO(token, "Login successful"));
	}
	
	@PostMapping("/forgot_password")
	public ResponseEntity<String>forgotPasswod(@RequestParam String email){
		userService.forgotPassword(email);
		return ResponseEntity.ok("Reset password-Email sent overyour email");
	}
	
	@PostMapping("/reset_password")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
		userService.resetPassword(token, newPassword);
		return ResponseEntity.ok("Password reset successful");
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String>logout(HttpServletRequest request){
		return ResponseEntity.ok(userService.logout(request));
	}
}
