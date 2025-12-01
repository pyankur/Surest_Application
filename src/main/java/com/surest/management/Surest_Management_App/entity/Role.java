package com.surest.management.Surest_Management_App.entity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    private UUID id;


    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;


// getters and setters


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}