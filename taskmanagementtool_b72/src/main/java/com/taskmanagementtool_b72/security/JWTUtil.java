package com.taskmanagementtool_b72.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.taskmanagementtool_b72.entity.UserAuth;
import com.taskmanagementtool_b72.enums.Permission;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
	
	
	private final Key key;
	private final long validateToken= 1000L*60*60*12;
	
	public JWTUtil() {
		
		String secret=System.getenv("JWT_SECRET");
		if(secret==null || secret.isEmpty()) {
			secret="Replace this with some secret key";
			
		}
		key= Keys.hmacShaKeyFor(secret.getBytes());
	}

	
	
	public String generateToken(UserAuth user) {
		
		Map<String,Object>claims= new HashMap<>();
		claims.put("role",user.getRole().name());
		
		Set<Permission>perms= RolePermissionConfig.getRoleBasedPermission().get(user.getRole());
		
//		List <String>permNames= perms==null ? List.class:perms.stream().map(Enum::name).collect(Collectors.toList());
		
		Date now= new Date();
		Date expire= new Date(now.getTime()+validateToken);
		
		return Jwts.builder().
				setClaims(claims).
				setSubject(user.getUserOfficialEmail())
				.setIssuedAt(now)
				.setExpiration(expire).signWith(key,SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
	
	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
	public String getUserEmail(String token) {
		return getClaims(token).getSubject();
	}
	
	public String extractToken(String header) {
		if(header !=null && header.startsWith("Barear ")) {
			return header.substring(7);
		}
		return null;
	}
}
