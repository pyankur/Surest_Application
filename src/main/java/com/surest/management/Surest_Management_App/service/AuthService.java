package com.surest.management.Surest_Management_App.service;

import com.surest.management.Surest_Management_App.config.JwtUtil;
import com.surest.management.Surest_Management_App.dto.AuthRequest;
import com.surest.management.Surest_Management_App.dto.AuthResponse;
import com.surest.management.Surest_Management_App.entity.AppUser;
import com.surest.management.Surest_Management_App.repository.RoleRepository;
import com.surest.management.Surest_Management_App.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;


    public AuthService(UserRepository userRepository, PasswordEncoder
            passwordEncoder, JwtUtil jwtUtil,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleRepository=roleRepository;
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        AppUser user =
                userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new
                        RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(),
                user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername(),
                user.getRole().getName());
        return new AuthResponse(token);
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        String roleName = (request.getRole() == null || request.getRole().isBlank()) ?
                "ROLE_USER" : request.getRole();
        var role = roleRepository.findByName(roleName).orElseThrow(() -> new
                RuntimeException("Role not found: " + roleName));
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(),
                user.getRole().getName());
        return new AuthResponse(token);
    }


}
