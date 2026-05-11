package com.taskmanagementtool_b72.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.dto.EmailLogDTO;
import com.taskmanagementtool_b72.entity.EmailLog;
import com.taskmanagementtool_b72.repository.EmailLogRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailLogService {
	
	@Autowired
	private EmailLogRepository emailRepo;
	
	
	@Autowired
	private JavaMailSender mailSender;
	
	public String sendEmail(EmailLogDTO email) {
		
		boolean sentStatus= false;
		
		try {
			
			MimeMessage message= mailSender.createMimeMessage();
			MimeMessageHelper helper= new MimeMessageHelper(message,true);
			
			helper.setTo(email.getRecipientEmail());
			helper.setSubject(email.getSubject());
			helper.setText(email.getBody(),true);
			mailSender.send(message);
			
			sentStatus= true;
			
			
		} catch (MessagingException e) {
			
			sentStatus=false;
		}
		
		EmailLog log= new EmailLog(email.getRecipientEmail(),email.getSubject(),email.getBody(),sentStatus);
		emailRepo.save(log);
		
		return sentStatus ? "Email sent successfully":"Email sending failed";
	}

	
	public void sentResetPasswordEmail(String to,String token) {
		String resetLink= "http://localhost:7878/auth/reset=password?token="+token;
		
		SimpleMailMessage message= new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Reset your Password");
		message.setText("Click the link to reset your password:\n"+resetLink);
		mailSender.send(message);
		
	}
}
