package stud.ntnu.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.mail.MessagingException;

import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.AuthResponseDto;
import stud.ntnu.backend.dto.auth.ChangePasswordDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.model.user.EmailToken;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.TwoFactorCode;
import stud.ntnu.backend.repository.user.*;
import stud.ntnu.backend.util.JwtUtil;

@DisplayName("Authentication Service Tests")
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @Mock private EmailTokenRepository emailTokenRepository;
    @Mock private TwoFactorCodeService twoFactorCodeService;
    @Mock private NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks private AuthService authService;

    // Test fixtures
    private User standardUser;
    private User userWith2FA;
    private Role userRole;
    private String validToken;
    private String validPassword;
    private String validEmail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestFixtures();
    }

    private void setupTestFixtures() {
        // Setup common test data
        validEmail = "test@example.com";
        validPassword = "Password123!";
        validToken = "valid-token";

        userRole = new Role();
        userRole.setId(1);
        userRole.setName("USER");

        standardUser = new User();
        standardUser.setId(1);
        standardUser.setEmail(validEmail);
        standardUser.setPasswordHash(validPassword);
        standardUser.setRole(userRole);
        standardUser.setIsUsing2FA(false);

        userWith2FA = new User();
        userWith2FA.setId(2);
        userWith2FA.setEmail("2fa@example.com");
        userWith2FA.setPasswordHash(validPassword);
        userWith2FA.setRole(userRole);
        userWith2FA.setIsUsing2FA(true);
    }

    @Nested
    @DisplayName("Login Process")
    class LoginTests {

        @Nested
        @DisplayName("Successful Scenarios")
        class SuccessfulLogin {

            @Test
            @DisplayName("Standard login should return valid JWT token")
            void standardLoginShouldReturnValidJwtToken() {
                // Given
                AuthRequestDto request = new AuthRequestDto(validEmail, validPassword, "recaptcha-token");
                Authentication auth = mock(Authentication.class);

                when(authenticationManager.authenticate(any())).thenReturn(auth);
                when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(standardUser));
                when(jwtUtil.generateToken(validEmail)).thenReturn("jwt-token");

                // When
                AuthResponseDto response = authService.login(request);

                // Then
                assertNotNull(response);
                assertEquals("jwt-token", response.getToken());
                assertEquals(validEmail, response.getEmail());
                assertEquals(1, response.getUserId());
                assertEquals("USER", response.getRole());
                assertFalse(response.getIsUsing2FA());
            }

            @Test
            @DisplayName("2FA-enabled login should indicate 2FA requirement")
            void twoFactorLoginShouldIndicateRequirement() {
                // Given
                AuthRequestDto request = new AuthRequestDto("2fa@example.com", validPassword, "recaptcha-token");
                Authentication auth = mock(Authentication.class);

                when(authenticationManager.authenticate(any())).thenReturn(auth);
                when(userRepository.findByEmail("2fa@example.com")).thenReturn(Optional.of(userWith2FA));

                // When
                AuthResponseDto response = authService.login(request);

                // Then
                assertNotNull(response);
                assertNull(response.getToken()); // No token for 2FA yet
                assertEquals("2fa@example.com", response.getEmail());
                assertTrue(response.getIsUsing2FA());
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class FailedLogin {

            @Test
            @DisplayName("Invalid credentials should throw BadCredentialsException")
            void invalidCredentialsShouldThrowException() {
                // Given
                AuthRequestDto request = new AuthRequestDto("wrong@example.com", "wrongpass", "recaptcha-token");

                when(authenticationManager.authenticate(any()))
                        .thenThrow(new BadCredentialsException("Invalid credentials"));

                // When/Then
                assertThrows(BadCredentialsException.class, () -> authService.login(request));
            }

            @Test
            @DisplayName("User not found after authentication should throw exception")
            void userNotFoundShouldThrowException() {
                // Given
                AuthRequestDto request = new AuthRequestDto("missing@example.com", validPassword, "recaptcha-token");
                Authentication auth = mock(Authentication.class);

                when(authenticationManager.authenticate(any())).thenReturn(auth);
                when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

                // When/Then
                assertThrows(BadCredentialsException.class, () -> authService.login(request));
            }
        }
    }

    @Nested
    @DisplayName("Registration Process")
    class RegistrationTests {

        @Test
        @DisplayName("Valid registration should create user and send verification email")
        void validRegistrationShouldCreateUserAndSendEmail()  throws MessagingException {
            // Given
            RegisterRequestDto request = createValidRegistrationRequest();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("hashed-password");
            when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // When
            authService.register(request);

            // Then
            verify(userRepository).save(any(User.class));
            verify(emailTokenRepository).save(any(EmailToken.class));
            verify(emailService).sendVerificationEmail(any(User.class), anyString());
            verify(notificationPreferenceRepository, atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("Registration with existing email should throw exception")
        void existingEmailShouldThrowException() {
            // Given
            RegisterRequestDto request = createValidRegistrationRequest();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.register(request));
            verify(userRepository, never()).save(any());
            verify(emailTokenRepository, never()).save(any());
        }

        private RegisterRequestDto createValidRegistrationRequest() {
            RegisterRequestDto request = new RegisterRequestDto();
            request.setEmail("new@example.com");
            request.setPassword("securePass123!");
            request.setFirstName("Alice");
            request.setLastName("Doe");
            request.setPhoneNumber("12345678");
            request.setPrivacyPolicyAccepted(true);
            return request;
        }
    }
    @Nested
    @DisplayName("Email Verification")
    class EmailVerificationTests {

        @Test
        @DisplayName("Valid verification token should activate user account")
        void validTokenShouldActivateAccount() {
            // Given
            User user = new User();
            user.setId(1);
            user.setEmail(validEmail);
            user.setEmailVerified(false);

            EmailToken emailToken = new EmailToken(
                user, validToken, EmailToken.TokenType.VERIFICATION,
                LocalDateTime.now().plusHours(24)
            );

            when(emailTokenRepository.findByToken(validToken)).thenReturn(Optional.of(emailToken));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            authService.verifyEmail(validToken);

            // Then
            assertTrue(user.getEmailVerified());
            verify(userRepository).save(user);
            verify(emailTokenRepository).save(emailToken);
            assertNotNull(emailToken.getUsedAt());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideInvalidVerificationScenarios")
        @DisplayName("Invalid verification scenarios should throw appropriate exceptions")
        void invalidVerificationScenarios(String scenarioName, String token,
                                         Optional<EmailToken> returnedToken,
                                         Class<? extends Exception> expectedException) {
            // Given
            when(emailTokenRepository.findByToken(token)).thenReturn(returnedToken);

            // When/Then
            assertThrows(expectedException, () -> authService.verifyEmail(token));
        }

        static Stream<Arguments> provideInvalidVerificationScenarios() {
            User user = new User();

            EmailToken expiredToken = new EmailToken(
                user, "expired-token", EmailToken.TokenType.VERIFICATION,
                LocalDateTime.now().minusHours(1)
            );

            EmailToken usedToken = new EmailToken(
                user, "used-token", EmailToken.TokenType.VERIFICATION,
                LocalDateTime.now().plusHours(24)
            );
            usedToken.setUsedAt(LocalDateTime.now());

            EmailToken wrongTypeToken = new EmailToken(
                user, "wrong-type-token", EmailToken.TokenType.RESET,
                LocalDateTime.now().plusHours(24)
            );

            EmailToken noUserToken = new EmailToken(
                null, "no-user-token", EmailToken.TokenType.VERIFICATION,
                LocalDateTime.now().plusHours(24)
            );

            return Stream.of(
                Arguments.of("Token not found", "invalid-token", Optional.empty(), IllegalArgumentException.class),
                Arguments.of("Token expired", "expired-token", Optional.of(expiredToken), IllegalStateException.class),
                Arguments.of("Token already used", "used-token", Optional.of(usedToken), IllegalStateException.class),
                Arguments.of("Wrong token type", "wrong-type-token", Optional.of(wrongTypeToken), IllegalStateException.class),
                Arguments.of("No user associated", "no-user-token", Optional.of(noUserToken), IllegalStateException.class)
            );
        }
    }

    @Nested
    @DisplayName("Two-Factor Authentication")
    class TwoFactorAuthTests {

        @Test
        @DisplayName("Valid 2FA code should return JWT token")
        void valid2FACodeShouldReturnToken() {
            // Given
            String email = "2fa@example.com";
            Integer code = 123456;

            when(twoFactorCodeService.verifyCode(email, code)).thenReturn(true);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(userWith2FA));
            when(jwtUtil.generateToken(email)).thenReturn("jwt-token");

            // When
            AuthResponseDto response = authService.verify2FA(email, code);

            // Then
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            assertEquals(email, response.getEmail());
            assertEquals(2, response.getUserId());
            assertEquals("USER", response.getRole());
        }

        @Test
        @DisplayName("Invalid 2FA code should throw exception")
        void invalid2FACodeShouldThrowException() {
            // Given
            String email = "2fa@example.com";
            Integer code = 123456;

            when(twoFactorCodeService.verifyCode(email, code)).thenReturn(false);

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.verify2FA(email, code));
        }

        @Test
        @DisplayName("User not found during 2FA verification should throw exception")
        void userNotFoundShouldThrowException() {
            // Given
            String email = "nonexistent@example.com";
            Integer code = 123456;

            when(twoFactorCodeService.verifyCode(email, code)).thenReturn(true);
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.verify2FA(email, code));
        }

        @Test
        @DisplayName("Send 2FA code should call service")
        void send2FACodeShouldCallService() throws MessagingException {
            // Given
            String email = "2fa@example.com";

            // When
            authService.send2FACode(email);

            // Then
            verify(twoFactorCodeService).sendVerificationCode(email);
        }
    }

    @Nested
    @DisplayName("Forgot Password Process")
    class ForgotPasswordTests {

        @Test
        @DisplayName("Should initiate password reset successfully")
        void shouldInitiatePasswordResetSuccessfully() {
            // Given
            String email = validEmail;

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(standardUser));
            when(emailTokenRepository.save(any(EmailToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            authService.forgotPassword(email);

            // Then
            verify(emailTokenRepository).save(any(EmailToken.class));
            verify(emailService).sendPasswordResetEmail(eq(standardUser), anyString());
        }

        @Test
        @DisplayName("Should throw when user not found")
        void shouldThrowWhenUserNotFound() {
            // Given
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.forgotPassword(email));
            verify(emailTokenRepository, never()).save(any());
            verify(emailService, never()).sendPasswordResetEmail(any(), anyString());
        }
    }

    @Nested
    @DisplayName("Password Reset")
    class PasswordResetTests {

        @Test
        @DisplayName("Valid reset token should update password")
        void validResetTokenShouldUpdatePassword() {
            // Given
            String token = "reset-token";
            String newPassword = "NewPassword123!";

            User user = new User();
            user.setId(1);
            user.setEmail(validEmail);
            user.setPasswordHash("oldPasswordHash");

            EmailToken emailToken = new EmailToken(
                user, token, EmailToken.TokenType.RESET,
                LocalDateTime.now().plusMinutes(10)
            );

            when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(passwordEncoder.encode(newPassword)).thenReturn("newPasswordHash");

            // When
            authService.resetPassword(token, newPassword);

            // Then
            assertEquals("newPasswordHash", user.getPasswordHash());
            verify(emailTokenRepository).save(emailToken);
            assertNotNull(emailToken.getUsedAt());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideInvalidResetScenarios")
        @DisplayName("Invalid reset scenarios should throw appropriate exceptions")
        void invalidResetScenarios(String scenarioName, String token, String newPassword,
                                  Optional<EmailToken> returnedToken,
                                  Class<? extends Exception> expectedException) {
            // Given
            when(emailTokenRepository.findByToken(token)).thenReturn(returnedToken);

            // When/Then
            assertThrows(expectedException, () -> authService.resetPassword(token, newPassword));
        }

        static Stream<Arguments> provideInvalidResetScenarios() {
            String newPassword = "NewPassword123!";
            User user = new User();

            EmailToken expiredToken = new EmailToken(
                user, "expired-token", EmailToken.TokenType.RESET,
                LocalDateTime.now().minusMinutes(10)
            );

            EmailToken usedToken = new EmailToken(
                user, "used-token", EmailToken.TokenType.RESET,
                LocalDateTime.now().plusMinutes(10)
            );
            usedToken.setUsedAt(LocalDateTime.now());

            EmailToken wrongTypeToken = new EmailToken(
                user, "wrong-type-token", EmailToken.TokenType.VERIFICATION,
                LocalDateTime.now().plusMinutes(10)
            );

            return Stream.of(
                Arguments.of("Token not found", "invalid-token", newPassword, Optional.empty(), IllegalArgumentException.class),
                Arguments.of("Token expired", "expired-token", newPassword, Optional.of(expiredToken), IllegalStateException.class),
                Arguments.of("Token already used", "used-token", newPassword, Optional.of(usedToken), IllegalStateException.class),
                Arguments.of("Wrong token type", "wrong-type-token", newPassword, Optional.of(wrongTypeToken), IllegalStateException.class)
            );
        }
    }

    @Nested
    @DisplayName("Password Change")
    class PasswordChangeTests {

        @Test
        @DisplayName("Valid password change should update user's password")
        void validPasswordChangeShouldUpdatePassword() {
            // Given
            String email = validEmail;
            String oldPassword = "OldPassword123!";
            String newPassword = "NewPassword123!";

            User user = new User();
            user.setEmail(email);
            user.setPasswordHash("oldPasswordHash");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn("newPasswordHash");

            ChangePasswordDto changePasswordDto = new ChangePasswordDto(
                oldPassword, newPassword, newPassword
            );

            // When
            authService.changePassword(changePasswordDto);

            // Then
            assertEquals("newPasswordHash", user.getPasswordHash());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Incorrect old password should throw exception")
        void incorrectOldPasswordShouldThrowException() {
            // Given
            String email = validEmail;
            String wrongOldPassword = "WrongPassword123!";
            String newPassword = "NewPassword123!";

            User user = new User();
            user.setEmail(email);
            user.setPasswordHash("correctPasswordHash");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(wrongOldPassword, user.getPasswordHash())).thenReturn(false);

            ChangePasswordDto changePasswordDto = new ChangePasswordDto(
                wrongOldPassword, newPassword, newPassword
            );

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.changePassword(changePasswordDto));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("New password same as old should throw exception")
        void newPasswordSameAsOldShouldThrowException() {
            // Given
            String email = validEmail;
            String password = "Password123!";

            User user = new User();
            user.setEmail(email);
            user.setPasswordHash("passwordHash");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, user.getPasswordHash())).thenReturn(true);

            ChangePasswordDto changePasswordDto = new ChangePasswordDto(
                password, password, password
            );

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.changePassword(changePasswordDto));
        }

        @Test
        @DisplayName("Password confirmation mismatch should throw exception")
        void passwordConfirmationMismatchShouldThrowException() {
            // Given
            String email = validEmail;
            String oldPassword = "OldPassword123!";
            String newPassword = "NewPassword123!";
            String differentConfirmation = "DifferentPassword123!";

            User user = new User();
            user.setEmail(email);
            user.setPasswordHash("oldPasswordHash");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(true);

            ChangePasswordDto changePasswordDto = new ChangePasswordDto(
                oldPassword, newPassword, differentConfirmation
            );

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> authService.changePassword(changePasswordDto));
        }
    }
}

