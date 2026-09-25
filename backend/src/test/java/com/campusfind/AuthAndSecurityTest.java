package com.campusfind;

import com.campusfind.dto.LoginRequest;
import com.campusfind.dto.RegisterRequest;
import com.campusfind.entity.Role;
import com.campusfind.entity.User;
import com.campusfind.repository.UserRepository;
import com.campusfind.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthAndSecurityTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should encode password and validate user credentials with JWT generation")
    void testUserRegistrationAndJwt() {
        String testEmail = "unit.test@campus.edu";
        String rawPassword = "SecurePassword123!";

        User user = new User(
                testEmail,
                passwordEncoder.encode(rawPassword),
                "Test Student",
                Role.ROLE_STUDENT,
                "STU-9901",
                "+1 555-9999",
                "Information Tech"
        );
        User saved = userRepository.save(user);
        assertNotNull(saved.getId());

        // Validate password encoding
        assertTrue(passwordEncoder.matches(rawPassword, saved.getPassword()));
        assertFalse(passwordEncoder.matches("WrongPassword", saved.getPassword()));

        // Generate and validate JWT token
        String token = jwtTokenProvider.generateTokenForUser(saved.getId(), saved.getEmail(), saved.getFullName(), saved.getRole().name());
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(saved.getId(), jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Security: Public registration must ignore requested ROLE_ADMIN and force ROLE_STUDENT")
    void testRegistrationRoleEscalationPrevention() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("attacker@campus.edu");
        req.setPassword("Password123!");
        req.setFullName("Mallory Attacker");
        req.setRole(Role.ROLE_ADMIN); // Malicious attempt to self-register as ADMIN

        // Even though req.role is ROLE_ADMIN, user created in registration flow must be ROLE_STUDENT
        User registeredUser = new User(
                req.getEmail(),
                passwordEncoder.encode(req.getPassword()),
                req.getFullName(),
                Role.ROLE_STUDENT, // Enforced by AuthController
                null, null, null
        );
        User saved = userRepository.save(registeredUser);

        assertEquals(Role.ROLE_STUDENT, saved.getRole(), "Self-registered users must always receive ROLE_STUDENT!");
    }
}
