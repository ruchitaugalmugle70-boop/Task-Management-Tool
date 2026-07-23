package com.taskmanagementtool_b72.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanagementtool_b72.entity.BoardCards;

@Repository
public interface BoardCardRepository extends JpaRepository<BoardCards, Long> {

	List<BoardCards> findByBoardIdAndColumnIdOrderByPositionInOrdAsc(Long boardId, Long columnId);

	long countByBoardIdAndColumnId(Long boardId, Long columnId);

	Optional<BoardCards> findByIssueId(Long issueId);
}
