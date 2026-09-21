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
}
