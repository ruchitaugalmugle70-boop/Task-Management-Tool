package com.taskmanagementtool_b72.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	Optional<Project> findByProjectKey(String projectKey);
}
