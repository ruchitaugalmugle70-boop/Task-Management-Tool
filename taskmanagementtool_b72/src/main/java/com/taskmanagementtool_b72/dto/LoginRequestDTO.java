package com.taskmanagementtool_b72.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

	public String userOfficialEmail;
	public String password;
	
	
}
