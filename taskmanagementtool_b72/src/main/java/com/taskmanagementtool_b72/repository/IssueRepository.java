package com.taskmanagementtool_b72.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.enums.IssueStatus;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

	Optional<Issue> findByIssueKey(String issueKey);

	List<Issue> findByAssigneeEmail(String assigneeEmail);

	List<Issue> findByIssueStatus(IssueStatus issueStatus);

	List<Issue> findBySprintId(Long sprintId);

	List<Issue> findByProjectId(Long projectId);

	List<Issue> findByEpicId(Long epicId);

	List<Issue> findByProjectIdAndSprintIdIsNullOrderByBackLogPositionAsc(Long projectId);
}
