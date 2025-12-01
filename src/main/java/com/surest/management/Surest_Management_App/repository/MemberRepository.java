package com.surest.management.Surest_Management_App.repository;

import com.surest.management.Surest_Management_App.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.UUID;


public interface MemberRepository extends JpaRepository<Member, UUID> {
    Page<Member> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);
    boolean existsByEmail(String email);
}
