package com.taskmanagementtool_b72.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taskmanagementtool_b72.entity.TeamMember;
import com.taskmanagementtool_b72.service.TeamMemberService;

@RestController
@RequestMapping("/api/teams")
public class TeamMemberController {

	@Autowired
	private TeamMemberService teamMemberService;

	@PostMapping
	public ResponseEntity<TeamMember> addMember(@RequestBody TeamMember member) {
		return ResponseEntity.ok(teamMemberService.addMember(member));
	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<TeamMember>> getMembersByProject(@PathVariable Long projectId) {
		return ResponseEntity.ok(teamMemberService.getMembersByProject(projectId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> removeMember(@PathVariable Long id) {
		teamMemberService.removeMember(id);
		return ResponseEntity.ok("Member removed");
	}
}
