package com.taskmanagementtool_b72.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanagementtool_b72.entity.Board;
import com.taskmanagementtool_b72.entity.BoardCards;
import com.taskmanagementtool_b72.entity.BoardColumn;
import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.repository.BoardCardRepository;
import com.taskmanagementtool_b72.repository.BoardColumnRepository;
import com.taskmanagementtool_b72.repository.BoardRepository;
import com.taskmanagementtool_b72.repository.IssueRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepo;

	@Autowired
	private BoardColumnRepository boardColumnRepo;

	@Autowired
	private BoardCardRepository boardCardRepo;

	@Autowired
	private IssueRepository issueRepo;

	public Board createBoard(Board board) {
		return boardRepo.save(board);
	}

	public BoardColumn createColumn(BoardColumn column) {
		return boardColumnRepo.save(column);
	}

	public Board findById(Long id) {
		return boardRepo.findById(id).orElseThrow(() -> new RuntimeException("board not found"));
	}

	public List<BoardColumn> getColumn(Long boardId) {
		return boardColumnRepo.findByBoardIdOrderByPositionInOrdAsc(boardId);
	}

	public List<BoardCards> getCardsAndColumn(Long boardId, Long columnId) {
		return boardCardRepo.findByBoardIdAndColumnIdOrderByPositionInOrdAsc(boardId, columnId);
	}

	@Transactional
	public BoardCards addIssueToBoard(Long boardId, Long columnId, Long issueId) {

		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new RuntimeException("issue not found"));

		boardCardRepo.findByIssueId(issueId).ifPresent(boardCardRepo::delete);

		BoardColumn column = boardColumnRepo.findById(columnId)
				.orElseThrow(() -> new RuntimeException("column not found"));

		if (column.getWipLimit() != null && column.getWipLimit() > 0) {
			long count = boardCardRepo.countByBoardIdAndColumnId(boardId, columnId);

			if (count >= column.getWipLimit()) {
				throw new RuntimeException("wip limit reached for column:" + column.getName());
			}
		}

		List<BoardCards> existing = boardCardRepo.findByBoardIdAndColumnIdOrderByPositionInOrdAsc(boardId, columnId);

		int position = existing.size();

		BoardCards cards = new BoardCards();
		cards.setBoardId(boardId);
		cards.setColumn(column);
		cards.setIssueId(issueId);
		cards.setPositionInOrd(position);
		boardCardRepo.save(cards);

		if (column.getStatusKey() != null) {
			issue.setIssueStatus(column.getStatusKey());
			issueRepo.save(issue);
		}

		return cards;
	}

	@Transactional
	public void moveCard(Long boardId, Long cardId, Long columnId, int toPosition, String performBy) {

		BoardCards card = boardCardRepo.findById(cardId).orElseThrow(() -> new RuntimeException("Cards not found"));
		BoardColumn from = card.getColumn();
		BoardColumn to = boardColumnRepo.findById(columnId)
				.orElseThrow(() -> new RuntimeException("Column not found"));

		if (to.getWipLimit() != null && to.getWipLimit() > 0) {
			long count = boardCardRepo.countByBoardIdAndColumnId(boardId, columnId);

			if (!Objects.equals(from.getId(), to.getId()) && count >= to.getWipLimit()) {
				throw new RuntimeException("Wip limits exceeded for column:" + to.getName());
			}
		}

		List<BoardCards> fromList = boardCardRepo.findByBoardIdAndColumnIdOrderByPositionInOrdAsc(boardId,
				from.getId());

		for (BoardCards c : fromList) {
			if (c.getPositionInOrd() > card.getPositionInOrd()) {
				c.setPositionInOrd(c.getPositionInOrd() - 1);
				boardCardRepo.save(c);
			}
		}

		List<BoardCards> toList = boardCardRepo.findByBoardIdAndColumnIdOrderByPositionInOrdAsc(boardId, to.getId());

		for (BoardCards c : toList) {
			if (c.getPositionInOrd() >= toPosition) {
				c.setPositionInOrd(c.getPositionInOrd() + 1);
				boardCardRepo.save(c);
			}
		}

		card.setColumn(to);
		card.setPositionInOrd(toPosition);
		boardCardRepo.save(card);

		issueRepo.findById(card.getIssueId()).ifPresent(issue -> {
			if (to.getStatusKey() != null) {
				issue.setIssueStatus(to.getStatusKey());
				issueRepo.save(issue);
			}
		});
	}

	@Transactional
	public void recordColumn(Long boardId, Long columnId, List<Long> orderedCardId) {

		int pos = 0;
		for (Long cardId : orderedCardId) {
			BoardCards card = boardCardRepo.findById(cardId)
					.orElseThrow(() -> new RuntimeException("card not found"));

			card.setPositionInOrd(pos++);
			boardCardRepo.save(card);
		}
	}

	@Transactional
	public void startSprint(Long sprintId) {

	}

	@Transactional
	public void closeSprint(Long sprintId) {

	}
}
