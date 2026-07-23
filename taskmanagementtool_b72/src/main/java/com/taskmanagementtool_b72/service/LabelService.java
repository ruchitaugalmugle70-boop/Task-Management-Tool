package com.taskmanagementtool_b72.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taskmanagementtool_b72.entity.Label;
import com.taskmanagementtool_b72.repository.LabelRepository;

@Service
public class LabelService {

	@Autowired
	private LabelRepository labelRepo;

	public Label createLabel(Label label) {
		return labelRepo.save(label);
	}

	public List<Label> getLabelsByProject(Long projectId) {
		return labelRepo.findByProjectId(projectId);
	}

	public void deleteLabel(Long id) {
		labelRepo.deleteById(id);
	}
}
