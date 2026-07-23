package com.taskmanagementtool_b72.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taskmanagementtool_b72.entity.Label;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
	List<Label> findByProjectId(Long projectId);
}
