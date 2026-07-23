/* ============================================================
   TaskFlow Enterprise - Next-Gen Single Page Application Engine
   ============================================================ */

const API_BASE = '/api';
let projects = [];
let currentProject = null;
let allIssues = [];
let allSprints = [];
let allLabels = [];

// Initialize Application on DOM Ready
document.addEventListener('DOMContentLoaded', async () => {
  console.log('TaskFlow Enterprise Engine Booting...');
  await loadProjects();
  await loadProfile();
  await loadNotifications();
  await loadDashboard();
});

// Navigation Tab Switching
function switchTab(tabId) {
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));

  const activeNav = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
  if (activeNav) activeNav.classList.add('active');

  const activePane = document.getElementById(`tab-${tabId}`);
  if (activePane) activePane.classList.add('active');

  if (tabId === 'dashboard') loadDashboard();
  else if (tabId === 'kanban') loadKanban();
  else if (tabId === 'backlog') loadBacklog();
  else if (tabId === 'labels') loadLabels();
  else if (tabId === 'timelogs') loadTimeLogs();
  else if (tabId === 'teams') loadTeamMembers();
  else if (tabId === 'activities') loadActivities();
  else if (tabId === 'reports') loadReports();
  else if (tabId === 'profile') loadProfile();
}

// Modal Controllers
function openModal(id) { document.getElementById(id).classList.add('active'); }
function closeModal(id) { document.getElementById(id).classList.remove('active'); }

// Toast Alert System
function showToast(message, type = 'success') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-xmark'}" style="color: ${type === 'success' ? 'var(--emerald)' : 'var(--rose)'};"></i> ${message}`;
  container.appendChild(toast);
  setTimeout(() => { toast.style.opacity = '0'; setTimeout(() => toast.remove(), 300); }, 3000);
}

// 1. PROJECTS MODULE
async function loadProjects() {
  try {
    const res = await fetch(`${API_BASE}/projects`);
    if (res.ok) {
      projects = await res.json();
      if (!currentProject && projects.length > 0) currentProject = projects[0];
      renderProjectSelect();
    }
  } catch (err) { console.error('Error loading projects:', err); }
}

function renderProjectSelect() {
  const select = document.getElementById('projectSelect');
  if (!select) return;
  select.innerHTML = projects.map(p =>
    `<option value="${p.id}" ${currentProject?.id === p.id ? 'selected' : ''}>[${p.projectKey}] ${p.projectName}</option>`
  ).join('');
}

function onProjectChanged() {
  const select = document.getElementById('projectSelect');
  currentProject = projects.find(p => p.id === parseInt(select.value));
  const activeTab = document.querySelector('.tab-pane.active')?.id?.replace('tab-', '');
  if (activeTab) switchTab(activeTab);
}

async function submitProject(e) {
  e.preventDefault();
  const payload = {
    projectKey: document.getElementById('newProjKey').value,
    projectName: document.getElementById('newProjName').value,
    leadEmail: document.getElementById('newProjLead').value,
    description: document.getElementById('newProjDesc').value
  };

  const res = await fetch(`${API_BASE}/projects`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal('createProjectModal');
    showToast('Project created successfully!');
    await loadProjects();
    switchTab('dashboard');
  }
}

// 2. DASHBOARD OVERVIEW
async function loadDashboard() {
  await loadProjects();
  try {
    const issuesRes = await fetch(`${API_BASE}/issues/search`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({})
    });
    if (issuesRes.ok) {
      allIssues = await issuesRes.json();
      document.getElementById('statTotal').textContent = allIssues.length;
      document.getElementById('statInProgress').textContent = allIssues.filter(i => i.issueStatus === 'IN_PROGRESS').length;
      document.getElementById('statCompleted').textContent = allIssues.filter(i => i.issueStatus === 'DONE').length;
    }

    const projId = currentProject ? currentProject.id : 1;
    const sprintsRes = await fetch(`${API_BASE}/sprints/project/${projId}`);
    if (sprintsRes.ok) {
      allSprints = await sprintsRes.json();
      document.getElementById('statSprints').textContent = allSprints.filter(s => s.sprintstate === 'ACTIVE').length || allSprints.length;
    }

    const teamRes = await fetch(`${API_BASE}/teams/project/${projId}`);
    if (teamRes.ok) {
      const members = await teamRes.json();
      document.getElementById('statMembers').textContent = members.length;
    }

    const tbody = document.getElementById('projectsBody');
    if (tbody) {
      tbody.innerHTML = projects.map(p => `
        <tr>
          <td><span class="badge badge-story">${p.projectKey}</span></td>
          <td><strong>${p.projectName}</strong></td>
          <td>${p.leadEmail || 'N/A'}</td>
          <td>${new Date(p.createdAt || Date.now()).toLocaleDateString()}</td>
        </tr>
      `).join('');
    }
  } catch (err) { console.error('Error loading dashboard:', err); }
}

// 3. KANBAN BOARD
async function loadKanban() {
  const boardContainer = document.getElementById('kanbanBoard');
  if (!boardContainer) return;

  try {
    const colsRes = await fetch(`${API_BASE}/boards/1/column`);
    const columns = colsRes.ok ? await colsRes.json() : [];

    const issuesRes = await fetch(`${API_BASE}/issues/search`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({})
    });
    allIssues = issuesRes.ok ? await issuesRes.json() : [];

    boardContainer.innerHTML = columns.map(col => {
      const colIssues = allIssues.filter(i => i.issueStatus === col.statusKey);
      return `
        <div class="kanban-column" ondragover="allowDrop(event)" ondrop="dropCard(event, '${col.statusKey}')">
          <div class="col-header">
            <div class="col-title">
              <i class="fa-solid fa-circle" style="font-size: 8px; color: var(--primary);"></i> ${col.name}
            </div>
            <span class="col-count">${colIssues.length} / ${col.wipLimit || '∞'}</span>
          </div>
          <div class="cards-container">
            ${colIssues.map(issue => {
              const labelHtml = issue.labels ? issue.labels.split(',').map(l =>
                `<span class="label-pill" style="background: var(--primary);">${l.trim()}</span>`
              ).join('') : '';

              return `
                <div class="kanban-card" draggable="true" ondragstart="dragCard(event, ${issue.id})" onclick="openTaskDetails(${issue.id})">
                  <div class="card-key">${issue.issueKey || 'TMT-' + issue.id}</div>
                  <div class="card-title">${issue.issueTitle}</div>
                  <div>${labelHtml}</div>
                  <div class="card-meta">
                    <span class="badge badge-${(issue.issueType || 'TASK').toLowerCase()}">${issue.issueType || 'TASK'}</span>
                    <span class="badge badge-${(issue.priority || 'MEDIUM').toLowerCase()}">${issue.priority || 'MEDIUM'}</span>
                  </div>
                </div>
              `;
            }).join('')}
          </div>
        </div>
      `;
    }).join('');
  } catch (err) { console.error('Error loading Kanban board:', err); }
}

let draggedCardId = null;
function dragCard(e, issueId) { draggedCardId = issueId; }
function allowDrop(e) { e.preventDefault(); }
async function dropCard(e, targetStatus) {
  e.preventDefault();
  if (!draggedCardId) return;

  await fetch(`${API_BASE}/issues/${draggedCardId}/status?issueStatus=${targetStatus}`, { method: 'PUT' });
  showToast(`Task updated to ${targetStatus}`);
  draggedCardId = null;
  await loadKanban();
}

// 4. CREATE TASK
async function submitIssue(e) {
  e.preventDefault();
  const payload = {
    issueTitle: document.getElementById('newIssueTitle').value,
    issueType: document.getElementById('newIssueType').value,
    priority: document.getElementById('newIssuePriority').value,
    assigneeEmail: document.getElementById('newIssueAssignee').value,
    labels: document.getElementById('newIssueLabels').value,
    issueDescriptions: document.getElementById('newIssueDesc').value,
    projectId: currentProject ? currentProject.id : 1
  };

  const res = await fetch(`${API_BASE}/issues/create`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal('createIssueModal');
    showToast('Task created successfully!');
    await loadKanban();
  }
}

// 5. BACKLOG & SPRINTS
async function loadBacklog() {
  const projId = currentProject ? currentProject.id : 1;
  try {
    const sprintsRes = await fetch(`${API_BASE}/sprints/project/${projId}`);
    if (sprintsRes.ok) {
      allSprints = await sprintsRes.json();
      renderSprintsTable(allSprints);
    }

    const backlogRes = await fetch(`${API_BASE}/backLog/${projId}`);
    if (backlogRes.ok) {
      const backlogItems = await backlogRes.json();
      renderBacklogTable(backlogItems);
    }
  } catch (err) { console.error('Error loading Backlog & Sprints:', err); }
}

function renderSprintsTable(sprints) {
  const tbody = document.getElementById('sprintsBody');
  if (!tbody) return;
  if (sprints.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" class="empty-state" style="padding: 20px;">No sprints found. Create a sprint to get started!</td></tr>';
    return;
  }

  tbody.innerHTML = sprints.map(s => {
    let actionBtn = '';
    let stateBadge = '';

    if (s.sprintstate === 'ACTIVE') {
      stateBadge = `<span class="badge badge-active">ACTIVE</span>`;
      actionBtn = `<button class="btn btn-danger btn-sm" style="padding: 4px 10px; font-size: 11px;" onclick="completeSprint(${s.id})"><i class="fa-solid fa-flag-checkered"></i> Complete Sprint</button>`;
    } else if (s.sprintstate === 'PLANNED') {
      stateBadge = `<span class="badge badge-planned">PLANNED</span>`;
      actionBtn = `<button class="btn btn-success btn-sm" style="padding: 4px 10px; font-size: 11px;" onclick="startSprint(${s.id})"><i class="fa-solid fa-play"></i> Start Sprint</button>`;
    } else {
      stateBadge = `<span class="badge badge-completed">COMPLETED</span>`;
      actionBtn = `<span style="font-size: 11px; color: var(--text-muted); font-weight: 700;"><i class="fa-solid fa-check"></i> Completed</span>`;
    }

    return `
      <tr>
        <td><strong>${s.sprintName}</strong></td>
        <td>${stateBadge}</td>
        <td>${s.goal || 'N/A'}</td>
        <td>${s.startDate ? new Date(s.startDate).toLocaleDateString() : 'N/A'}</td>
        <td>${s.endDate ? new Date(s.endDate).toLocaleDateString() : 'N/A'}</td>
        <td>${actionBtn}</td>
      </tr>
    `;
  }).join('');
}

function renderBacklogTable(items) {
  const tbody = document.getElementById('backlogBody');
  if (!tbody) return;
  if (items.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" class="empty-state" style="padding: 20px;">Backlog is clear! All tasks are in active sprints.</td></tr>';
    return;
  }

  const activeSprint = allSprints.find(s => s.sprintstate === 'ACTIVE') || allSprints[0];
  const activeSprintId = activeSprint ? activeSprint.id : 1;
  const activeSprintName = activeSprint ? activeSprint.sprintName : 'Sprint 1';

  tbody.innerHTML = items.map(item => `
    <tr>
      <td><strong>${item.issueKey || 'TMT-' + item.id}</strong></td>
      <td>${item.issueTitle}</td>
      <td><span class="badge badge-${(item.issueType || 'TASK').toLowerCase()}">${item.issueType}</span></td>
      <td><span class="badge badge-${(item.priority || 'MEDIUM').toLowerCase()}">${item.priority}</span></td>
      <td>${item.labels ? item.labels : '—'}</td>
      <td>${item.assigneeEmail || 'Unassigned'}</td>
      <td><button class="btn btn-outline btn-sm" style="padding: 4px 10px; font-size: 11px;" onclick="assignToSprint(${item.id}, ${activeSprintId})">+ Assign to ${activeSprintName}</button></td>
    </tr>
  `).join('');
}

async function startSprint(sprintId) {
  try {
    const res = await fetch(`${API_BASE}/sprints/start/${sprintId}`, { method: 'PUT' });
    if (res.ok) {
      showToast('Sprint started successfully!');
      await loadBacklog();
      await loadDashboard();
    } else {
      showToast('Sprint could not be started', 'error');
    }
  } catch (err) { showToast('Error starting sprint', 'error'); }
}

async function completeSprint(sprintId) {
  if (!confirm('Are you sure you want to complete this sprint? Any unfinished tasks will be moved back to the backlog.')) return;
  try {
    const res = await fetch(`${API_BASE}/sprints/close/${sprintId}`, { method: 'PUT' });
    if (res.ok) {
      showToast('Sprint completed successfully!');
      await loadBacklog();
      await loadDashboard();
    } else {
      showToast('Failed to complete sprint', 'error');
    }
  } catch (err) { showToast('Error completing sprint', 'error'); }
}

async function assignToSprint(issueId, sprintId = 1) {
  try {
    const res = await fetch(`${API_BASE}/backLog/add-to-sprint/${issueId}/${sprintId}`, { method: 'PUT' });
    if (res.ok) {
      showToast('Task assigned to sprint!');
      await loadBacklog();
    }
  } catch (err) { showToast('Error assigning task to sprint', 'error'); }
}

async function submitSprint(e) {
  e.preventDefault();
  const payload = {
    sprintName: document.getElementById('newSprintName').value,
    goal: document.getElementById('newSprintGoal').value,
    projectId: currentProject ? currentProject.id : 1
  };

  const res = await fetch(`${API_BASE}/sprints/create`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal('createSprintModal');
    showToast('Sprint started!');
    await loadBacklog();
  }
}

// 6. LABELS & TAGS MODULE
async function loadLabels() {
  const projId = currentProject ? currentProject.id : 1;
  try {
    const res = await fetch(`${API_BASE}/labels/project/${projId}`);
    if (res.ok) {
      allLabels = await res.json();
      const tbody = document.getElementById('labelsBody');
      if (tbody) {
        tbody.innerHTML = allLabels.map(l => `
          <tr>
            <td><span class="label-pill" style="background: ${l.color}; font-size: 12px; padding: 4px 12px;">${l.name}</span></td>
            <td><strong>${l.name}</strong></td>
            <td><code>${l.color}</code></td>
            <td><button class="btn btn-outline" style="padding: 4px 10px; font-size: 11px; color: var(--rose);" onclick="deleteLabel(${l.id})">Delete</button></td>
          </tr>
        `).join('');
      }
    }
  } catch (err) { console.error('Error loading labels:', err); }
}

async function submitLabel(e) {
  e.preventDefault();
  const payload = {
    name: document.getElementById('newLabelName').value,
    color: document.getElementById('newLabelColor').value,
    projectId: currentProject ? currentProject.id : 1
  };

  const res = await fetch(`${API_BASE}/labels`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal('createLabelModal');
    showToast('Label created!');
    await loadLabels();
  }
}

async function deleteLabel(id) {
  await fetch(`${API_BASE}/labels/${id}`, { method: 'DELETE' });
  showToast('Label deleted');
  await loadLabels();
}

// 7. TIME TRACKING MODULE
async function loadTimeLogs() {
  try {
    const res = await fetch(`${API_BASE}/timelogs`);
    if (res.ok) {
      const logs = await res.json();
      const tbody = document.getElementById('timelogsBody');
      if (tbody) {
        tbody.innerHTML = logs.map(t => `
          <tr>
            <td><strong>Task #${t.issueId}</strong></td>
            <td>${t.userEmail}</td>
            <td><span class="badge badge-story">${t.hoursLogged} hrs</span></td>
            <td>${t.description || 'N/A'}</td>
            <td>${new Date(t.loggedAt || Date.now()).toLocaleString()}</td>
          </tr>
        `).join('');
      }
    }
  } catch (err) { console.error('Error loading time logs:', err); }
}

async function submitTimeLog(e) {
  e.preventDefault();
  const payload = {
    issueId: parseInt(document.getElementById('timeLogIssueId').value),
    hoursLogged: parseFloat(document.getElementById('timeLogHours').value),
    userEmail: document.getElementById('timeLogUser').value,
    description: document.getElementById('timeLogDesc').value
  };

  const res = await fetch(`${API_BASE}/timelogs`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal('logTimeModal');
    showToast('Time logged successfully!');
    await loadTimeLogs();
  }
}

// 8. TEAM MEMBERS MODULE
async function loadTeamMembers() {
  const projId = currentProject ? currentProject.id : 1;
  try {
    const res = await fetch(`${API_BASE}/teams/project/${projId}`);
    if (res.ok) {
      const members = await res.json();
      const tbody = document.getElementById('teamBody');
      if (tbody) {
        tbody.innerHTML = members.map(m => `
          <tr>
            <td><strong>${m.userName}</strong></td>
            <td>${m.userEmail}</td>
            <td><span class="badge badge-story">${m.role}</span></td>
            <td>${new Date(m.joinedAt || Date.now()).toLocaleDateString()}</td>
            <td><button class="btn btn-outline" style="padding: 4px 10px; font-size: 11px; color: var(--rose);" onclick="removeTeamMember(${m.id})">Remove</button></td>
          </tr>
        `).join('');
      }
    }
  } catch (err) { console.error('Error loading team members:', err); }
}

async function submitTeamMember(e) {
  e.preventDefault();
  const payload = {
    userName: document.getElementById('newMemberName').value,
    userEmail: document.getElementById('newMemberEmail').value,
    role: document.getElementById('newMemberRole').value,
    projectId: currentProject ? currentProject.id : 1
  };

  const res = await fetch(`${API_BASE}/teams`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal('addTeamMemberModal');
    showToast('Team member added!');
    await loadTeamMembers();
  }
}

async function removeTeamMember(id) {
  await fetch(`${API_BASE}/teams/${id}`, { method: 'DELETE' });
  showToast('Member removed');
  await loadTeamMembers();
}

// 9. NOTIFICATIONS MODULE
async function loadNotifications() {
  try {
    const res = await fetch(`${API_BASE}/notifications/admin.lead@taskmanagement.com`);
    if (res.ok) {
      const notifs = await res.json();
      const unreadCount = notifs.filter(n => !n.read).length;
      document.getElementById('notifBadge').textContent = unreadCount;

      const list = document.getElementById('notifList');
      if (list) {
        list.innerHTML = notifs.map(n => `
          <div class="notif-item ${!n.read ? 'unread' : ''}" onclick="markNotificationRead(${n.id})">
            <div>${n.message}</div>
            <div class="notif-time">${new Date(n.createdAt || Date.now()).toLocaleTimeString()}</div>
          </div>
        `).join('');
      }
    }
  } catch (err) { console.error('Error loading notifications:', err); }
}

function toggleNotifications() {
  const dd = document.getElementById('notifDropdown');
  dd.classList.toggle('active');
}

async function markNotificationRead(id) {
  await fetch(`${API_BASE}/notifications/${id}/read`, { method: 'PUT' });
  await loadNotifications();
}

// 10. AUDIT ACTIVITY STREAM
async function loadActivities() {
  try {
    const res = await fetch(`${API_BASE}/activities`);
    if (res.ok) {
      const activities = await res.json();
      const tbody = document.getElementById('activitiesBody');
      if (tbody) {
        tbody.innerHTML = activities.map(act => `
          <tr>
            <td><span class="badge badge-story">${act.action}</span></td>
            <td><strong>${act.performedBy}</strong></td>
            <td>${act.entityType || 'System'} #${act.entityId || ''}</td>
            <td>${act.details || ''}</td>
            <td>${new Date(act.timestamp || Date.now()).toLocaleTimeString()}</td>
          </tr>
        `).join('');
      }
    }
  } catch (err) { console.error('Error loading activities:', err); }
}

// 11. REPORTS & ANALYTICS
async function loadReports() {
  const projId = currentProject ? currentProject.id : 1;
  try {
    const wlRes = await fetch(`${API_BASE}/reports/workLoadReport/${projId}`);
    if (wlRes.ok) {
      const data = await wlRes.json();
      const workloadMap = data.workLoad || {};
      const view = document.getElementById('workloadReport');
      if (view) {
        const entries = Object.entries(workloadMap);
        if (entries.length === 0) {
          view.innerHTML = '<p style="font-size: 13px; color: var(--text-muted); padding: 12px 0;">No workload data logged yet.</p>';
        } else {
          view.innerHTML = entries.map(([user, count]) => `
            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13px; padding: 10px 0; border-bottom: 1px solid var(--border-color);">
              <span><strong>${user}</strong></span>
              <span class="badge badge-story">${count} Tasks Assigned</span>
            </div>
          `).join('');
        }
      }
    }

    const flowRes = await fetch(`${API_BASE}/reports/flowDiagram/${projId}`);
    if (flowRes.ok) {
      const data = await flowRes.json();
      const flowMap = data.flowDiagram || {};
      const view = document.getElementById('flowReport');
      if (view) {
        const entries = Object.entries(flowMap);
        if (entries.length === 0) {
          view.innerHTML = '<p style="font-size: 13px; color: var(--text-muted); padding: 12px 0;">No status data logged yet.</p>';
        } else {
          view.innerHTML = entries.map(([status, count]) => `
            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13px; padding: 10px 0; border-bottom: 1px solid var(--border-color);">
              <span><strong>${status}</strong></span>
              <span class="badge badge-task">${count} Issues</span>
            </div>
          `).join('');
        }
      }
    }

    const velRes = await fetch(`${API_BASE}/reports/velocity/${projId}`);
    if (velRes.ok) {
      const data = await velRes.json();
      const velMap = data.velocity || {};
      const view = document.getElementById('velocityReport');
      if (view) {
        const entries = Object.entries(velMap);
        if (entries.length === 0) {
          view.innerHTML = '<p style="font-size: 13px; color: var(--text-muted); padding: 12px 0;">No velocity data logged yet.</p>';
        } else {
          view.innerHTML = entries.map(([sprint, count]) => `
            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13px; padding: 10px 0; border-bottom: 1px solid var(--border-color);">
              <span><strong>${sprint}</strong></span>
              <span class="badge badge-active">${count} Done Issues Completed</span>
            </div>
          `).join('');
        }
      }
    }
  } catch (err) { console.error('Error loading reports:', err); }
}

// 12. TASK DETAILS & COMMENTS POPUP
let currentDetailIssueId = null;

async function openTaskDetails(issueId) {
  currentDetailIssueId = issueId;
  try {
    const res = await fetch(`${API_BASE}/issues/${issueId}`);
    if (res.ok) {
      const issue = await res.json();
      document.getElementById('detailIssueKey').textContent = issue.issueKey || 'TMT-' + issue.id;
      document.getElementById('detailIssueTitle').textContent = issue.issueTitle;
      document.getElementById('detailDesc').textContent = issue.issueDescriptions || 'No description provided.';
      document.getElementById('detailAssignee').textContent = issue.assigneeEmail || 'Unassigned';
      document.getElementById('detailReporter').textContent = issue.reporterEmail || 'System';

      document.getElementById('detailBadges').innerHTML = `
        <span class="badge badge-${(issue.issueType || 'TASK').toLowerCase()}">${issue.issueType}</span>
        <span class="badge badge-${(issue.priority || 'MEDIUM').toLowerCase()}">${issue.priority}</span>
        <span class="badge badge-active">${issue.issueStatus}</span>
      `;

      await loadTaskComments(issueId);
      openModal('taskDetailModal');
    }
  } catch (err) { console.error('Error loading issue details:', err); }
}

async function loadTaskComments(issueId) {
  try {
    const res = await fetch(`${API_BASE}/issues/${issueId}/comments`);
    const commentsContainer = document.getElementById('detailComments');
    if (res.ok && commentsContainer) {
      const comments = await res.json();
      if (comments.length === 0) {
        commentsContainer.innerHTML = '<p style="font-size: 12px; color: var(--text-muted);">No comments yet. Start the conversation!</p>';
      } else {
        commentsContainer.innerHTML = comments.map(c => `
          <div style="background: var(--bg-subtle); padding: 10px 12px; border-radius: 8px; border: 1px solid var(--border-color);">
            <div style="display: flex; justify-content: space-between; font-size: 11px; font-weight: 700; color: var(--primary); margin-bottom: 4px;">
              <span>${c.authorEmail || 'team@company.com'}</span>
              <span style="color: var(--text-muted); font-weight: 400;">${new Date(c.createdAt || Date.now()).toLocaleTimeString()}</span>
            </div>
            <div style="font-size: 13px; color: var(--text-primary); line-height: 1.4;">${c.body}</div>
          </div>
        `).join('');
      }
    }
  } catch (err) { console.error('Error loading comments:', err); }
}

async function submitComment(e) {
  e.preventDefault();
  if (!currentDetailIssueId) return;

  const input = document.getElementById('newCommentBody');
  const bodyText = input.value.trim();
  if (!bodyText) return;

  const payload = {
    authorEmail: currentProfileEmail || 'admin.lead@taskmanagement.com',
    body: bodyText
  };

  const res = await fetch(`${API_BASE}/issues/${currentDetailIssueId}/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    input.value = '';
    showToast('Comment posted!');
    await loadTaskComments(currentDetailIssueId);
  }
}

async function deleteCurrentTask() {
  if (!currentDetailIssueId) return;
  if (!confirm('Are you sure you want to delete this task?')) return;

  const res = await fetch(`${API_BASE}/issues/${currentDetailIssueId}`, { method: 'DELETE' });
  if (res.ok) {
    closeModal('taskDetailModal');
    showToast('Task deleted successfully');
    const activeTab = document.querySelector('.tab-pane.active')?.id?.replace('tab-', '');
    if (activeTab === 'kanban') loadKanban();
    else if (activeTab === 'backlog') loadBacklog();
    else loadDashboard();
  }
}

// 14. REAL-TIME SEARCH FILTER
document.addEventListener('input', (e) => {
  if (e.target && e.target.id === 'globalSearch') {
    const query = e.target.value.toLowerCase().trim();
    filterCurrentView(query);
  }
});

function filterCurrentView(query) {
  // Filter Kanban Cards
  document.querySelectorAll('.kanban-card').forEach(card => {
    const text = card.textContent.toLowerCase();
    card.style.display = text.includes(query) ? 'flex' : 'none';
  });

  // Filter Data Tables
  document.querySelectorAll('.data-table tbody tr').forEach(row => {
    const text = row.textContent.toLowerCase();
    row.style.display = text.includes(query) ? '' : 'none';
  });
}

// 13. USER PROFILE SETTINGS
let currentProfileEmail = 'admin.lead@taskmanagement.com';

async function loadProfile() {
  try {
    const res = await fetch(`${API_BASE}/user_profile_update/${encodeURIComponent(currentProfileEmail)}`);
    if (res.ok) {
      const profile = await res.json();
      currentProfileEmail = profile.userEmail || currentProfileEmail;
      document.getElementById('profEmail').value = profile.userEmail || '';
      document.getElementById('profName').value = profile.userName || '';
      document.getElementById('profDept').value = profile.department || '';
      document.getElementById('profTitle').value = profile.designation || '';
      document.getElementById('profOrg').value = profile.organizationName || '';

      document.getElementById('headerName').textContent = profile.userName || 'Alex Mercer';
      const initials = (profile.userName || 'AM').split(' ').map(n => n[0]).join('');
      document.getElementById('headerAvatar').textContent = initials;
    }
  } catch (err) { console.error('Error loading profile:', err); }
}

async function saveProfile(e) {
  e.preventDefault();
  const newEmail = document.getElementById('profEmail').value;
  const payload = {
    userEmail: newEmail,
    userName: document.getElementById('profName').value,
    department: document.getElementById('profDept').value,
    designation: document.getElementById('profTitle').value,
    organizationName: document.getElementById('profOrg').value,
    active: true
  };

  const res = await fetch(`${API_BASE}/user_profile_update/user_profile/update?oldEmail=${encodeURIComponent(currentProfileEmail)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    currentProfileEmail = newEmail;
    showToast('User Profile updated successfully!');
    await loadProfile();
  } else {
    showToast('Failed to update user profile or email already taken', 'error');
  }
}
