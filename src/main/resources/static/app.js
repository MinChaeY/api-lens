// State Management
const state = {
    token: localStorage.getItem('api_lens_token') || null,
    user: null,
    projects: [],
    selectedProject: null,
    endpoints: [],
    selectedEndpoint: null,
    testCases: [],
    selectedTestCase: null
};

// API Base URL (Relative to host since frontend is served by Spring Boot)
const API_BASE = '';

// 1. Core API Fetch Helper
async function apiCall(path, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };

    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    const config = {
        method,
        headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE}${path}`, config);
        
        if (response.status === 401) {
            handleLogout();
            throw new Error('인증이 만료되었습니다. 다시 로그인해 주세요.');
        }

        if (response.status === 204) {
            return null; // No Content
        }

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || '요청 처리 중 오류가 발생했습니다.');
        }

        return data;
    } catch (error) {
        console.error(`API Call failed (${method} ${path}):`, error);
        throw error;
    }
}

// 2. DOM Elements
const elements = {
    authContainer: document.getElementById('auth-container'),
    appContainer: document.getElementById('app-container'),
    loginForm: document.getElementById('login-form'),
    signupForm: document.getElementById('signup-form'),
    goToSignup: document.getElementById('go-to-signup'),
    goToLogin: document.getElementById('go-to-login'),
    authSubtitle: document.getElementById('auth-subtitle'),
    
    currentUserName: document.getElementById('current-user-name'),
    currentUserEmail: document.getElementById('current-user-email'),
    btnLogout: document.getElementById('btn-logout'),
    
    projectListUl: document.getElementById('project-list-ul'),
    btnAddProject: document.getElementById('btn-add-project'),
    welcomeView: document.getElementById('welcome-view'),
    btnWelcomeCreate: document.getElementById('btn-welcome-create'),
    projectView: document.getElementById('project-view'),
    
    selectedProjectName: document.getElementById('selected-project-name'),
    selectedProjectBaseUrl: document.getElementById('selected-project-base-url'),
    selectedProjectDescription: document.getElementById('selected-project-description'),
    btnEditProject: document.getElementById('btn-edit-project'),
    btnDeleteProject: document.getElementById('btn-delete-project'),
    
    openapiUrlInput: document.getElementById('openapi-url-input'),
    btnSaveOpenapi: document.getElementById('btn-save-openapi'),
    btnImportEndpoints: document.getElementById('btn-import-endpoints'),
    
    endpointListUl: document.getElementById('endpoint-list-ul'),
    endpointEmptyView: document.getElementById('endpoint-empty-view'),
    endpointDetailView: document.getElementById('endpoint-detail-view'),
    
    endpointMethodBadge: document.getElementById('endpoint-method-badge'),
    endpointPathText: document.getElementById('endpoint-path-text'),
    endpointSummary: document.getElementById('endpoint-summary'),
    endpointOperationId: document.getElementById('endpoint-operation-id'),
    
    btnAddTestcase: document.getElementById('btn-add-testcase'),
    testcaseTabUl: document.getElementById('testcase-tab-ul'),
    testcaseEmptyDetail: document.getElementById('testcase-empty-detail'),
    testcaseContentForm: document.getElementById('testcase-content-form'),
    testcaseDetailTitle: document.getElementById('testcase-detail-title'),
    
    testcaseDetailForm: document.getElementById('testcase-detail-form'),
    testcaseIdInput: document.getElementById('testcase-id-input'),
    testcaseNameInput: document.getElementById('testcase-name-input'),
    testcaseExpectedStatus: document.getElementById('testcase-expected-status'),
    testcaseHeadersInput: document.getElementById('testcase-headers-input'),
    testcaseBodyInput: document.getElementById('testcase-body-input'),
    testcaseExpectedBody: document.getElementById('testcase-expected-body'),
    btnDeleteTestcase: document.getElementById('btn-delete-testcase'),
    btnRunTestcase: document.getElementById('btn-run-testcase'),
    
    testResultBox: document.getElementById('test-result-box'),
    resultStatusBadge: document.getElementById('result-status-badge'),
    resultActualStatus: document.getElementById('result-actual-status'),
    resultDuration: document.getElementById('result-duration'),
    resultErrorContainer: document.getElementById('result-error-container'),
    resultErrorText: document.getElementById('result-error-text'),
    resultHeadersPre: document.getElementById('result-headers-pre'),
    resultBodyPre: document.getElementById('result-body-pre'),
    
    projectModal: document.getElementById('project-modal'),
    projectModalTitle: document.getElementById('project-modal-title'),
    projectForm: document.getElementById('project-form'),
    projectIdInput: document.getElementById('project-id-input'),
    projectNameInput: document.getElementById('project-name-input'),
    projectBaseUrlInput: document.getElementById('project-base-url-input'),
    projectDescInput: document.getElementById('project-desc-input'),
    btnCloseProjectModal: document.getElementById('btn-close-project-modal'),
    btnCancelProjectModal: document.getElementById('btn-cancel-project-modal'),
    btnSubmitProjectModal: document.getElementById('btn-submit-project-modal')
};

// 3. Initialization
window.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    checkAuth();
});

// Check JWT on startup
async function checkAuth() {
    if (state.token) {
        try {
            const userData = await apiCall('/api/users/me');
            state.user = userData;
            elements.currentUserName.textContent = state.user.name || 'User';
            elements.currentUserEmail.textContent = state.user.email;
            
            elements.authContainer.classList.add('hidden');
            elements.appContainer.classList.remove('hidden');
            
            loadProjects();
        } catch (error) {
            handleLogout();
        }
    } else {
        showAuthForm(true); // Show Login
    }
}

// Log in view control
function showAuthForm(showLogin) {
    elements.authContainer.classList.remove('hidden');
    elements.appContainer.classList.add('hidden');
    
    if (showLogin) {
        elements.loginForm.classList.remove('hidden');
        elements.signupForm.classList.add('hidden');
        elements.authSubtitle.textContent = '프로젝트 관리를 시작하려면 로그인하세요.';
    } else {
        elements.loginForm.classList.add('hidden');
        elements.signupForm.classList.remove('hidden');
        elements.authSubtitle.textContent = 'API Lens와 함께 API 사양을 자동 검증하세요.';
    }
}

// Logout handler
function handleLogout() {
    localStorage.removeItem('api_lens_token');
    state.token = null;
    state.user = null;
    state.projects = [];
    state.selectedProject = null;
    state.endpoints = [];
    state.selectedEndpoint = null;
    state.testCases = [];
    state.selectedTestCase = null;
    
    showAuthForm(true);
}

// Event Listeners Setup
function setupEventListeners() {
    // Auth Toggle Link
    elements.goToSignup.addEventListener('click', (e) => { e.preventDefault(); showAuthForm(false); });
    elements.goToLogin.addEventListener('click', (e) => { e.preventDefault(); showAuthForm(true); });
    
    // Auth Actions
    elements.loginForm.addEventListener('submit', handleLoginSubmit);
    elements.signupForm.addEventListener('submit', handleSignupSubmit);
    elements.btnLogout.addEventListener('click', handleLogout);
    
    // Project Actions
    elements.btnAddProject.addEventListener('click', () => showProjectModal());
    elements.btnWelcomeCreate.addEventListener('click', () => showProjectModal());
    elements.btnCloseProjectModal.addEventListener('click', hideProjectModal);
    elements.btnCancelProjectModal.addEventListener('click', hideProjectModal);
    elements.projectForm.addEventListener('submit', handleProjectSubmit);
    elements.btnEditProject.addEventListener('click', () => showProjectModal(state.selectedProject));
    elements.btnDeleteProject.addEventListener('click', handleDeleteProject);
    
    // OpenAPI Actions
    elements.btnSaveOpenapi.addEventListener('click', handleSaveOpenapi);
    elements.btnImportEndpoints.addEventListener('click', handleImportEndpoints);
    
    // Test Case Actions
    elements.btnAddTestcase.addEventListener('click', showNewTestCaseForm);
    elements.testcaseDetailForm.addEventListener('submit', handleSaveTestCase);
    elements.btnDeleteTestcase.addEventListener('click', handleDeleteTestCase);
    elements.btnRunTestcase.addEventListener('click', handleRunTestCase);
}

// 4. API Event Handlers

// SignUp Submit
async function handleSignupSubmit(e) {
    e.preventDefault();
    const name = document.getElementById('signup-name').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;
    
    try {
        await apiCall('/api/auth/signup', 'POST', { name, email, password });
        alert('회원가입에 성공했습니다! 이제 로그인해 주세요.');
        elements.signupForm.reset();
        showAuthForm(true);
    } catch (error) {
        alert(error.message);
    }
}

// Login Submit
async function handleLoginSubmit(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const data = await apiCall('/api/auth/login', 'POST', { email, password });
        state.token = data.accessToken;
        localStorage.setItem('api_lens_token', state.token);
        
        elements.loginForm.reset();
        checkAuth();
    } catch (error) {
        alert(error.message);
    }
}

// 5. Projects Logic
async function loadProjects() {
    try {
        state.projects = await apiCall('/api/projects');
        renderProjectList();
    } catch (error) {
        alert('프로젝트 목록을 불러오지 못했습니다: ' + error.message);
    }
}

function renderProjectList() {
    elements.projectListUl.innerHTML = '';
    
    if (state.projects.length === 0) {
        const li = document.createElement('li');
        li.className = 'project-item';
        li.style.color = 'var(--text-muted)';
        li.style.fontSize = '0.75rem';
        li.textContent = '프로젝트가 없습니다.';
        elements.projectListUl.appendChild(li);
        return;
    }
    
    state.projects.forEach(p => {
        const li = document.createElement('li');
        li.className = 'project-item';
        if (state.selectedProject && state.selectedProject.id === p.id) {
            li.classList.add('active');
        }
        li.innerHTML = `
            <span><i class="fa-solid fa-folder-open" style="margin-right: 8px; color: var(--color-primary);"></i> ${escapeHtml(p.name)}</span>
            <i class="fa-solid fa-chevron-right" style="font-size: 0.75rem; opacity: 0.5;"></i>
        `;
        li.addEventListener('click', () => selectProject(p.id));
        elements.projectListUl.appendChild(li);
    });
}

function showProjectModal(project = null) {
    elements.projectModal.classList.remove('hidden');
    elements.projectForm.reset();
    
    if (project) {
        elements.projectModalTitle.textContent = '프로젝트 정보 수정';
        elements.projectIdInput.value = project.id;
        elements.projectNameInput.value = project.name;
        elements.projectBaseUrlInput.value = project.baseUrl;
        elements.projectDescInput.value = project.description || '';
        elements.btnSubmitProjectModal.textContent = '수정';
    } else {
        elements.projectModalTitle.textContent = '새 프로젝트 생성';
        elements.projectIdInput.value = '';
        elements.btnSubmitProjectModal.textContent = '생성';
    }
}

function hideProjectModal() {
    elements.projectModal.classList.add('hidden');
}

async function handleProjectSubmit(e) {
    e.preventDefault();
    const id = elements.projectIdInput.value;
    const name = elements.projectNameInput.value;
    const baseUrl = elements.projectBaseUrlInput.value;
    const description = elements.projectDescInput.value;
    
    const body = { name, baseUrl, description };
    
    try {
        let result;
        if (id) {
            result = await apiCall(`/api/projects/${id}`, 'PUT', body);
            alert('프로젝트가 수정되었습니다.');
        } else {
            result = await apiCall('/api/projects', 'POST', body);
            alert('프로젝트가 생성되었습니다.');
        }
        
        hideProjectModal();
        await loadProjects();
        selectProject(result.id);
    } catch (error) {
        alert(error.message);
    }
}

async function selectProject(projectId) {
    const project = state.projects.find(p => p.id === projectId);
    if (!project) return;
    
    state.selectedProject = project;
    state.selectedEndpoint = null;
    state.endpoints = [];
    state.testCases = [];
    state.selectedTestCase = null;
    
    // UI Update
    renderProjectList();
    elements.welcomeView.classList.add('hidden');
    elements.projectView.classList.remove('hidden');
    
    elements.selectedProjectName.textContent = project.name;
    elements.selectedProjectBaseUrl.textContent = project.baseUrl;
    elements.selectedProjectDescription.textContent = project.description || '프로젝트 설명이 없습니다.';
    
    elements.openapiUrlInput.value = project.openApiUrl || '';
    toggleImportButton(!!project.openApiUrl);
    
    elements.endpointEmptyView.classList.remove('hidden');
    elements.endpointDetailView.classList.add('hidden');
    
    // Load Endpoints
    loadEndpoints();
}

function toggleImportButton(hasUrl) {
    if (hasUrl) {
        elements.btnImportEndpoints.classList.remove('hidden');
    } else {
        elements.btnImportEndpoints.classList.add('hidden');
    }
}

async function handleDeleteProject() {
    if (!state.selectedProject) return;
    if (!confirm(`정말로 프로젝트 "${state.selectedProject.name}"을(를) 삭제하시겠습니까?\n프로젝트 삭제 시 등록된 모든 엔드포인트 및 테스트 실행 내역이 함께 지워집니다.`)) {
        return;
    }
    
    try {
        await apiCall(`/api/projects/${state.selectedProject.id}`, 'DELETE');
        alert('프로젝트가 성공적으로 삭제되었습니다.');
        state.selectedProject = null;
        elements.projectView.classList.add('hidden');
        elements.welcomeView.classList.remove('hidden');
        loadProjects();
    } catch (error) {
        alert(error.message);
    }
}

// 6. OpenAPI Actions
async function handleSaveOpenapi() {
    if (!state.selectedProject) return;
    const openApiUrl = elements.openapiUrlInput.value.trim();
    
    try {
        const result = await apiCall(`/api/projects/${state.selectedProject.id}/register-openapi`, 'POST', { openApiUrl });
        alert('OpenAPI Specification URL이 저장되었습니다.');
        state.selectedProject.openApiUrl = result.openApiUrl;
        toggleImportButton(!!result.openApiUrl);
        loadProjects();
    } catch (error) {
        alert(error.message);
    }
}

async function handleImportEndpoints() {
    if (!state.selectedProject) return;
    elements.btnImportEndpoints.disabled = true;
    elements.btnImportEndpoints.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> 분석 및 가져오는 중...';
    
    try {
        const result = await apiCall(`/api/projects/${state.selectedProject.id}/endpoints/import`, 'POST');
        alert(`API 분석 완료!\n가져온 엔드포인트 수: ${result.importedCount}개`);
        await loadEndpoints();
    } catch (error) {
        alert('오픈 API 명세를 가져오지 못했습니다: ' + error.message);
    } finally {
        elements.btnImportEndpoints.disabled = false;
        elements.btnImportEndpoints.innerHTML = '<i class="fa-solid fa-file-import"></i> 명세서로부터 엔드포인트 불러오기 (Import)';
    }
}

// 7. Endpoints Logic
async function loadEndpoints() {
    if (!state.selectedProject) return;
    try {
        state.endpoints = await apiCall(`/api/projects/${state.selectedProject.id}/endpoints`);
        renderEndpointList();
    } catch (error) {
        alert('엔드포인트 리스트를 불러오지 못했습니다: ' + error.message);
    }
}

function renderEndpointList() {
    elements.endpointListUl.innerHTML = '';
    
    if (state.endpoints.length === 0) {
        const li = document.createElement('li');
        li.style.padding = '20px';
        li.style.textAlign = 'center';
        li.style.color = 'var(--text-muted)';
        li.style.fontSize = '0.813rem';
        li.textContent = '분석된 엔드포인트가 없습니다. 상단에서 OpenAPI URL을 등록하고 가져와 주세요.';
        elements.endpointListUl.appendChild(li);
        return;
    }
    
    state.endpoints.forEach(e => {
        const li = document.createElement('li');
        li.className = 'endpoint-item';
        if (state.selectedEndpoint && state.selectedEndpoint.id === e.id) {
            li.classList.add('active');
        }
        
        li.innerHTML = `
            <span class="endpoint-method ${e.httpMethod.toLowerCase()}">${escapeHtml(e.httpMethod)}</span>
            <span class="endpoint-path" title="${escapeHtml(e.path)}">${escapeHtml(e.path)}</span>
        `;
        li.addEventListener('click', () => selectEndpoint(e));
        elements.endpointListUl.appendChild(li);
    });
}

async function selectEndpoint(endpoint) {
    state.selectedEndpoint = endpoint;
    state.selectedTestCase = null;
    state.testCases = [];
    
    renderEndpointList();
    elements.endpointEmptyView.classList.add('hidden');
    elements.endpointDetailView.classList.remove('hidden');
    
    // Meta Card Mapping
    elements.endpointMethodBadge.textContent = endpoint.httpMethod;
    elements.endpointMethodBadge.className = `badge ${endpoint.httpMethod.toLowerCase()}`;
    elements.endpointPathText.textContent = endpoint.path;
    elements.endpointSummary.textContent = endpoint.summary || '설명이 작성되지 않았습니다.';
    elements.endpointOperationId.textContent = endpoint.operationId ? `Operation ID: ${endpoint.operationId}` : 'No Operation ID';
    
    // Form views reset
    elements.testcaseEmptyDetail.classList.remove('hidden');
    elements.testcaseContentForm.classList.add('hidden');
    elements.testResultBox.classList.add('hidden');
    
    // Load Test Cases
    loadTestCases();
}

// 8. Test Cases Logic
async function loadTestCases() {
    if (!state.selectedProject || !state.selectedEndpoint) return;
    try {
        state.testCases = await apiCall(`/api/projects/${state.selectedProject.id}/endpoints/${state.selectedEndpoint.id}/test-cases`);
        renderTestCaseList();
    } catch (error) {
        alert('테스트 케이스를 불러오지 못했습니다: ' + error.message);
    }
}

function renderTestCaseList() {
    elements.testcaseTabUl.innerHTML = '';
    
    if (state.testCases.length === 0) {
        const li = document.createElement('li');
        li.style.padding = '12px';
        li.style.fontSize = '0.75rem';
        li.style.color = 'var(--text-muted)';
        li.style.textAlign = 'center';
        li.textContent = '테스트 케이스가 없습니다.';
        elements.testcaseTabUl.appendChild(li);
        return;
    }
    
    state.testCases.forEach(tc => {
        const li = document.createElement('li');
        li.className = 'testcase-tab';
        if (state.selectedTestCase && state.selectedTestCase.id === tc.id) {
            li.classList.add('active');
        }
        li.textContent = tc.name;
        li.title = tc.name;
        li.addEventListener('click', () => selectTestCase(tc));
        elements.testcaseTabUl.appendChild(li);
    });
}

function selectTestCase(testCase) {
    state.selectedTestCase = testCase;
    renderTestCaseList();
    
    elements.testcaseEmptyDetail.classList.add('hidden');
    elements.testcaseContentForm.classList.remove('hidden');
    elements.testResultBox.classList.add('hidden');
    
    // Mapping form field
    elements.testcaseDetailTitle.textContent = `테스트 케이스 설정 - ${testCase.name}`;
    elements.testcaseIdInput.value = testCase.id;
    elements.testcaseNameInput.value = testCase.name;
    elements.testcaseExpectedStatus.value = testCase.expectedStatus;
    elements.testcaseHeadersInput.value = testCase.requestHeaders || '';
    elements.testcaseBodyInput.value = testCase.requestBody || '';
    elements.testcaseExpectedBody.value = testCase.expectedBody || '';
    
    elements.btnDeleteTestcase.classList.remove('hidden');
    elements.btnRunTestcase.classList.remove('hidden');
}

function showNewTestCaseForm() {
    if (!state.selectedEndpoint) return;
    
    state.selectedTestCase = null;
    renderTestCaseList();
    
    elements.testcaseEmptyDetail.classList.add('hidden');
    elements.testcaseContentForm.classList.remove('hidden');
    elements.testResultBox.classList.add('hidden');
    
    elements.testcaseDetailTitle.textContent = '새 테스트 케이스 추가';
    elements.testcaseIdInput.value = '';
    elements.testcaseNameInput.value = '새 테스트 시나리오';
    elements.testcaseExpectedStatus.value = 200;
    elements.testcaseHeadersInput.value = '';
    elements.testcaseBodyInput.value = '';
    elements.testcaseExpectedBody.value = '';
    
    elements.btnDeleteTestcase.classList.add('hidden');
    elements.btnRunTestcase.classList.add('hidden');
}

async function handleSaveTestCase(e) {
    e.preventDefault();
    if (!state.selectedProject || !state.selectedEndpoint) return;
    
    const id = elements.testcaseIdInput.value;
    const name = elements.testcaseNameInput.value;
    const expectedStatus = parseInt(elements.testcaseExpectedStatus.value, 10);
    const requestHeaders = elements.testcaseHeadersInput.value;
    const requestBody = elements.testcaseBodyInput.value;
    const expectedBody = elements.testcaseExpectedBody.value;
    
    const body = {
        name,
        expectedStatus,
        requestHeaders,
        requestBody,
        expectedBody
    };
    
    try {
        let result;
        if (id) {
            result = await apiCall(`/api/projects/${state.selectedProject.id}/endpoints/${state.selectedEndpoint.id}/test-cases/${id}`, 'PUT', body);
            alert('테스트 케이스가 저장되었습니다.');
        } else {
            result = await apiCall(`/api/projects/${state.selectedProject.id}/endpoints/${state.selectedEndpoint.id}/test-cases`, 'POST', body);
            alert('테스트 케이스가 생성되었습니다.');
        }
        
        await loadTestCases();
        selectTestCase(result);
    } catch (error) {
        alert(error.message);
    }
}

async function handleDeleteTestCase() {
    if (!state.selectedProject || !state.selectedEndpoint || !state.selectedTestCase) return;
    
    if (!confirm('이 테스트 케이스를 정말로 삭제하시겠습니까?\n과거 실행 이력도 함께 지워집니다.')) {
        return;
    }
    
    try {
        await apiCall(`/api/projects/${state.selectedProject.id}/endpoints/${state.selectedEndpoint.id}/test-cases/${state.selectedTestCase.id}`, 'DELETE');
        alert('테스트 케이스가 삭제되었습니다.');
        state.selectedTestCase = null;
        elements.testcaseEmptyDetail.classList.remove('hidden');
        elements.testcaseContentForm.classList.add('hidden');
        loadTestCases();
    } catch (error) {
        alert(error.message);
    }
}

// 9. Run Test Case Engine
async function handleRunTestCase() {
    if (!state.selectedProject || !state.selectedEndpoint || !state.selectedTestCase) return;
    
    elements.btnRunTestcase.disabled = true;
    elements.btnRunTestcase.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> API 전송 중...';
    
    elements.testResultBox.classList.add('hidden');
    
    try {
        const result = await apiCall(
            `/api/projects/${state.selectedProject.id}/endpoints/${state.selectedEndpoint.id}/test-cases/${state.selectedTestCase.id}/run`,
            'POST'
        );
        
        // Render Result Box
        elements.testResultBox.classList.remove('hidden');
        
        if (result.success) {
            elements.resultStatusBadge.textContent = 'PASSED';
            elements.resultStatusBadge.className = 'result-badge-passed';
        } else {
            elements.resultStatusBadge.textContent = 'FAILED';
            elements.resultStatusBadge.className = 'result-badge-failed';
        }
        
        // Status code text mapping
        if (result.actualStatus) {
            elements.resultActualStatus.textContent = `${result.actualStatus}`;
        } else {
            elements.resultActualStatus.textContent = 'N/A';
        }
        
        elements.resultDuration.textContent = `${result.responseTimeMs} ms`;
        
        // Error Message Mapping
        if (result.errorMessage) {
            elements.resultErrorContainer.classList.remove('hidden');
            elements.resultErrorText.textContent = result.errorMessage;
        } else {
            elements.resultErrorContainer.classList.add('hidden');
        }
        
        // Format & Mapping Headers
        elements.resultHeadersPre.textContent = formatHeaders(result.actualHeaders);
        
        // Format & Mapping Body
        elements.resultBodyPre.textContent = formatBody(result.actualBody);
        
    } catch (error) {
        alert('테스트 케이스를 구동하는 중 치명적인 서버 예외가 발생했습니다: ' + error.message);
    } finally {
        elements.btnRunTestcase.disabled = false;
        elements.btnRunTestcase.innerHTML = '<i class="fa-solid fa-play"></i> 테스트 실행 (Run)';
    }
}

// Format multi-value headers to simple text output
function formatHeaders(headers) {
    if (!headers || Object.keys(headers).length === 0) {
        return 'No Headers';
    }
    
    let text = '';
    Object.keys(headers).forEach(key => {
        const val = headers[key];
        const valStr = Array.isArray(val) ? val.join(', ') : val;
        text += `${key}: ${valStr}\n`;
    });
    return text.trim();
}

// Pretty-print response body if it is JSON
function formatBody(body) {
    if (!body) {
        return 'No Content';
    }
    
    try {
        const parsed = JSON.parse(body);
        return JSON.stringify(parsed, null, 2);
    } catch (e) {
        return body; // Fallback to raw string
    }
}

// Utility: HTML escape
function escapeHtml(string) {
    if (!string) return '';
    return string
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
