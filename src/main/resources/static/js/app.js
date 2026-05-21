// Конфигурация API
const API_BASE_URL = 'http://localhost:8080/api';

// Текущая выбранная заявка
let currentRequestId = null;

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    setupTabs();
    setupForms();
    loadRequests();
    loadCompetences();
    loadEngineers();
});

// Настройка переключения вкладок
function setupTabs() {
    const tabs = document.querySelectorAll('#mainTabs .nav-link');
    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();

            // Убираем активный класс со всех вкладок
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            // Скрываем все представления
            document.querySelectorAll('.tab-content').forEach(view => {
                view.classList.add('d-none');
            });

            // Показываем нужное представление
            const tabName = this.dataset.tab;
            document.getElementById(tabName + '-view').classList.remove('d-none');

            if (tabName === 'requests') {
                loadRequests();
            } else if (tabName === 'create') {
                loadCompetences();
            } else if (tabName === 'engineers') {
                loadEngineers();
            }
        });
    });
}

// Настройка форм
function setupForms() {
    const form = document.getElementById('create-request-form');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        await createRequest();
    });

    // Кнопки модального окна
    document.getElementById('btn-delete').addEventListener('click', deleteCurrentRequest);
    document.getElementById('btn-escalate').addEventListener('click', escalateCurrentRequest);

    // Форма создания инженера
    const engineerForm = document.getElementById('create-engineer-form');
    engineerForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        await createEngineer();
    });
}

// Показать уведомление
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `alert alert-${type}`;
    notification.classList.remove('d-none');

    setTimeout(() => {
        notification.classList.add('d-none');
    }, 5000);
}

// Загрузка списка заявок
async function loadRequests() {
    try {
        const response = await fetch(`${API_BASE_URL}/requests`);

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const requests = await response.json();
        renderRequestsTable(requests);
    } catch (error) {
        console.error('Ошибка загрузки заявок:', error);
        showNotification('Ошибка загрузки заявок: ' + error.message, 'danger');
        document.getElementById('requests-table-body').innerHTML =
            '<tr><td colspan="6" class="text-center text-danger">Ошибка загрузки данных</td></tr>';
    }
}

// Отрисовка таблицы заявок
function renderRequestsTable(requests) {
    const tbody = document.getElementById('requests-table-body');

    if (requests.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Заявок нет</td></tr>';
        return;
    }

    tbody.innerHTML = requests.map(req => `
        <tr>
            <td>${req.idRequest}</td>
            <td>${escapeHtml(req.subject)}</td>
            <td><span class="badge badge-${req.priority}">${req.priority}</span></td>
            <td><span class="badge badge-${req.status}">${req.status}</span></td>
            <td>${formatDate(req.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-info" onclick="showRequestDetails(${req.idRequest})">Просмотр</button>
            </td>
        </tr>
    `).join('');
}

// Загрузка компетенций
async function loadCompetences() {
    try {
        const response = await fetch(`${API_BASE_URL}/sla`);

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const slaRecords = await response.json();

        // Извлекаем уникальные компетенции из SLA записей или используем дефолтные
        const select = document.getElementById('competence');

        // Для простоты создадим несколько компетенций по умолчанию
        // В реальном приложении нужен отдельный эндпоинт для компетенций
        const defaultCompetences = [
            { id: 1, name: '1 линия поддержки' },
            { id: 2, name: '2 линия поддержки' },
            { id: 3, name: 'Сетевые технологии' },
            { id: 4, name: 'Базы данных' },
            { id: 5, name: 'Веб-приложения' }
        ];

        select.innerHTML = '<option value="">Выберите компетенцию</option>' +
            defaultCompetences.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    } catch (error) {
        console.error('Ошибка загрузки компетенций:', error);
        // Используем дефолтные значения даже при ошибке
        const select = document.getElementById('competence');
        const defaultCompetences = [
            { id: 1, name: '1 линия поддержки' },
            { id: 2, name: '2 линия поддержка' },
            { id: 3, name: 'Сетевые технологии' }
        ];
        select.innerHTML = '<option value="">Выберите компетенцию</option>' +
            defaultCompetences.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    }
}

// Создание заявки
async function createRequest() {
    const subject = document.getElementById('subject').value.trim();
    const specification = document.getElementById('specification').value.trim();
    const contactInfo = document.getElementById('contactInfo').value.trim();
    const priority = document.getElementById('priority').value;
    const competenceId = document.getElementById('competence').value;

    if (!subject || !specification || !contactInfo || !priority || !competenceId) {
        showNotification('Заполните все обязательные поля', 'warning');
        return;
    }

    const dto = {
        subject: subject,
        specification: specification,
        contactInfo: contactInfo,
        priority: priority,
        idCompetence: parseInt(competenceId)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/requests`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dto)
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        showNotification('Заявка успешно создана!', 'success');

        // Очистить форму и переключиться на список
        document.getElementById('create-request-form').reset();
        document.querySelector('[data-tab="requests"]').click();
    } catch (error) {
        console.error('Ошибка создания заявки:', error);
        showNotification('Ошибка создания заявки: ' + error.message, 'danger');
    }
}

// Показать детали заявки
async function showRequestDetails(id) {
    currentRequestId = id;

    try {
        const response = await fetch(`${API_BASE_URL}/requests/${id}`);

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const req = await response.json();

        document.getElementById('modal-id').textContent = req.idRequest;
        document.getElementById('modal-subject').textContent = req.subject;
        document.getElementById('modal-specification').textContent = req.specification;
        document.getElementById('modal-contact').textContent = req.contactInfo;

        const priorityBadge = document.getElementById('modal-priority');
        priorityBadge.textContent = req.priority;
        priorityBadge.className = `badge badge-${req.priority}`;

        const statusBadge = document.getElementById('modal-status');
        statusBadge.textContent = req.status;
        statusBadge.className = `badge badge-${req.status}`;

        document.getElementById('modal-created').textContent = formatDate(req.createdAt);
        document.getElementById('modal-sla').textContent = formatDate(req.slaDeadline);
        document.getElementById('modal-actual-end').textContent = formatDate(req.actualEnd) || 'Не завершена';
        document.getElementById('modal-escalation').textContent = req.escalationReason || 'Нет';

        const modal = new bootstrap.Modal(document.getElementById('requestModal'));
        modal.show();
    } catch (error) {
        console.error('Ошибка загрузки деталей:', error);
        showNotification('Ошибка загрузки деталей заявки: ' + error.message, 'danger');
    }
}

// Удаление заявки
async function deleteCurrentRequest() {
    if (!currentRequestId) return;

    if (!confirm('Вы уверены, что хотите удалить эту заявку?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/requests/${currentRequestId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        showNotification('Заявка удалена', 'success');

        // Закрыть модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('requestModal'));
        modal.hide();

        // Обновить список
        loadRequests();
    } catch (error) {
        console.error('Ошибка удаления:', error);
        showNotification('Ошибка удаления заявки: ' + error.message, 'danger');
    }
}

// Эскалация заявки
async function escalateCurrentRequest() {
    if (!currentRequestId) return;

    const reason = prompt('Введите причину эскалации:');
    if (!reason) return;

    try {
        const response = await fetch(`${API_BASE_URL}/requests/${currentRequestId}/escalate`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reason)
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        showNotification('Заявка эскалирована', 'success');

        // Закрыть модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('requestModal'));
        modal.hide();

        // Обновить список
        loadRequests();
    } catch (error) {
        console.error('Ошибка эскалации:', error);
        showNotification('Ошибка эскалации заявки: ' + error.message, 'danger');
    }
}

// Запуск алгоритма планирования
async function runPlanning() {
    const resultDiv = document.getElementById('planning-result');
    resultDiv.innerHTML = '<div class="spinner-border text-primary" role="status"></div> Выполняется...';

    try {
        const response = await fetch(`${API_BASE_URL}/sla/planning/run`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const result = await response.json();

        resultDiv.innerHTML = `
            <div class="alert alert-success">
                <h5>Планирование завершено!</h5>
                <p>Назначено заявок: <strong>${result.assignedCount}</strong></p>
                <p>${result.message}</p>
            </div>
        `;

        showNotification('Алгоритм планирования выполнен успешно', 'success');
    } catch (error) {
        console.error('Ошибка планирования:', error);
        resultDiv.innerHTML = `
            <div class="alert alert-danger">
                Ошибка выполнения алгоритма: ${error.message}
            </div>
        `;
        showNotification('Ошибка планирования: ' + error.message, 'danger');
    }
}

// Загрузка списка инженеров
async function loadEngineers() {
    try {
        const response = await fetch(`${API_BASE_URL}/sla/engineers`);

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const engineers = await response.json();
        renderEngineersTable(engineers);
    } catch (error) {
        console.error('Ошибка загрузки инженеров:', error);
        showNotification('Ошибка загрузки инженеров: ' + error.message, 'danger');
        document.getElementById('engineers-table-body').innerHTML =
            '<tr><td colspan="5" class="text-center text-danger">Ошибка загрузки данных</td></tr>';
    }
}

// Отрисовка таблицы инженеров
function renderEngineersTable(engineers) {
    const tbody = document.getElementById('engineers-table-body');

    if (engineers.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Инженеров нет</td></tr>';
        return;
    }

    tbody.innerHTML = engineers.map(eng => `
        <tr>
            <td>${eng.id}</td>
            <td>${escapeHtml(eng.fio)}</td>
            <td>${getLineLevelName(eng.lineLevel)}</td>
            <td>${eng.active ? '<span class="badge bg-success">Да</span>' : '<span class="badge bg-secondary">Нет</span>'}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="deleteEngineer(${eng.id})">Удалить</button>
            </td>
        </tr>
    `).join('');
}

// Получение имени уровня инженера
function getLineLevelName(level) {
    const names = {
        'ONE': '1 линия',
        'TWO': '2 линия',
        'THREE': '3 линия'
    };
    return names[level] || level;
}

// Создание инженера
async function createEngineer() {
    const fio = document.getElementById('engineerFio').value.trim();
    const lineLevel = document.getElementById('engineerLevel').value;
    const active = document.getElementById('engineerActive').value === 'true';

    if (!fio || !lineLevel) {
        showNotification('Заполните все обязательные поля', 'warning');
        return;
    }

    const engineer = {
        fio: fio,
        lineLevel: lineLevel,
        active: active
    };

    try {
        const response = await fetch(`${API_BASE_URL}/sla/engineers`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(engineer)
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        showNotification('Инженер успешно добавлен!', 'success');
        document.getElementById('create-engineer-form').reset();
        loadEngineers();
    } catch (error) {
        console.error('Ошибка создания инженера:', error);
        showNotification('Ошибка создания инженера: ' + error.message, 'danger');
    }
}

// Удаление инженера
async function deleteEngineer(id) {
    if (!confirm('Вы уверены, что хотите удалить этого инженера?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/sla/engineers/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        showNotification('Инженер удален', 'success');
        loadEngineers();
    } catch (error) {
        console.error('Ошибка удаления:', error);
        showNotification('Ошибка удаления инженера: ' + error.message, 'danger');
    }
}

// Форматирование даты
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Экранирование HTML
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}