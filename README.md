# Devm Todo - Task Manager

## Overview

Devm Todo is a task management system built with **Spring Boot & Thymeleaf** that integrates **Google's Gemini API** to enhance task input with **natural language processing (NLP)**. Users can add tasks in casual language, and the AI will correct and structure the input before saving it to the database.

## Features

- ✅ **Task Management**: Add, edit, delete, and mark tasks as completed.
- 📩 **Email Notifications**: Get notified about pending tasks.
- 🔐 **U**ser Authentication: Secure login/logout functionality.

## Tech Stack

- **Backend**: Spring Boot, MySQL
- **Frontend**: Thymeleaf, Tailwind, JavaScript

## Installation Guide

### Prerequisites

Ensure you have the following installed:

- Java 17+
- Maven
- MySQL

### Steps to Run the Project

1. **Clone the Repository**:

   ```sh
   git clone https://github.com/yourusername/devm-todo.git
   cd devm-todo
   ```
   
2. **Set Up MySQL Database**:

   - Create a database named `todo_db`.
   - Update `application.properties` with your database credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/todo_db
     spring.datasource.username=root
     spring.datasource.password=yourpassword
     ```



3. **Run the Application**:

   ```sh
   mvn spring-boot:run
   ```

5. **Access the Application**:
   Open `http://localhost:8081` in your browser.

## Gemini AI Integration

### How It Works

1. User adds a task in casual language.
2. The backend calls Gemini API to correct & structure the task.
3. The formatted task is saved in the database.

### Example

**User Input:** "buy milk tomorrow morning"
**AI-Processed Task:** "Buy milk on [tomorrow's date] at morning time."

## API Endpoints

| Method | Endpoint      | Description    |
| ------ | ------------- | -------------- |
| POST   | `/tasks/add`  | Add a new task |
| GET    | `/tasks`      | List all tasks |
| PUT    | `/tasks/{id}` | Update a task  |
| DELETE | `/tasks/{id}` | Delete a task  |

## Future Enhancements

- ✅ Add voice-based task input.
- ✅ Implement AI-generated task reminders.

## License

This project is licensed under the MIT License.

---

🚀 **Devm Todo - Organize smarter, work better!**

![alt text](/readmeImage/image.png)
![alt text](/readmeImage/image-1.png)
![alt text](/readmeImage/Screenshot%202025-04-03%20011942.png)
