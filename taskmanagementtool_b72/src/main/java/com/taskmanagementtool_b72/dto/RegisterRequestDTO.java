package com.taskmanagementtool_b72.dto;

import com.taskmanagementtool_b72.enums.Role;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
	
	public String userName;
	public String userOfficcialEmail;
	public String password;
	public Role role;
	
	

}
