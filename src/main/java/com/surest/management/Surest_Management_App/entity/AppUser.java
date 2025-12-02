package com.surest.management.Surest_Management_App.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
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


}
