package com.taskmanagementtool_b72.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class JWTAuthFilter extends OncePerRequestFilter{
	
	@Autowired
	private JWTUtil jwtUtil;
	
	
	@Autowired
	private CustomUserDetailsService customeUserDetails;
	

	@Autowired
	private TokenBlockService tokenblock;
	
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filter) 
			          throws ServletException,IOException{
		
		String header = request.getHeader("Authorization");
		String token= null;
		
		if(StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		if(token!=null && tokenblock.isBlockToken(token)) {
			
		
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    return;
			}
		
		if(token!=null && jwtUtil.validateToken(token) && SecurityContextHolder.getContext().getAuthentication()==null) {
			
		
			String userEmail= jwtUtil.getUserEmail(token);
			
			try {
				UserDetails userDetails= customeUserDetails.loadUserByEmail(userEmail);
				UsernamePasswordAuthenticationToken auth= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	}
//			UserDetails userDetails= customeUserDetails.loadUserByEmail(userEmail);
//			UsernamePasswordAuthenticationToken auth= 
//					new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//			
//			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//			SecurityContextHolder.getContext().setAuthentication(auth);
//		}
//			
		filter.doFilter(request, response);
	}	

}
