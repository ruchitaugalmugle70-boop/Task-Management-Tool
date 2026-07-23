package com.taskmanagementtool_b72.service;


import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.dto.AuthResponseDTO;
import com.taskmanagementtool_b72.dto.LoginRequestDTO;
import com.taskmanagementtool_b72.dto.RegisterRequestDTO;
import com.taskmanagementtool_b72.entity.UserAuth;
import com.taskmanagementtool_b72.repository.UserAuthRepository;
import com.taskmanagementtool_b72.security.JWTUtil;
import com.taskmanagementtool_b72.security.TokenBlockService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserAuthService {
	
	@Autowired
	private UserAuthRepository userRepo;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailLogService emailService;
	
	@Autowired
	private TokenBlockService tokenBlock;
	
	public AuthResponseDTO register(RegisterRequestDTO register) {
		
		
		Optional<UserAuth>exist=userRepo.findByUserOfficialEmail(register.userOfficialEmail);
		if(exist.isPresent()) {
			throw new RuntimeException("User already exist");
		}
		
		UserAuth user= new UserAuth();
		
		user.setUserName(register.userName);
		user.setUserOfficialEmail(register.userOfficialEmail);
		user.setPassword(passwordEncoder.encode(register.password));
		user.setRole(register.role);
		
		userRepo.save(user);
		
		String token = jwtUtil.generateToken(user);
		return new AuthResponseDTO(token,"user registered successfully");
	}
	
	
	public String login(LoginRequestDTO login) {
		
		UserAuth user = userRepo.findByUserOfficialEmail(login.userOfficialEmail)
				             .orElseThrow(()-> new RuntimeException("User not found"));
		
		if(!passwordEncoder.matches(login.password, user.getPassword())) {
			throw new RuntimeException("invalid credential");
		}
		
		return jwtUtil.generateToken(user);
		
	}
	
	
	public void forgotPassword(String email) {
		
		UserAuth user= userRepo.findByUserOfficialEmail(email).orElseThrow(()-> new RuntimeException("user not found"));
		
		String token= UUID.randomUUID().toString();
		
		user.setResetToken(token);
		user.setResetTokenExpire(new Date(System.currentTimeMillis()+10*60*1000));
		
		userRepo.save(user);
		
		System.out.println("FORGOT PASSWORD TOKEN: " + token);
		
		try {
			emailService.sentResetPasswordEmail(email, token);
		} catch (Exception e) {
			System.err.println("Failed to send email: " + e.getMessage());
			System.out.println("TESTING MODE: Continuing without email. Use the token above to reset password.");
		}
		
	}
	
	
	public void resetPassword(String token, String newPassword) {
		
		UserAuth user= userRepo.findByResetToken(token).orElseThrow(()-> new RuntimeException("Invalid token"));
		
		if(user.getResetTokenExpire().before(new Date())) {
			
			throw new RuntimeException("Token got expired");
		}
		
		user.setPassword(passwordEncoder.encode(newPassword) );
		user.setResetToken(null);
		user.setResetTokenExpire(null);
		
		userRepo.save(user);
		
	}
	
	public String logout(HttpServletRequest request) {
		String header= request.getHeader("Authorization");
		String token= jwtUtil.extractToken(header);
		
		if(token!=null) {
			tokenBlock.blockListToken(token);
		}
		
		return "Logged Out Successfully ";
	}
	

}
