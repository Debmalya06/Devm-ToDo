document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const taskInput = document.getElementById('task-input');
    const taskDescription = document.getElementById('task-description');
    const taskCategory = document.getElementById('task-category');
    const taskDueDate = document.getElementById('task-due-date');
    const addTaskBtn = document.getElementById('add-task-btn');
    const taskList = document.getElementById('task-list');
    const filterBtns = document.querySelectorAll('.filter-btn');
    const totalTasksEl = document.getElementById('total-tasks');
    const completedTasksEl = document.getElementById('completed-tasks');
    const pendingTasksEl = document.getElementById('pending-tasks');
    const notificationContainer = document.getElementById('notification-container');

    // Set min date to today for due date input
    const today = new Date().toISOString().split('T')[0];
    taskDueDate.setAttribute('min', today);

    // Tasks array
    let tasks = JSON.parse(localStorage.getItem('tasks')) || [];
    let currentFilter = 'all';
    let editingTaskId = null;

    // Initialize
    renderTasks();
    updateStats();

    // Event Listeners
    addTaskBtn.addEventListener('click', addTask);
    taskInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            addTask();
        }
    });

    filterBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            filterBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            currentFilter = this.dataset.filter;
            renderTasks();
        });
    });

    // Functions
    function addTask() {
        const title = taskInput.value.trim();
        const description = taskDescription.value.trim();
        const category = taskCategory.value;
        const dueDate = taskDueDate.value;

        if (!title) {
            showNotification('Please enter a task title', 'error');
            return;
        }

        if (editingTaskId) {
            // Update existing task
            const taskIndex = tasks.findIndex(task => task.id === editingTaskId);
            if (taskIndex !== -1) {
                tasks[taskIndex] = {
                    ...tasks[taskIndex],
                    title,
                    description,
                    category,
                    dueDate,
                    updatedAt: new Date().toISOString()
                };
                showNotification('Task updated successfully');
                editingTaskId = null;
                addTaskBtn.textContent = 'Add Task';
            }
        } else {
            // Add new task
            const newTask = {
                id: Date.now().toString(),
                title,
                description,
                category,
                dueDate,
                completed: false,
                notified: false,
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString()
            };
            tasks.push(newTask);
            showNotification('Task added successfully');
        }

        // Save to localStorage
        saveTasks();

        // Clear inputs
        taskInput.value = '';
        taskDescription.value = '';
        taskCategory.value = '';
        taskDueDate.value = '';

        // Render tasks
        renderTasks();
        updateStats();
    }

    function renderTasks() {
        taskList.innerHTML = '';

        let filteredTasks = tasks;
        if (currentFilter === 'active') {
            filteredTasks = tasks.filter(task => !task.completed);
        } else if (currentFilter === 'completed') {
            filteredTasks = tasks.filter(task => task.completed);
        }

        if (filteredTasks.length === 0) {
            taskList.innerHTML = `
                <div class="empty-state">
                    <p>No ${currentFilter === 'all' ? '' : currentFilter} tasks found</p>
                </div>
            `;
            return;
        }

        filteredTasks.forEach(task => {
            const taskElement = document.createElement('div');
            taskElement.classList.add('task-item');
            if (task.completed) {
                taskElement.classList.add('completed');
            }

            // Format due date if exists
            let formattedDueDate = '';
            if (task.dueDate) {
                const date = new Date(task.dueDate);
                formattedDueDate = date.toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric',
                    year: 'numeric'
                });
            }

            taskElement.innerHTML = `
                <div class="task-header">
                    <div>
                        <div class="task-title">${task.title}</div>
                        <div class="task-meta">
                            ${task.category ? `<div class="task-category"><i class="fas fa-tag"></i> ${task.category}</div>` : ''}
                            ${task.dueDate ? `<div class="task-due-date"><i class="far fa-calendar"></i> ${formattedDueDate}</div>` : ''}
                        </div>
                    </div>
                </div>
                ${task.description ? `<div class="task-description">${task.description}</div>` : ''}
                <div class="task-actions">
                    <button class="task-btn complete-btn" data-id="${task.id}">
                        <i class="fas ${task.completed ? 'fa-undo' : 'fa-check'}"></i>
                        ${task.completed ? 'Undo' : 'Complete'}
                    </button>
                    <button class="task-btn edit-btn" data-id="${task.id}">
                        <i class="fas fa-edit"></i>
                        Edit
                    </button>
                    <button class="task-btn delete-btn" data-id="${task.id}">
                        <i class="fas fa-trash"></i>
                        Delete
                    </button>
                    <button class="task-btn notify-btn" data-id="${task.id}" ${task.notified ? 'disabled' : ''}>
                        <i class="fas fa-bell"></i>
                        ${task.notified ? 'Notified' : 'Notify'}
                    </button>
                </div>
            `;

            // Add event listeners
            taskElement.querySelector('.complete-btn').addEventListener('click', function() {
                toggleTaskComplete(task.id);
            });

            taskElement.querySelector('.delete-btn').addEventListener('click', function() {
                deleteTask(task.id);
            });

            taskElement.querySelector('.notify-btn').addEventListener('click', function() {
                notifyTask(task.id);
            });

            taskElement.querySelector('.edit-btn').addEventListener('click', function() {
                editTask(task.id);
            });

            taskList.appendChild(taskElement);
        });
    }

    function toggleTaskComplete(taskId) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        if (taskIndex !== -1) {
            tasks[taskIndex].completed = !tasks[taskIndex].completed;
            tasks[taskIndex].updatedAt = new Date().toISOString();
            saveTasks();
            renderTasks();
            updateStats();
            showNotification(`Task marked as ${tasks[taskIndex].completed ? 'completed' : 'active'}`);
        }
    }

    function deleteTask(taskId) {
        tasks = tasks.filter(task => task.id !== taskId);
        saveTasks();
        renderTasks();
        updateStats();
        showNotification('Task deleted successfully');
    }

    function editTask(taskId) {
        const task = tasks.find(task => task.id === taskId);
        if (task) {
            taskInput.value = task.title;
            taskDescription.value = task.description || '';
            taskCategory.value = task.category || '';
            taskDueDate.value = task.dueDate || '';
            editingTaskId = taskId;
            addTaskBtn.textContent = 'Update Task';
            taskInput.focus();
            // Scroll to form
            document.querySelector('.task-form').scrollIntoView({ behavior: 'smooth' });
        }
    }

    function notifyTask(taskId) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        if (taskIndex !== -1 && !tasks[taskIndex].notified) {
            // Check if browser supports notifications
            if (!("Notification" in window)) {
                showNotification("This browser does not support desktop notifications");
                return;
            }

            // Request permission
            Notification.requestPermission().then(permission => {
                if (permission === "granted") {
                    const task = tasks[taskIndex];
                    const notification = new Notification("Devm Todo Reminder", {
                        body: task.title,
                        icon: "https://via.placeholder.com/64"
                    });

                    tasks[taskIndex].notified = true;
                    saveTasks();
                    renderTasks();
                    showNotification('Notification sent');
                }
            });
        }
    }

    function updateStats() {
        const totalTasks = tasks.length;
        const completedTasks = tasks.filter(task => task.completed).length;
        const pendingTasks = totalTasks - completedTasks;

        totalTasksEl.textContent = totalTasks;
        completedTasksEl.textContent = completedTasks;
        pendingTasksEl.textContent = pendingTasks;
    }

    function saveTasks() {
        localStorage.setItem('tasks', JSON.stringify(tasks));
    }

    function showNotification(message, type = 'success') {
        const notification = document.createElement('div');
        notification.classList.add('notification');
        notification.innerHTML = `
            <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
            <span>${message}</span>
        `;
        
        notificationContainer.appendChild(notification);
        
        // Remove notification after 3 seconds
        setTimeout(() => {
            notification.classList.add('hide');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }

    // Check for tasks with due dates and show notifications
    function checkDueTasks() {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        tasks.forEach(task => {
            if (!task.completed && task.dueDate) {
                const dueDate = new Date(task.dueDate);
                dueDate.setHours(0, 0, 0, 0);
                
                // If due date is today or past due
                if (dueDate <= today && !task.dueDateNotified) {
                    showNotification(`Task "${task.title}" is ${dueDate < today ? 'overdue' : 'due today'}!`, 'error');
                    
                    // Mark as notified for due date
                    const taskIndex = tasks.findIndex(t => t.id === task.id);
                    if (taskIndex !== -1) {
                        tasks[taskIndex].dueDateNotified = true;
                        saveTasks();
                    }
                }
            }
        });
    }

    // Check due tasks on load
    checkDueTasks();
});