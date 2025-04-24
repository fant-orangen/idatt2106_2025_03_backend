package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.AuthResponseDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.service.AuthService;

/**
 * Handles user authentication and account lifecycle actions. Includes user registration, email
 * verification, login (JWT issuance), and password reset flows (forgot/reset).
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  private Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

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
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequest) {
    AuthResponseDto authResponse = authService.login(authRequest);
    return ResponseEntity.ok(authResponse);
  }

  /**
   * Register a new user with the USER role.
   *
   * @param registrationRequest the registration request containing email, password, firstName, lastName,
   *                           and optional home address and location coordinates
   * @return ResponseEntity with status 200 OK if successful
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registrationRequest) {
    try {
      authService.register(registrationRequest);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      log.info("User registration failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
