package com.taskmanagementtool_b72.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.UserAuth;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth,Long> {
	
	Optional<UserAuth>findByUserOfficialEmail(String userOfficilEmail);
	Optional<UserAuth>findByResetToken(String resetToken);

}
