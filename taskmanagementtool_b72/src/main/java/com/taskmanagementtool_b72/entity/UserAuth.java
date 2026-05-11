package com.taskmanagementtool_b72.entity;

import java.util.Date;

import com.taskmanagementtool_b72.enums.Role;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="user_auth")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserAuth {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String userName;
	
	@Column(nullable=false,unique=true)
	private String userOfficialEmail;
	
	@Column(nullable=false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	private String resetToken;
	private Date resetTokenExpire;
//	public UserAuth() {}
	
//	public UserAuth(Long id,String userName,String userOfficialEmail,String password,Role role) {
//		this.id=id;
//		this.userName=userName;
//		this.userOfficialEmail=userOfficialEmail;
//		this.password=password;
//		this.role=role;
//		
//	}
	
	
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserOfficialEmail() {
		return userOfficialEmail;
	}

	public void setUserOfficialEmail(String userOfficialEmail) {
		this.userOfficialEmail = userOfficialEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Date getResetTokenExpire() {
		return resetTokenExpire;
	}

	public void setResetTokenExpire(Date resetTokenExpire) {
		this.resetTokenExpire = resetTokenExpire;
	}
	
	
	
	

}