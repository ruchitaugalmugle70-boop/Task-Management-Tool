package com.taskmanagementtool_b72.entity;

import java.time.LocalDateTime;

import com.taskmanagementtool_b72.enums.BoardType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String projectKey;

	@Enumerated(EnumType.STRING)
	private BoardType boardTpe;

	private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public BoardType getBoardTpe() {
		return boardTpe;
	}

	public void setBoardTpe(BoardType boardTpe) {
		this.boardTpe = boardTpe;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
