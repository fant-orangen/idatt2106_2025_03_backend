package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.AuthRequestDto;
import stud.ntnu.backend.dto.AuthResponseDto;
import stud.ntnu.backend.dto.RegisterRequestDto;
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
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Handles the email verification request.
   * Receives the token sent via the verification link in the email.
   *
   * @param token The verification token from the request parameter.
   * @return ResponseEntity indicating success or failure of the verification.
   */
  @GetMapping("/verify")
  public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
    // TODO: Implement the call to the service layer to handle verification logic
    return ResponseEntity.ok("Verification endpoint reached. Token: " + token);
  }
}
