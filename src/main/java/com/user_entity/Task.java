package com.user_entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private String description;

    @Column
    private boolean completed;

    @Column
    private boolean notified; // Add this field

    @Column
    private String taskdate;

    @Column
    private String  category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}