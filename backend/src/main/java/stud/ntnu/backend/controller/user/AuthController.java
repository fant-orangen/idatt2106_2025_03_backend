package stud.ntnu.backend.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.auth.*;
import stud.ntnu.backend.service.user.AuthService;
import stud.ntnu.backend.service.user.RecaptchaService;

/**
 * Handles user authentication and account lifecycle actions. Includes user registration, email
 * verification, login (JWT issuance), two-factor authentication (2FA), reCAPTCHA validation, and
 * password reset flows (forgot/reset).
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Operations for user authentication, registration, and account management")
public class AuthController
{

  private final AuthService authService;
  private final RecaptchaService recaptchaService;

  public AuthController(AuthService authService, RecaptchaService recaptchaService)
  {
    this.authService = authService;
    this.recaptchaService = recaptchaService;
  }

  /**
   * Validates the JWT token from the Authorization header.
   *
   * @return ResponseEntity with status 200 OK if token is valid, 401 Unauthorized otherwise
   */
  @Operation(summary = "Validate token", description = "Validates the JWT token from the Authorization header.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token is valid"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - invalid token")
  })
  @GetMapping("/validate")
  public ResponseEntity<?> validateToken()
  {
    return ResponseEntity.ok().build();
  }

  /**
   * Authenticate a user and generate a JWT token.
   *
   * @param authRequest the authentication request containing email and password
   * @return ResponseEntity containing the JWT token and user information
   */
  @Operation(summary = "Login", description = "Authenticate a user and generate a JWT token. Requires reCAPTCHA validation.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully authenticated",
          content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
      @ApiResponse(responseCode = "202", description = "2FA required",
          content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid credentials or reCAPTCHA",
          content = @Content(schema = @Schema(implementation = AuthResponseDto.class)))
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequest)
  {
    // Verify the reCAPTCHA token
    if (!recaptchaService.verifyRecaptcha(authRequest.getRecaptchaToken()))
    {
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

    return authResponse.getIsUsing2FA()
        ? ResponseEntity.status(202).body(authResponse)
        : ResponseEntity.ok(authResponse);
  }

  /**
   * Registers a new user with the USER role.
   *
   * @param registrationRequest the registration request containing user details
   * @return ResponseEntity with status 200 OK if successful, 400 Bad Request if registration fails
   */
  @Operation(summary = "Register", description = "Registers a new user with the USER role.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid registration data",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registrationRequest) {
    try
    {
      authService.register(registrationRequest);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e)
    {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (MessagingException e)
    {
      return ResponseEntity.status(500)
          .body("Failed to send verification email. Please try again later.");
    }
  }


  /**
   * Handles email verification using a token sent via email.
   *
   * @param token the verification token from the request parameter
   * @return ResponseEntity indicating success or failure of the verification
   */
  @Operation(summary = "Verify email", description = "Handles email verification using a token sent via email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email successfully verified",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid token",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/verify")
  public ResponseEntity<?> verifyEmail(@RequestParam("token") String token)
  {
    try
    {
      authService.verifyEmail(token);
      return ResponseEntity.ok().body("Email successfully verified.");
    } catch (IllegalArgumentException | IllegalStateException e)
    {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e)
    {
      return ResponseEntity.status(500)
          .body("An unexpected error occurred during email verification.");
    }
  }

  /**
   * Initiates the 2FA process by sending a verification code to the user's email.
   *
   * @param request the request containing the user's email
   * @return ResponseEntity with status 200 OK if successful, 500 Internal Server Error if sending
   * fails
   */
  @Operation(summary = "Send 2FA code", description = "Initiates the 2FA process by sending a verification code to the user's email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "2FA code sent successfully",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Failed to send 2FA code",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/send-2fa")
  public ResponseEntity<?> send2FACode(@RequestBody @Valid Send2FACodeRequestDto request)
  {
    try
    {
      authService.send2FACode(request.getEmail());
      return ResponseEntity.ok("2FA code sent successfully.");
    } catch (Exception e)
    {
      return ResponseEntity.status(500).body("Failed to send 2FA code.");
    }
  }

  /**
   * Completes the 2FA process by verifying the code sent to the user's email.
   *
   * @param request the request containing the user's email and verification code
   * @return ResponseEntity with status 200 OK if successful, 400 Bad Request if code is invalid
   */
  @Operation(summary = "Verify 2FA", description = "Completes the 2FA process by verifying the code sent to the user's email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully verified 2FA",
          content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid code",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Failed to verify 2FA code",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/verify-2fa")
  public ResponseEntity<?> verify2FA(@RequestBody @Valid TwoFactorRequestDto request)
  {
    try
    {
      AuthResponseDto response = authService.verify2FA(request.getEmail(), request.getCode());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e)
    {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e)
    {
      return ResponseEntity.status(500).body("Failed to verify 2FA code.");
    }
  }

  /**
   * Initiates the password reset process by sending a reset link to the user's email.
   *
   * @param request the request containing the user's email
   * @return ResponseEntity with status 200 OK and a success message
   */
  @Operation(summary = "Forgot password", description = "Initiates the password reset process by sending a reset link to the user's email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reset link sent if email exists",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Failed to process forgot password request",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request)
  {
    try
    {
      authService.forgotPassword(request.getEmail());
      return ResponseEntity.ok(
          "If your email exists in our system, you will receive a password reset link.");
    } catch (IllegalArgumentException e)
    {
      return ResponseEntity.ok(
          "If your email exists in our system, you will receive a password reset link.");
    } catch (Exception e)
    {
      return ResponseEntity.status(500).body("Failed to process forgot password request.");
    }
  }

  /**
   * Resets a user's password using a valid reset token.
   *
   * @param request the request containing the reset token and new password
   * @return ResponseEntity with status 200 OK if successful, 400 Bad Request if token is invalid
   */
  @Operation(summary = "Reset password", description = "Resets a user's password using a valid reset token.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password reset successfully",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid token",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Failed to reset password",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request)
  {
    try
    {
      authService.resetPassword(request.getToken(), request.getNewPassword());
      return ResponseEntity.ok("Password reset successfully.");
    } catch (IllegalArgumentException | IllegalStateException e)
    {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e)
    {
      return ResponseEntity.status(500).body("Failed to reset password.");
    }
  }

  /**
   * Changes the password for the currently authenticated user.
   *
   * @param request the request containing the new password
   * @return ResponseEntity with status 200 OK if successful, 400 Bad Request if password change
   * fails
   */
  @Operation(summary = "Change password", description = "Changes the password for the currently authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password changed successfully",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid password",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Failed to change password",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto request)
  {
    try
    {
      authService.changePassword(request);
      return ResponseEntity.ok("Password changed successfully.");
    } catch (IllegalArgumentException | IllegalStateException e)
    {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e)
    {
      return ResponseEntity.status(500).body("Failed to change password - " + e.getMessage());
    }
  }
}
