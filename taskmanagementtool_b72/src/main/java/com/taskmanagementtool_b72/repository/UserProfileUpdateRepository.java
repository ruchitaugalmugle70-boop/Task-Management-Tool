package com.taskmanagementtool_b72.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.UserProfileUpdate;

@Repository
public interface UserProfileUpdateRepository extends JpaRepository<UserProfileUpdate, Long> {

	Optional<UserProfileUpdate> findByUserEmail(String userEmail);
}
