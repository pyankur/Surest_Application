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
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    private UUID id;


    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

}