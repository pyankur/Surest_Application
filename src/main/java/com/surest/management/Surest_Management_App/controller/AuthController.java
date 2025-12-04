package com.surest.management.Surest_Management_App.controller;

import com.surest.management.Surest_Management_App.dto.AuthRequest;
import com.surest.management.Surest_Management_App.dto.AuthResponse;
import com.surest.management.Surest_Management_App.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody
                                              AuthRequest req) {
        var resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody
                                                 AuthRequest req) {
        var resp = authService.register(req);
        return ResponseEntity.status(201).body(resp);
    }

}
