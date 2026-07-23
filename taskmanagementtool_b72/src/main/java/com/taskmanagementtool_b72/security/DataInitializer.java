package com.taskmanagementtool_b72.security;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taskmanagementtool_b72.entity.Board;
import com.taskmanagementtool_b72.entity.BoardCards;
import com.taskmanagementtool_b72.entity.BoardColumn;
import com.taskmanagementtool_b72.entity.Issue;
import com.taskmanagementtool_b72.entity.IssueComment;
import com.taskmanagementtool_b72.entity.Label;
import com.taskmanagementtool_b72.entity.Notification;
import com.taskmanagementtool_b72.entity.Project;
import com.taskmanagementtool_b72.entity.Sprint;
import com.taskmanagementtool_b72.entity.TeamMember;
import com.taskmanagementtool_b72.entity.TimeLog;
import com.taskmanagementtool_b72.entity.UserProfileUpdate;
import com.taskmanagementtool_b72.enums.BoardType;
import com.taskmanagementtool_b72.enums.IssuePriority;
import com.taskmanagementtool_b72.enums.IssueStatus;
import com.taskmanagementtool_b72.enums.IssueType;
import com.taskmanagementtool_b72.enums.SprintState;
import com.taskmanagementtool_b72.enums.TeamRole;
import com.taskmanagementtool_b72.repository.BoardCardRepository;
import com.taskmanagementtool_b72.repository.BoardColumnRepository;
import com.taskmanagementtool_b72.repository.BoardRepository;
import com.taskmanagementtool_b72.repository.IssueCommentRepository;
import com.taskmanagementtool_b72.repository.IssueRepository;
import com.taskmanagementtool_b72.repository.LabelRepository;
import com.taskmanagementtool_b72.repository.NotificationRepository;
import com.taskmanagementtool_b72.repository.ProjectRepository;
import com.taskmanagementtool_b72.repository.SprintRepository;
import com.taskmanagementtool_b72.repository.TeamMemberRepository;
import com.taskmanagementtool_b72.repository.TimeLogRepository;
import com.taskmanagementtool_b72.repository.UserProfileUpdateRepository;
import com.taskmanagementtool_b72.service.ActivityLogService;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private BoardRepository boardRepo;

	@Autowired
	private BoardColumnRepository columnRepo;

	@Autowired
	private BoardCardRepository cardRepo;

	@Autowired
	private SprintRepository sprintRepo;

	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private IssueCommentRepository commentRepo;

	@Autowired
	private UserProfileUpdateRepository userProfileRepo;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private LabelRepository labelRepo;

	@Autowired
	private TimeLogRepository timeLogRepo;

	@Autowired
	private NotificationRepository notificationRepo;

	@Autowired
	private TeamMemberRepository teamMemberRepo;

	@Override
	public void run(String... args) throws Exception {
		if (projectRepo.count() == 0) {
			// 1. Seed Projects
			Project p1 = Project.builder()
					.projectKey("TMT")
					.projectName("Task Management Tool Core Engine")
					.leadEmail("lead.dev@taskmanagement.com")
					.description("Core Agile Issue Tracker, Board, and Sprint Execution Engine.")
					.createdAt(LocalDateTime.now().minusDays(15))
					.build();
			projectRepo.save(p1);

			Project p2 = Project.builder()
					.projectKey("INT")
					.projectName("Integrations & Webhooks Module")
					.leadEmail("integrations@taskmanagement.com")
					.description("GitHub, Slack, and REST Webhooks integration layer.")
					.createdAt(LocalDateTime.now().minusDays(10))
					.build();
			projectRepo.save(p2);

			Project p3 = Project.builder()
					.projectKey("SEC")
					.projectName("Enterprise Security & Audit Suite")
					.leadEmail("security@taskmanagement.com")
					.description("OAuth2, Single Sign-On, and System Audit Logs.")
					.createdAt(LocalDateTime.now().minusDays(5))
					.build();
			projectRepo.save(p3);
		}

		// 2. Seed User Profile
		if (userProfileRepo.count() == 0) {
			UserProfileUpdate user = UserProfileUpdate.builder()
					.userEmail("admin.lead@taskmanagement.com")
					.userName("Alex Mercer")
					.department("Engineering")
					.designation("Principal Architect")
					.organizationName("TaskManagement Enterprise")
					.active(true)
					.createdAt(LocalDateTime.now().minusDays(20))
					.build();
			userProfileRepo.save(user);
		}

		// 3. Seed Board & Agile Data
		if (boardRepo.count() == 0) {
			Board board = Board.builder()
					.name("TMT Core Agile Kanban Board")
					.projectKey("TMT")
					.boardTpe(BoardType.KANBAN)
					.createdAt(LocalDateTime.now().minusDays(15))
					.build();
			boardRepo.save(board);

			// 4. Seed Board Columns
			BoardColumn colTodo = BoardColumn.builder()
					.board(board)
					.name("To Do")
					.statusKey(IssueStatus.TODO)
					.positionInOrd(0)
					.wipLimit(10)
					.build();
			columnRepo.save(colTodo);

			BoardColumn colInProg = BoardColumn.builder()
					.board(board)
					.name("In Progress")
					.statusKey(IssueStatus.IN_PROGRESS)
					.positionInOrd(1)
					.wipLimit(5)
					.build();
			columnRepo.save(colInProg);

			BoardColumn colReview = BoardColumn.builder()
					.board(board)
					.name("In Review")
					.statusKey(IssueStatus.IN_REVIEW)
					.positionInOrd(2)
					.wipLimit(4)
					.build();
			columnRepo.save(colReview);

			BoardColumn colDone = BoardColumn.builder()
					.board(board)
					.name("Done")
					.statusKey(IssueStatus.DONE)
					.positionInOrd(3)
					.wipLimit(20)
					.build();
			columnRepo.save(colDone);

			// 5. Seed Sprints
			Sprint sprint1 = Sprint.builder()
					.sprintName("Sprint 1 - Core Features")
					.startDate(LocalDateTime.now().minusDays(7))
					.endDate(LocalDateTime.now().plusDays(7))
					.sprintstate(SprintState.ACTIVE)
					.goal("Deliver Kanban Board and Backlog Management APIs")
					.creaedat(LocalDateTime.now().minusDays(10))
					.projectId(1L)
					.build();
			sprintRepo.save(sprint1);

			Sprint sprint2 = Sprint.builder()
					.sprintName("Sprint 2 - Security & Analytics")
					.startDate(LocalDateTime.now().plusDays(8))
					.endDate(LocalDateTime.now().plusDays(22))
					.sprintstate(SprintState.PLANNED)
					.goal("Deliver Audit Streams and Analytics Reports")
					.creaedat(LocalDateTime.now().minusDays(2))
					.projectId(1L)
					.build();
			sprintRepo.save(sprint2);

			// 6. Seed Issues with Labels
			Issue epic1 = Issue.builder()
					.issueKey("TMT-1")
					.issueTitle("Agile Task Management Architecture")
					.issueDescriptions("Design and implement the high-throughput task management data layer.")
					.issueType(IssueType.EPIC)
					.priority(IssuePriority.HIGH)
					.issueStatus(IssueStatus.IN_PROGRESS)
					.assigneeEmail("admin.lead@taskmanagement.com")
					.reporterEmail("manager@taskmanagement.com")
					.projectId(1L)
					.sprintId(sprint1.getId())
					.labels("Architecture,Backend")
					.build();
			issueRepo.save(epic1);

			Issue story1 = Issue.builder()
					.issueKey("TMT-2")
					.issueTitle("Implement Interactive Kanban Board UI")
					.issueDescriptions("Build a sleek light-themed Drag and Drop Kanban Board with WIP limits.")
					.issueType(IssueType.STORY)
					.priority(IssuePriority.HIGH)
					.issueStatus(IssueStatus.IN_PROGRESS)
					.assigneeEmail("admin.lead@taskmanagement.com")
					.reporterEmail("product@taskmanagement.com")
					.epicId(epic1.getId())
					.projectId(1L)
					.sprintId(sprint1.getId())
					.labels("Frontend,UI/UX")
					.build();
			issueRepo.save(story1);

			Issue story2 = Issue.builder()
					.issueKey("TMT-3")
					.issueTitle("Backlog Drag-and-Drop Reordering")
					.issueDescriptions("Allow team members to drag and prioritize issues within project backlog.")
					.issueType(IssueType.STORY)
					.priority(IssuePriority.MEDIUM)
					.issueStatus(IssueStatus.TODO)
					.assigneeEmail("dev1@taskmanagement.com")
					.reporterEmail("product@taskmanagement.com")
					.epicId(epic1.getId())
					.projectId(1L)
					.backLogPosition(0)
					.labels("Frontend")
					.build();
			issueRepo.save(story2);

			Issue bug1 = Issue.builder()
					.issueKey("TMT-4")
					.issueTitle("Fix CORS Header Mapping for REST endpoints")
					.issueDescriptions("Ensure WebMvcConfigurer allows preflight OPTIONS requests across all subdomains.")
					.issueType(IssueType.BUG)
					.priority(IssuePriority.CRITICAL)
					.issueStatus(IssueStatus.DONE)
					.assigneeEmail("admin.lead@taskmanagement.com")
					.reporterEmail("qa@taskmanagement.com")
					.projectId(1L)
					.sprintId(sprint1.getId())
					.labels("Security,Urgent")
					.build();
			issueRepo.save(bug1);

			// 7. Seed Board Cards
			BoardCards card1 = BoardCards.builder()
					.boardId(board.getId())
					.column(colInProg)
					.issueId(story1.getId())
					.positionInOrd(0)
					.build();
			cardRepo.save(card1);

			BoardCards card2 = BoardCards.builder()
					.boardId(board.getId())
					.column(colDone)
					.issueId(bug1.getId())
					.positionInOrd(0)
					.build();
			cardRepo.save(card2);

			// 8. Seed Issue Comments
			IssueComment comment1 = IssueComment.builder()
					.issueId(story1.getId())
					.authorEmail("admin.lead@taskmanagement.com")
					.body("Kanban board column WIP limits logic has been implemented and tested.")
					.createdAt(LocalDateTime.now().minusHours(4))
					.build();
			commentRepo.save(comment1);
		}

		// 9. Seed Labels
		if (labelRepo.count() == 0) {
			Label l1 = Label.builder().name("Frontend").color("#4f46e5").projectId(1L).build();
			Label l2 = Label.builder().name("Backend").color("#10b981").projectId(1L).build();
			Label l3 = Label.builder().name("Architecture").color("#8b5cf6").projectId(1L).build();
			Label l4 = Label.builder().name("Urgent").color("#f43f5e").projectId(1L).build();
			Label l5 = Label.builder().name("UI/UX").color("#0ea5e9").projectId(1L).build();
			labelRepo.save(l1); labelRepo.save(l2); labelRepo.save(l3); labelRepo.save(l4); labelRepo.save(l5);
		}

		// 10. Seed Time Logs
		if (timeLogRepo.count() == 0) {
			TimeLog t1 = TimeLog.builder().issueId(2L).userEmail("admin.lead@taskmanagement.com").hoursLogged(4.5).description("Implemented drag-and-drop event handlers").loggedAt(LocalDateTime.now().minusDays(2)).build();
			TimeLog t2 = TimeLog.builder().issueId(2L).userEmail("admin.lead@taskmanagement.com").hoursLogged(3.0).description("Styled board columns and WIP badges").loggedAt(LocalDateTime.now().minusDays(1)).build();
			TimeLog t3 = TimeLog.builder().issueId(4L).userEmail("admin.lead@taskmanagement.com").hoursLogged(2.0).description("Debugged preflight OPTIONS CORS headers").loggedAt(LocalDateTime.now().minusDays(3)).build();
			timeLogRepo.save(t1); timeLogRepo.save(t2); timeLogRepo.save(t3);
		}

		// 11. Seed Notifications
		if (notificationRepo.count() == 0) {
			Notification n1 = Notification.builder().recipientEmail("admin.lead@taskmanagement.com").message("You were assigned to TMT-2 (Kanban Board UI)").type("ASSIGNMENT").isRead(false).createdAt(LocalDateTime.now().minusHours(5)).build();
			Notification n2 = Notification.builder().recipientEmail("admin.lead@taskmanagement.com").message("TMT-4 CORS Header Mapping was marked DONE").type("STATUS_CHANGE").isRead(false).createdAt(LocalDateTime.now().minusHours(2)).build();
			Notification n3 = Notification.builder().recipientEmail("admin.lead@taskmanagement.com").message("New comment added on TMT-2").type("COMMENT").isRead(true).createdAt(LocalDateTime.now().minusHours(1)).build();
			notificationRepo.save(n1); notificationRepo.save(n2); notificationRepo.save(n3);
		}

		// 12. Seed Team Members
		if (teamMemberRepo.count() == 0) {
			TeamMember tm1 = TeamMember.builder().projectId(1L).userEmail("admin.lead@taskmanagement.com").userName("Alex Mercer").role(TeamRole.LEAD).joinedAt(LocalDateTime.now().minusDays(15)).build();
			TeamMember tm2 = TeamMember.builder().projectId(1L).userEmail("dev1@taskmanagement.com").userName("Sarah Jenkins").role(TeamRole.DEVELOPER).joinedAt(LocalDateTime.now().minusDays(12)).build();
			TeamMember tm3 = TeamMember.builder().projectId(1L).userEmail("qa@taskmanagement.com").userName("David Kim").role(TeamRole.TESTER).joinedAt(LocalDateTime.now().minusDays(10)).build();
			TeamMember tm4 = TeamMember.builder().projectId(1L).userEmail("product@taskmanagement.com").userName("Elena Rostova").role(TeamRole.MANAGER).joinedAt(LocalDateTime.now().minusDays(14)).build();
			teamMemberRepo.save(tm1); teamMemberRepo.save(tm2); teamMemberRepo.save(tm3); teamMemberRepo.save(tm4);
		}

		// 13. Seed Activity Stream
		activityLogService.logActivity("PROJECT_CREATED", "system", "Project", 1L, "Project TMT Core Platform initialized.");
		activityLogService.logActivity("BOARD_CREATED", "admin.lead@taskmanagement.com", "Board", 1L, "Created TMT Core Agile Kanban Board.");
		activityLogService.logActivity("SPRINT_STARTED", "admin.lead@taskmanagement.com", "Sprint", 1L, "Sprint 1 started with 3 issues.");
		activityLogService.logActivity("ISSUE_COMPLETED", "admin.lead@taskmanagement.com", "Issue", 4L, "Resolved TMT-4 CORS Header Mapping.");
	}
}
