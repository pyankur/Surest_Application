package com.surest.management.Surest_Management_App.service;

import com.surest.management.Surest_Management_App.config.JwtUtil;
import com.surest.management.Surest_Management_App.dto.AuthRequest;
import com.surest.management.Surest_Management_App.dto.AuthResponse;
import com.surest.management.Surest_Management_App.entity.AppUser;
import com.surest.management.Surest_Management_App.entity.Role;
import com.surest.management.Surest_Management_App.repository.RoleRepository;
import com.surest.management.Surest_Management_App.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success_returnsToken() {
        AuthRequest req = new AuthRequest();
        req.setUsername("alice");
        req.setPassword("password");

        Role role = new Role();
        role.setName("ROLE_USER");

        AppUser user = new AppUser();
        user.setUsername("alice");
        user.setPasswordHash("$2a$...hashed...");
        user.setRole(role);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("password"), anyString())).thenReturn(true);
        when(jwtUtil.generateToken("alice", "ROLE_USER")).thenReturn("jwt-token");

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("jwt-token", resp.getToken());
        verify(userRepository).findByUsername("alice");
        verify(passwordEncoder).matches(eq("password"), anyString());
        verify(jwtUtil).generateToken("alice", "ROLE_USER");
    }

    @Test
    void login_invalidPassword_throwsRuntimeException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("bob");
        req.setPassword("badpass");

        AppUser user = new AppUser();
        user.setUsername("bob");
        user.setPasswordHash("$2a$...hashed...");
        user.setRole(new Role());

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("badpass"), anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Invalid credentials", ex.getMessage());

        verify(userRepository).findByUsername("bob");
        verify(passwordEncoder).matches(eq("badpass"), anyString());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_userNotFound_throwsRuntimeException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("unknown");
        req.setPassword("pass");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Invalid credentials", ex.getMessage());

        verify(userRepository).findByUsername("unknown");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void register_success_createsUserAndReturnsToken() {
        AuthRequest req = new AuthRequest();
        req.setUsername("newuser");
        req.setPassword("newpass");
        req.setRole("ROLE_USER");

        Role role = new Role();
        role.setName("ROLE_USER");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$encoded");
        when(jwtUtil.generateToken("newuser", "ROLE_USER")).thenReturn("created-jwt");

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("created-jwt", resp.getToken());

        AppUser saved = userCaptor.getValue();
        assertEquals("newuser", saved.getUsername());
        assertEquals("$2a$encoded", saved.getPasswordHash());
        assertEquals("ROLE_USER", saved.getRole().getName());

        verify(userRepository).findByUsername("newuser");
        verify(roleRepository).findByName("ROLE_USER");
        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(any(AppUser.class));
        verify(jwtUtil).generateToken("newuser", "ROLE_USER");
    }

    @Test
    void register_usernameExists_throwsRuntimeException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("exists");
        req.setPassword("pass");

        when(userRepository.findByUsername("exists"))
                .thenReturn(Optional.of(new AppUser()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Username already exists", ex.getMessage());

        verify(userRepository).findByUsername("exists");
        verifyNoInteractions(roleRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void register_roleNotFound_throwsRuntimeException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("someone");
        req.setPassword("pass");
        req.setRole("ROLE_NON_EXISTENT");

        when(userRepository.findByUsername("someone")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_NON_EXISTENT")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Role not found: ROLE_NON_EXISTENT", ex.getMessage());

        verify(userRepository).findByUsername("someone");
        verify(roleRepository).findByName("ROLE_NON_EXISTENT");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }
}
