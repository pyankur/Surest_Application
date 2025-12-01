package com.surest.management.Surest_Management_App.repository;

import com.surest.management.Surest_Management_App.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
}
