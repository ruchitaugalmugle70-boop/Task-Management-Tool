package com.taskmanagementtool_b72.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taskmanagementtool_b72.entity.TeamMember;
import com.taskmanagementtool_b72.repository.TeamMemberRepository;

@Service
public class TeamMemberService {

	@Autowired
	private TeamMemberRepository teamMemberRepo;

	public TeamMember addMember(TeamMember member) {
		return teamMemberRepo.save(member);
	}

	public List<TeamMember> getMembersByProject(Long projectId) {
		return teamMemberRepo.findByProjectId(projectId);
	}

	public void removeMember(Long id) {
		teamMemberRepo.deleteById(id);
	}
}
