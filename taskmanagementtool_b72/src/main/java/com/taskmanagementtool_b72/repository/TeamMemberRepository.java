package com.taskmanagementtool_b72.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taskmanagementtool_b72.entity.TeamMember;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
	List<TeamMember> findByProjectId(Long projectId);
}
