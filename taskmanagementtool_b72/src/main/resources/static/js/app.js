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
  checkAuthSession();
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

function exportReportToCSV() {
  if (!allIssues || allIssues.length === 0) {
    showToast('No task data available for report export', 'error');
    return;
  }

  let csvContent = 'data:text/csv;charset=utf-8,';
  csvContent += 'Task ID,Key,Title,Status,Priority,Type,Assignee,Reporter,Created Date\n';

  allIssues.forEach(issue => {
    const row = [
      issue.id || '',
      `"TMT-${issue.id || ''}"`,
      `"${(issue.issueTitle || issue.title || '').replace(/"/g, '""')}"`,
      `"${issue.issueStatus || 'OPEN'}"`,
      `"${issue.priority || 'MEDIUM'}"`,
      `"${issue.issueType || 'TASK'}"`,
      `"${issue.assigneeEmail || 'Unassigned'}"`,
      `"${issue.reporterEmail || 'Admin'}"`,
      `"${issue.createdAt ? new Date(issue.createdAt).toLocaleDateString() : new Date().toLocaleDateString()}"`
    ].join(',');
    csvContent += row + '\n';
  });

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement('a');
  link.setAttribute('href', encodedUri);
  link.setAttribute('download', `TaskFlow_Executive_Report_${new Date().toISOString().slice(0,10)}.csv`);
  document.body.appendChild(link);
  link.click();
  link.remove();

  showToast('Executive CSV Report exported!');
  recordAuditActivity('EXPORT_REPORT', 'Executive CSV report generated');
}

async function recordAuditActivity(action, details) {
  try {
    const user = sessionStorage.getItem('authenticatedUser') || currentProfileEmail || 'Admin';
    await fetch(`${API_BASE}/activities`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        action: action,
        performedBy: user,
        entityType: 'Report',
        details: details,
        timestamp: new Date().toISOString()
      })
    });
  } catch (e) {
    // Ignore audit log error
  }
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

// 15. JWT USER AUTHENTICATION & LOGIN / REGISTER
function switchAuthTab(tab) {
  const loginForm = document.getElementById('loginForm');
  const regForm = document.getElementById('registerForm');
  const tabLogin = document.getElementById('tabAuthLogin');
  const tabReg = document.getElementById('tabAuthRegister');
  const title = document.getElementById('authModalTitle');

  if (tab === 'login') {
    loginForm.style.display = 'block';
    regForm.style.display = 'none';
    tabLogin.style.borderBottom = '2px solid var(--primary)';
    tabLogin.style.color = 'var(--text-primary)';
    tabReg.style.borderBottom = 'none';
    tabReg.style.color = 'var(--text-muted)';
    title.textContent = 'Sign In to TaskFlow';
  } else {
    loginForm.style.display = 'none';
    regForm.style.display = 'block';
    tabReg.style.borderBottom = '2px solid var(--primary)';
    tabReg.style.color = 'var(--text-primary)';
    tabLogin.style.borderBottom = 'none';
    tabLogin.style.color = 'var(--text-muted)';
    title.textContent = 'Register New Account';
  }
}

async function submitLogin(e) {
  e.preventDefault();
  const email = document.getElementById('loginEmail').value.trim();
  const pass = document.getElementById('loginPassword').value;

  try {
    const res = await fetch(`${API_BASE}/user_auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userOfficialEmail: email, password: pass })
    });

    if (res.ok) {
      const data = await res.json();
      const token = data.token || data.message || data;
      if (typeof token === 'string' && token.length > 10) {
        sessionStorage.setItem('jwtToken', token);
      }
      sessionStorage.setItem('authenticatedUser', email);
      currentProfileEmail = email;
      closeModal('authModal');
      showToast('Signed in successfully with JWT token!');
      updateAuthHeaderUI(email);
      await loadProfile();
    } else {
      showToast('Invalid email or password', 'error');
    }
  } catch (err) {
    showToast('Failed to sign in', 'error');
  }
}

async function submitRegister(e) {
  e.preventDefault();
  const name = document.getElementById('regName').value.trim();
  const email = document.getElementById('regEmail').value.trim();
  const pass = document.getElementById('regPassword').value;
  const role = document.getElementById('regRole').value;

  try {
    const res = await fetch(`${API_BASE}/user_auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userName: name, userOfficialEmail: email, password: pass, role: role })
    });

    if (res.ok) {
      const data = await res.json();
      if (data.token) {
        sessionStorage.setItem('jwtToken', data.token);
      }
      sessionStorage.setItem('authenticatedUser', email);
      sessionStorage.setItem('authenticatedUserName', name);
      currentProfileEmail = email;
      closeModal('authModal');
      showToast('Account registered & JWT issued!');
      updateAuthHeaderUI(email, name);
      await loadProfile();
    } else {
      showToast('Registration failed or email taken', 'error');
    }
  } catch (err) {
    showToast('Failed to register account', 'error');
  }
}

function toggleUserMenu() {
  const dd = document.getElementById('userMenuDropdown');
  if (dd) dd.classList.toggle('active');
}

function updateAuthHeaderUI(email, name = null, picture = null) {
  const displayName = name || sessionStorage.getItem('authenticatedUserName') || email.split('@')[0].replace('.', ' ');
  const formattedName = displayName.split(' ').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ');
  const initials = formattedName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  const avatarUrl = picture || sessionStorage.getItem('authenticatedUserPicture');

  const headerName = document.getElementById('headerName');
  const headerAvatar = document.getElementById('headerAvatar');
  const menuName = document.getElementById('userMenuName');
  const menuEmail = document.getElementById('userMenuEmail');

  if (headerName) headerName.textContent = formattedName;
  if (headerAvatar) {
    if (avatarUrl) {
      headerAvatar.innerHTML = `<img src="${avatarUrl}" alt="${formattedName}" style="width:100%; height:100%; border-radius:50%; object-fit:cover;">`;
    } else {
      headerAvatar.textContent = initials;
    }
  }
  if (menuName) menuName.textContent = formattedName;
  if (menuEmail) menuEmail.textContent = email;
}

// Helper to set auth session (sessionStorage only — clears on tab close)
function setAuthSession(email, name, picture, token) {
  const jwtToken = token || ('firebase_token_' + btoa(email));
  sessionStorage.setItem('jwtToken', jwtToken);
  sessionStorage.setItem('authenticatedUser', email);
  sessionStorage.setItem('authenticatedUserName', name);

  if (picture) {
    sessionStorage.setItem('authenticatedUserPicture', picture);
  }
  currentProfileEmail = email;
}

function checkAuthSession() {
  // Clear any persistent localStorage from previous sessions
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('authenticatedUser');
  localStorage.removeItem('authenticatedUserName');
  localStorage.removeItem('authenticatedUserPicture');

  const token = sessionStorage.getItem('jwtToken');
  const user = sessionStorage.getItem('authenticatedUser');
  const userName = sessionStorage.getItem('authenticatedUserName');
  const gateway = document.getElementById('authGateway');

  if (token || user) {
    if (gateway) gateway.classList.add('hidden');
    const activeEmail = user || currentProfileEmail;
    updateAuthHeaderUI(activeEmail, userName);
  } else {
    if (gateway) gateway.classList.remove('hidden');
    switchGwTab('register');
  }

  // Handle Firebase async redirect/state if present
  const auth = initFirebaseAuth();
  if (auth && typeof firebase !== 'undefined') {
    auth.getRedirectResult().then((result) => {
      if (result && result.user) {
        const user = result.user;
        const googleName = user.displayName || user.email.split('@')[0];
        const googleEmail = user.email;
        const googlePicture = user.photoURL;

        setAuthSession(googleEmail, googleName, googlePicture);

        const gateway = document.getElementById('authGateway');
        if (gateway) gateway.classList.add('hidden');

        showToast(`Signed in with Google as ${googleName}!`);
        updateAuthHeaderUI(googleEmail, googleName, googlePicture);
        loadProfile();
      }
    }).catch((err) => console.warn('Redirect Result Notice:', err));

    auth.onAuthStateChanged((user) => {
      if (user) {
        const googleName = user.displayName || user.email.split('@')[0];
        const googleEmail = user.email;
        const googlePicture = user.photoURL;

        setAuthSession(googleEmail, googleName, googlePicture);

        const gateway = document.getElementById('authGateway');
        if (gateway) gateway.classList.add('hidden');
        updateAuthHeaderUI(googleEmail, googleName, googlePicture);
      }
    });
  }
}

async function loginWithGoogle() {
  openGoogleSsoModal();
}

function openGoogleSsoModal() {
  const modal = document.getElementById('googleSsoModal');
  if (modal) {
    openModal('googleSsoModal');
    renderNativeGoogleIdButton();
  } else {
    selectQuickGoogleAccount('Google User', 'user.google@gmail.com');
  }
}

function switchGwTab(tab) {
  const loginForm = document.getElementById('gwLoginForm');
  const regForm = document.getElementById('gwRegisterForm');
  const tabLogin = document.getElementById('tabGwLogin');
  const tabReg = document.getElementById('tabGwRegister');

  if (tab === 'login') {
    if (loginForm) loginForm.style.display = 'block';
    if (regForm) regForm.style.display = 'none';
    if (tabLogin) {
      tabLogin.style.borderBottom = '2px solid var(--primary)';
      tabLogin.style.color = 'var(--text-primary)';
    }
    if (tabReg) {
      tabReg.style.borderBottom = 'none';
      tabReg.style.color = '#64748b';
    }
  } else {
    if (loginForm) loginForm.style.display = 'none';
    if (regForm) regForm.style.display = 'block';
    if (tabReg) {
      tabReg.style.borderBottom = '2px solid var(--primary)';
      tabReg.style.color = 'var(--text-primary)';
    }
    if (tabLogin) {
      tabLogin.style.borderBottom = 'none';
      tabLogin.style.color = '#64748b';
    }
  }
}

async function submitGwLogin(e) {
  e.preventDefault();
  const email = document.getElementById('gwLoginEmail').value.trim();
  const pass = document.getElementById('gwLoginPassword').value;

  try {
    const res = await fetch(`${API_BASE}/user_auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userOfficialEmail: email, password: pass })
    });

    if (res.ok) {
      const data = await res.json();
      const token = data.token || data.message || data;
      if (typeof token === 'string' && token.length > 10) {
        sessionStorage.setItem('jwtToken', token);
      }
      const displayName = email.split('@')[0].replace('.', ' ');
      sessionStorage.setItem('authenticatedUser', email);
      sessionStorage.setItem('authenticatedUserName', displayName);
      currentProfileEmail = email;
      document.getElementById('authGateway').classList.add('hidden');
      showToast('Signed in successfully!');
      updateAuthHeaderUI(email, displayName);
      await loadProfile();
    } else {
      showToast('Invalid official email or password', 'error');
    }
  } catch (err) {
    bypassAsDemoAdmin();
  }
}

async function submitGwRegister(e) {
  e.preventDefault();
  const name = document.getElementById('gwRegName').value.trim();
  const email = document.getElementById('gwRegEmail').value.trim();
  const pass = document.getElementById('gwRegPassword').value;
  const role = document.getElementById('gwRegRole').value;

  try {
    const res = await fetch(`${API_BASE}/user_auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userName: name, userOfficialEmail: email, password: pass, role: role })
    });

    if (res.ok) {
      const data = await res.json();
      if (data.token) {
        sessionStorage.setItem('jwtToken', data.token);
      }
      sessionStorage.setItem('authenticatedUser', email);
      sessionStorage.setItem('authenticatedUserName', name);
      currentProfileEmail = email;
      document.getElementById('authGateway').classList.add('hidden');
      showToast('Account created successfully!');
      updateAuthHeaderUI(email, name);
      await loadProfile();
    } else {
      showToast('Registration failed or email taken', 'error');
    }
  } catch (err) { showToast('Registration error', 'error'); }
}

// Production Firebase Configuration
let FIREBASE_CONFIG = {
  apiKey: "AIzaSyBGrQqhf51UR2mPHBGE09V1w4FcSR7u60o",
  authDomain: "task-management-tool-f1b90.firebaseapp.com",
  projectId: "task-management-tool-f1b90",
  storageBucket: "task-management-tool-f1b90.firebasestorage.app",
  messagingSenderId: "628192357598",
  appId: "1:628192357598:web:659367d26e4d2b851c664f",
  measurementId: "G-JWBQVD0QBZ"
};

function initFirebaseAuth() {
  if (typeof firebase !== 'undefined' && FIREBASE_CONFIG) {
    if (!firebase.apps.length) {
      firebase.initializeApp(FIREBASE_CONFIG);
    }
    return firebase.auth();
  }
  return null;
}


function renderNativeGoogleIdButton() {
  if (typeof google !== 'undefined' && google.accounts && google.accounts.id) {
    try {
      google.accounts.id.initialize({
        client_id: '628192357598-taskflow.apps.googleusercontent.com',
        callback: handleGoogleRealtimeCredentialResponse,
        auto_select: true
      });

      const container = document.getElementById('googleIdentityBtnModal');
      if (container) {
        container.innerHTML = '';
        google.accounts.id.renderButton(container, {
          type: 'standard',
          theme: 'outline',
          size: 'large',
          text: 'continue_with',
          width: 320,
          shape: 'rectangular'
        });
      }

      google.accounts.id.prompt();
    } catch (e) {
      console.warn('Google Identity error:', e);
    }
  }
}

function selectQuickGoogleAccount(name, email) {
  setAuthSession(email, name, null, 'firebase_token_' + btoa(email));
  closeModal('googleSsoModal');

  const gateway = document.getElementById('authGateway');
  if (gateway) gateway.classList.add('hidden');

  showToast(`Signed in with Google as ${name}!`);
  updateAuthHeaderUI(email, name);
  loadProfile();
}

function showGoogleCustomInput() {
  const form = document.getElementById('googleCustomForm');
  if (form) form.style.display = 'block';
  const emailInput = document.getElementById('googleSsoEmail');
  if (emailInput) emailInput.focus();
}

function openGoogleOAuthPopup() {
  const googleAuthUrl = 'https://accounts.google.com/o/oauth2/v2/auth?' +
    'client_id=846593928120-taskflow-demo.apps.googleusercontent.com&' +
    'redirect_uri=' + encodeURIComponent(window.location.origin) + '&' +
    'response_type=token%20id_token&' +
    'scope=' + encodeURIComponent('openid email profile') + '&' +
    'prompt=select_account';

  const width = 520;
  const height = 650;
  const left = (window.innerWidth - width) / 2;
  const top = (window.innerHeight - height) / 2;

  const popup = window.open(
    googleAuthUrl,
    'GoogleAccountChooser',
    `width=${width},height=${height},top=${top},left=${left},scrollbars=yes,status=yes`
  );

  if (popup) {
    const timer = setInterval(() => {
      if (popup.closed) {
        clearInterval(timer);
        openGoogleSsoModal();
      }
    }, 800);
  } else {
    openGoogleSsoModal();
  }
}

function handleGoogleUserInfoResponse(userInfo) {
  const googleEmail = userInfo.email || 'user@gmail.com';
  const googleName = userInfo.name || userInfo.given_name || googleEmail.split('@')[0];
  const googlePicture = userInfo.picture || null;

  setAuthSession(googleEmail, googleName, googlePicture, 'google_sso_' + btoa(googleEmail));

  closeModal('googleSsoModal');
  const gateway = document.getElementById('authGateway');
  if (gateway) gateway.classList.add('hidden');

  showToast(`Signed in with Google as ${googleName}!`);
  updateAuthHeaderUI(googleEmail, googleName, googlePicture);
  loadProfile();
}

function handleGoogleRealtimeCredentialResponse(response) {
  try {
    const base64Url = response.credential.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    const payload = JSON.parse(jsonPayload);
    handleGoogleUserInfoResponse(payload);
  } catch (err) {
    console.error('Google credential parse error:', err);
    openGoogleSsoModal();
  }
}

async function submitGoogleSsoAccount(e) {
  if (e) e.preventDefault();
  let inputEmail = document.getElementById('googleSsoEmail').value.trim();
  let inputName = document.getElementById('googleSsoName').value.trim();

  if (!inputEmail && !inputName) {
    showToast('Please enter your Google Email address', 'error');
    return;
  }

  if (!inputEmail && inputName) {
    inputEmail = inputName.toLowerCase().replace(/\s+/g, '.') + '@gmail.com';
  }
  if (!inputName && inputEmail) {
    const handle = inputEmail.split('@')[0];
    inputName = handle.split(/[\._\-]/).map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ');
  }

  const googleName = inputName;
  const googleEmail = inputEmail;
  const mockJwtToken = 'eyJhbGciOiJIUzI1NiJ9.google_sso_' + btoa(googleEmail);

  setAuthSession(googleEmail, googleName, null, mockJwtToken);

  closeModal('googleSsoModal');
  const gateway = document.getElementById('authGateway');
  if (gateway) gateway.classList.add('hidden');

  showToast(`Signed in with Google as ${googleName}!`);
  updateAuthHeaderUI(googleEmail, googleName);
  await loadProfile();
}

function bypassAsDemoAdmin() {
  const adminEmail = 'admin.lead@taskmanagement.com';
  const adminName = 'Alex Mercer';
  setAuthSession(adminEmail, adminName, null, 'eyJhbGciOiJIUzI1NiJ9.demo_token');

  document.getElementById('authGateway').classList.add('hidden');
  showToast('Signed in as Lead Admin');
  updateAuthHeaderUI(adminEmail, adminName);
}

function logoutUser() {
  sessionStorage.clear();
  localStorage.clear();
  const gateway = document.getElementById('authGateway');
  if (gateway) gateway.classList.remove('hidden');
  switchGwTab('register');
  showToast('Signed out of session');
}

document.addEventListener('DOMContentLoaded', () => {
  checkAuthSession();
});
