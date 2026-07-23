package com.taskmanagementtool_b72.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

	Optional<Board> findByProjectKey(String projectKey);
}
