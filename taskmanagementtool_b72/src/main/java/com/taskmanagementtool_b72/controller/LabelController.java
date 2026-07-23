package com.taskmanagementtool_b72.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taskmanagementtool_b72.entity.Label;
import com.taskmanagementtool_b72.service.LabelService;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

	@Autowired
	private LabelService labelService;

	@PostMapping
	public ResponseEntity<Label> createLabel(@RequestBody Label label) {
		return ResponseEntity.ok(labelService.createLabel(label));
	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<Label>> getLabelsByProject(@PathVariable Long projectId) {
		return ResponseEntity.ok(labelService.getLabelsByProject(projectId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteLabel(@PathVariable Long id) {
		labelService.deleteLabel(id);
		return ResponseEntity.ok("Label deleted");
	}
}
