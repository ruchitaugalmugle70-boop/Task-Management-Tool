package com.taskmanagementtool_b72.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_cards", indexes = { @Index(name = "idx_card_board_col", columnList = "boardId,column_id,positionInOrd") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCards {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "column_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private BoardColumn column;

	private Long issueId;
	private Long boardId;
	private Integer positionInOrd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BoardColumn getColumn() {
		return column;
	}

	public void setColumn(BoardColumn column) {
		this.column = column;
	}

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public Integer getPositionInOrd() {
		return positionInOrd;
	}

	public void setPositionInOrd(Integer positionInOrd) {
		this.positionInOrd = positionInOrd;
	}
}
