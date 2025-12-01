package com.surest.management.Surest_Management_App.repository;

import com.surest.management.Surest_Management_App.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;


public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
