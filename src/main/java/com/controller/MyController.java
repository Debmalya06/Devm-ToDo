package com.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.user_entity.Task;
import com.user_entity.User;

import jakarta.servlet.http.HttpSession;

import com.services2.TaskService;
import com.services2.UserService;

@Controller
public class MyController {
    @Autowired
    private TaskService taskService;
    @Autowired
    UserService Userser;
    @Autowired
    private JavaMailSender mailSender;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @GetMapping("/")
    public String getMethodName() {
        return "index";
    }

    @GetMapping("/loginpage")
    public String login(Model model) {
        model.addAttribute("userlogin", new User());
        return "login_page";
    }

    @GetMapping("/todo")
    public String todopage() {
        return "redirect:/tasks";
    }

    @GetMapping("/Regpage")
    public String Reg(Model model) {
        model.addAttribute("user", new User());
        return "reg";
    }

    @PostMapping("/userregister")
    public String postMethodName(@ModelAttribute("user") User user, Model model) {
        boolean status = Userser.AddUser(user);
        if (status) {
            model.addAttribute("SucMsg", "Register Successful");
        } else {
            model.addAttribute("FailMsg", "Register Failed");
        }
        return "reg";
    }

    @GetMapping("/tasks")
    public String getTasks(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/loginpage";
        }
        List<Task> tasks = taskService.getTasksByUser(user);
        model.addAttribute("tasks", tasks);
        return "Todo";
    }

    @PostMapping("/addTask")
    public String addTask(@RequestParam String description, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/loginpage";
        }
        Task task = new Task();
        task.setDescription(description);
        task.setCompleted(false);
        task.setUser(user);
        taskService.addTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/toggleTask/{id}")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleTaskCompletion(id);
        return "redirect:/tasks";
    }

    @PostMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    @PostMapping("/Luser")
    public String Elogin(@ModelAttribute("userlogin") User user, Model model, HttpSession session) {
        User validUser = Userser.login_user(user.getEmail(), user.getSetPassword());
        if (validUser != null) {
            session.setAttribute("user", validUser);
            return "user_dashboard";
        } else {
            model.addAttribute("FailMsg", "Login Failed! Incorrect Email or Password");
            return "login_page";
        }
    }

    @GetMapping("/logout")
    public String logoutMethod() {
        return "login_page";
    }

    @PostMapping("/notifyTask/{id}")
    public String notifyTask(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/loginpage";
        }
        Task task = taskService.getTaskById(id);
        if (task != null && !task.isCompleted()) {
            // Send an immediate notification
            sendEmailNotification(user.getEmail(), task.getDescription());

            // Schedule a daily reminder at 8 AM
            scheduleDailyReminder(user.getEmail(), task.getId(), task.getDescription());
        }
        return "redirect:/tasks";
    }

    private void sendEmailNotification(String email, String taskDescription) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("DEVMToDo Task Notification");
            message.setText("Reminder for your task: " + taskDescription + "\nDo your task fast!\nThanks for using DEVMToDo.");
            mailSender.send(message);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleDailyReminder(String email, Long taskId, String taskDescription) {
        long initialDelay = calculateInitialDelayFor8AM();
        long period = 24; // Repeat every 24 hours

        scheduler.scheduleAtFixedRate(() -> {
            Task task = taskService.getTaskById(taskId);
            if (task != null && !task.isCompleted()) {
                sendEmailNotification(email, "Reminder: " + taskDescription + " is still pending!");
            }
        }, initialDelay, period, TimeUnit.HOURS);
    }

    private long calculateInitialDelayFor8AM() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1); // Schedule for the next day if 8 AM has passed
        }

        return Duration.between(now, nextRun).toHours();
    }
}
