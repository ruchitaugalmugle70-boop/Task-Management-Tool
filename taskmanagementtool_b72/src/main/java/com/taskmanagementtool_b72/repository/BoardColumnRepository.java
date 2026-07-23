package com.taskmanagementtool_b72.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.BoardColumn;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {

	List<BoardColumn> findByBoardIdOrderByPositionInOrdAsc(Long boardId);
}
