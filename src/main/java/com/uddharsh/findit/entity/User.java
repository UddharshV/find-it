package com.uddharsh.findit.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "users")
public class User {
    @Id                                                   // primary key: unique ID for each row
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // Postgres assigns 1, 2, 3...
    private Long id;

    @Column(nullable = false)                             // must have a value
    private String name;

    @Column(nullable = false, unique = true)              // no two users can share an email
    private String email;

    @CreationTimestamp                                    // filled in automatically on save
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {}                                   // Hibernate needs an empty constructor

    public User(String name, String email) {
        this.name = name;
        this.email = normalizeEmail(email);
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalizeEmail(email);
    }

    private static String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
    }
    
}
