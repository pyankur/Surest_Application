package com.surest.management.Surest_Management_App.entity;

import jakarta.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue
    private UUID id;


    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;


    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;


// getters and setters


        public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
