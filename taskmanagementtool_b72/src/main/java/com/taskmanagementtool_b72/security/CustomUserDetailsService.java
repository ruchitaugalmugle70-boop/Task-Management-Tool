package com.taskmanagementtool_b72.security;

import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.taskmanagementtool_b72.entity.UserAuth;
import com.taskmanagementtool_b72.enums.Permission;
import com.taskmanagementtool_b72.repository.UserAuthRepository;



@Service
public class CustomUserDetailsService  implements UserDetailsService{
	
	@Autowired
	private UserAuthRepository userRepo;
	
	public UserDetails loadUserByEmail(String userOfficialEmail) throws Exception{
		
		UserAuth user= userRepo.findByUserOfficialEmail(userOfficialEmail)
				         .orElseThrow(()->new RuntimeException("user notfound") );
		
		Set<Permission>perms= RolePermissionConfig.getRoleBasedPermission().get(user.getRole());
//		List<GrantedAuthority>authorities= (perms== null? List.class:perms.
//				                            stream().map(p-> new SimpleGrantedAuthority(p.name())).collect(Collectors.toList()));
//		
		
		
		return new org.springframework.security.core.userdetails.User(user.getUserOfficialEmail(), user.getPassword(), null);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
		try {
			return loadUserByEmail(username);
		} catch (Exception e) {
			throw new org.springframework.security.core.userdetails.UsernameNotFoundException(e.getMessage());
		}
	}
}
