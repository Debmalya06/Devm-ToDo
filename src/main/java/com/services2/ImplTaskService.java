package com.services2;

import com.repo.TaskRepo;
import com.user_entity.Task;
import com.user_entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplTaskService implements TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Override
    public Task addTask(Task task) {
        return taskRepo.save(task);
    }

    @Override
    public List<Task> getTasksByUser(User user) {
        return taskRepo.findByUser(user);
    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepo.deleteById(taskId);
    }

    @Override
    public void toggleTaskCompletion(Long taskId) {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setCompleted(!task.isCompleted());
        taskRepo.save(task);
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepo.findById(taskId).orElse(null);
    }

    @Override
    public void updateTask(Task task) {
        taskRepo.save(task);
    }
}