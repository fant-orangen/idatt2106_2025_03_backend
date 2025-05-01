package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.AuthResponseDto;
import stud.ntnu.backend.dto.auth.ForgotPasswordRequestDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.dto.auth.ResetPasswordRequestDto;
import stud.ntnu.backend.dto.auth.Send2FACodeRequestDto;
import stud.ntnu.backend.service.AuthService;
import stud.ntnu.backend.dto.auth.TwoFactorRequestDto;
import stud.ntnu.backend.service.RecaptchaService;

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

    private final RecaptchaService recaptchaService;

    private final Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, RecaptchaService recaptchaService) {
        this.authService = authService;
        this.recaptchaService = recaptchaService;
    }


    /**
     * Validates the JWT token from the Authorization header.
     * Returns 200 OK if the token is valid, 401 Unauthorized otherwise.
     *
     * @return ResponseEntity with status 200 OK if token is valid
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // The token validation is handled by Spring Security's JWT filter
        // If we reach this endpoint, the token is valid
        return ResponseEntity.ok().build();
    }

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param authRequest the authentication request containing email and password
     * @return ResponseEntity containing the JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequest) {
        // Verify the reCAPTCHA token
        System.out.println("Recaptcha token: " + authRequest.getRecaptchaToken());
        log.info("Login request received: {}", authRequest);
        if (!recaptchaService.verifyRecaptcha(authRequest.getRecaptchaToken())) {
            // Return a response with an error message in the AuthResponseDto
            AuthResponseDto errorResponse = new AuthResponseDto();
            errorResponse.setToken(null);
            errorResponse.setEmail(null);
            errorResponse.setUserId(null);
            errorResponse.setRole(null);
            errorResponse.setHouseholdId(null);
            errorResponse.setIsUsing2FA(false);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Proceed with login if reCAPTCHA is valid
        AuthResponseDto authResponse = authService.login(authRequest);

        if (authResponse.getIsUsing2FA()) {
            return ResponseEntity.status(202).body(authResponse);
        }
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Register a new user with the USER role.
     *
     * @param registrationRequest the registration request containing email, password, firstName, lastName,
     *                            and optional home address and location coordinates
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

    /**
     * Handles the email verification request.
     * Receives the token sent via the verification link in the email.
     *
     * @param token The verification token from the request parameter.
     * @return ResponseEntity indicating success or failure of the verification.
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
            // On success, return HTTP 200 OK with a success message
            return ResponseEntity.ok().body("Email successfully verified.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Catch specific exceptions for invalid/expired/used tokens
            // Return HTTP 400 Bad Request with the error message from the service
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors during the process
            // Log the error for debugging
            // Return HTTP 500 Internal Server Error
            return ResponseEntity.status(500)
                .body("An unexpected error occurred during email verification.");
        }
    }

    @PostMapping("/send-2fa")
    public ResponseEntity<?> send2FACode(@RequestBody @Valid Send2FACodeRequestDto request) {
        try {
            authService.send2FACode(request.getEmail());
            return ResponseEntity.ok("2FA code sent successfully.");
        } catch (Exception e) {
            log.error("Error sending 2FA code: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to send 2FA code.");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody @Valid TwoFactorRequestDto request) {
        try {
            AuthResponseDto response = authService.verify2FA(request.getEmail(), request.getCode());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error verifying 2FA code: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to verify 2FA code.");
        }
    }

    /**
     * Initiates the password reset process for a user.
     * Sends a password reset email with a token link.
     *
     * @param request The forgot password request containing the user's email
     * @return ResponseEntity with status 200 OK if successful, or an error message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        try {
            authService.forgotPassword(request.getEmail());
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (IllegalArgumentException e) {
            // Don't reveal if the email exists or not for security reasons
            // Just return a generic success message
            log.info("Forgot password request for non-existent email: {}", request.getEmail());
            return ResponseEntity.ok("If your email exists in our system, you will receive a password reset link.");
        } catch (Exception e) {
            log.error("Error processing forgot password request: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to process forgot password request.");
        }
    }

    /**
     * Resets a user's password using a reset token.
     *
     * @param request The reset password request containing the token and new password
     * @return ResponseEntity with status 200 OK if successful, or an error message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to reset password.");
        }
    }
}
