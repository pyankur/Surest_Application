package com.surest.management.Surest_Management_App.controller;


import com.surest.management.Surest_Management_App.dto.AuthRequest;
import com.surest.management.Surest_Management_App.dto.AuthResponse;
import com.surest.management.Surest_Management_App.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success_returnsResponseEntityWithToken() {
        AuthRequest req = new AuthRequest();
        req.setUsername("alice");
        req.setPassword("password");

        when(authService.login(any(AuthRequest.class))).thenReturn(new AuthResponse("jwt-token"));

        var response = authController.login(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());

        verify(authService, times(1)).login(any(AuthRequest.class));
    }

    @Test
    void login_whenServiceThrows_propagatesException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("bad");
        req.setPassword("bad");

        when(authService.login(any(AuthRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.login(req));
        assertEquals("Invalid credentials", ex.getMessage());

        verify(authService, times(1)).login(any(AuthRequest.class));
    }

    @Test
    void register_success_returnsCreatedWithToken() {
        AuthRequest req = new AuthRequest();
        req.setUsername("new");
        req.setPassword("p");
        req.setRole("ROLE_USER");

        when(authService.register(any(AuthRequest.class))).thenReturn(new AuthResponse("created-jwt"));

        var response = authController.register(req);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("created-jwt", response.getBody().getToken());

        verify(authService, times(1)).register(any(AuthRequest.class));
    }

    @Test
    void register_whenServiceThrows_propagatesException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("exists");
        req.setPassword("p");

        when(authService.register(any(AuthRequest.class))).thenThrow(new RuntimeException("Username already exists"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.register(req));
        assertEquals("Username already exists", ex.getMessage());

        verify(authService, times(1)).register(any(AuthRequest.class));
    }
}
