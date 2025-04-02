package com.services2;

import com.user_entity.Task;
import com.user_entity.User;

import java.util.List;

public interface TaskService {
    Task addTask(Task task);
    List<Task> getTasksByUser(User user);
    void deleteTask(Long taskId);
    void toggleTaskCompletion(Long taskId);
    Task getTaskById(Long taskId);
    void updateTask(Task task); // Add this method
}