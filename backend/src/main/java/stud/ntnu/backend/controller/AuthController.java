package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.AuthRequestDto;
import stud.ntnu.backend.dto.AuthResponseDto;
import stud.ntnu.backend.service.AuthService;

/**
 * Handles user authentication and account lifecycle actions.
 * Includes user registration, email verification, login (JWT issuance),
 * and password reset flows (forgot/reset).
 *
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param authRequest the authentication request containing email and password
     * @return ResponseEntity containing the JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequest) {
        AuthResponseDto authResponse = authService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }
}
