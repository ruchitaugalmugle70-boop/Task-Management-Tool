package com.taskmanagementtool_b72.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanagementtool_b72.entity.Board;
import com.taskmanagementtool_b72.entity.BoardColumn;
import com.taskmanagementtool_b72.service.BoardService;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

	@Autowired
	private BoardService boardService;

	@PostMapping("/create_Board")
	public ResponseEntity<Board> createBoard(@RequestBody Board board) {
		return ResponseEntity.ok(boardService.createBoard(board));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Board> getBoard(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.findById(id));
	}

	@GetMapping("/{id}/column")
	public ResponseEntity<List<BoardColumn>> getColumn(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.getColumn(id));
	}

	@PostMapping("/{id}/columns")
	public ResponseEntity<BoardColumn> addColumn(@PathVariable Long id, @RequestBody BoardColumn column) {
		column.setBoard(boardService.findById(id));
		return ResponseEntity.ok(boardService.createColumn(column));
	}

	@PostMapping("/{id}/cards/{cardId}/move")
	public ResponseEntity<String> moveCards(@PathVariable Long id,
			@PathVariable Long cardId,
			@RequestBody Map<String, Object> body,
			@RequestHeader(value = "Ex_User_Email", required = false) String user) {
		Long toColumnId = Long.valueOf(String.valueOf(body.get("toColumnId")));
		int toPosition = Integer.valueOf(String.valueOf(body.getOrDefault("toPosition", "0")));
		boardService.moveCard(id, cardId, toColumnId, toPosition, user == null ? "system" : user);
		return ResponseEntity.ok("Moved");
	}

	@PostMapping("/{id}/columns/{columnId}/record")
	public ResponseEntity<String> recordColumns(@PathVariable Long id,
			@PathVariable Long columnId,
			@RequestBody List<Long> orderCardIds) {
		boardService.recordColumn(id, columnId, orderCardIds);
		return ResponseEntity.ok("Recorded");

	}

	@PostMapping("/sprint/{sprintId}/start")
	public ResponseEntity<String> startSprint(@PathVariable Long sprintId) {
		boardService.startSprint(sprintId);
		return ResponseEntity.ok("Sprint Started");
	}

	@PostMapping("/sprint/{sprintId}/complete")
	public ResponseEntity<String> completeSprint(@PathVariable Long sprintId) {
		boardService.closeSprint(sprintId);
		return ResponseEntity.ok("Sprint Completed");
	}
}
