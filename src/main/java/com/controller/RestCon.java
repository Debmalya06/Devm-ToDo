package com.controller;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import com.user_entity.Task;
import com.user_entity.User;
import jakarta.servlet.http.HttpSession;
import com.services2.TaskService;
import com.services2.UserService;

@RestController
@RequestMapping("/api")
public class RestCon {
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/")
    public String home() {
        return "Welcome to the API";
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        boolean status = userService.AddUser(user);
        return status ? "Register Successful" : "Register Failed";
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user, HttpSession session) {
        User validUser = userService.login_user(user.getEmail(), user.getSetPassword());
        if (validUser != null) {
            session.setAttribute("user", validUser);
            return "User Login Successful";
        } else {
            return "Login Failed! Incorrect Email or Password";
        }
    }

    @GetMapping("/tasks")
    public List<Task> getTasks(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        return taskService.getTasksByUser(user);
    }

    @PostMapping("/tasks")
    public String addTask(@RequestBody Task task, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        task.setUser(user);
        taskService.addTask(task);
        return "Task added successfully";
    }

    @PutMapping("/tasks/{id}/toggle")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleTaskCompletion(id);
        return "Task status updated";
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "Task deleted successfully";
    }

    @PostMapping("/tasks/{id}/notify")
    public String notifyTask(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Task task = taskService.getTaskById(id);
        if (task != null) {
            task.setNotified(true);
            taskService.updateTask(task);
            scheduleEmailNotification(user.getEmail(), task.getDescription());
            return "Notification scheduled";
        }
        return "Task not found";
    }

    private void scheduleEmailNotification(String email, String taskDescription) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> sendEmailNotification(email, taskDescription), 1, TimeUnit.MINUTES);
    }

    private void sendEmailNotification(String email, String taskDescription) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("DEVMToDo Task Notification");
            message.setText("Reminder for your task: " + taskDescription + "\nDo your task fast.\nThanks for using DEVMToDo.");
            mailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
