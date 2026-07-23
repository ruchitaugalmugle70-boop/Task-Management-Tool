package com.taskmanagementtool_b72.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.Sprint;
import com.taskmanagementtool_b72.enums.SprintState;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

	List<Sprint> findByProjectId(Long projectId);

	List<Sprint> findBySprintstate(SprintState sprintState);
}
