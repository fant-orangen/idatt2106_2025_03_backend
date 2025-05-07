package stud.ntnu.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class LoginTests {

        @Nested
        class Positive {

            @Test
            void shouldLoginSuccessfully() {
                String email = "test@example.com";
                String password = "password123";
                String recaptchaToken = "recaptcha-token";
                AuthRequestDto request = new AuthRequestDto(email, password, recaptchaToken);

                User user = new User();
                user.setEmail(email);
                user.setId(1);
                Role role = new Role();
                role.setId(1);
                role.setName("USER");
                user.setRole(role);
                user.setIsUsing2FA(false);

                Authentication auth = mock(Authentication.class);

                when(authenticationManager.authenticate(any())).thenReturn(auth);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(jwtUtil.generateToken(email)).thenReturn("jwt-token");

                AuthResponseDto response = authService.login(request);

                assertNotNull(response);
                assertEquals("jwt-token", response.getToken());
                assertEquals(email, response.getEmail());
            }

            @Test
            void shouldReturnResponseWithRequires2FAWhenUserHas2FAEnabled() {
                String email = "test@example.com";
                String password = "password123";
                String recaptchaToken = "recaptcha-token";
                AuthRequestDto request = new AuthRequestDto(email, password, recaptchaToken);

                User user = new User();
                user.setEmail(email);
                user.setId(1);
                Role role = new Role();
                role.setId(1);
                role.setName("USER");
                user.setRole(role);
                user.setIsUsing2FA(true); // 2FA is enabled

                Authentication auth = mock(Authentication.class);

                when(authenticationManager.authenticate(any())).thenReturn(auth);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

                AuthResponseDto response = authService.login(request);

                assertNotNull(response);
                assertNull(response.getToken()); // No token should be returned
                assertEquals(email, response.getEmail());
                assertTrue(response.getIsUsing2FA()); // 2FA should be indicated
            }
        }

        @Nested
        class Negative {

            @Test
            void shouldThrowBadCredentialsException() {
                String recaptchaToken = "recaptcha-token";
                AuthRequestDto request = new AuthRequestDto("wrong@example.com", "badpass", recaptchaToken);

                when(authenticationManager.authenticate(any()))
                        .thenThrow(new BadCredentialsException("Invalid credentials"));

                assertThrows(BadCredentialsException.class, () -> authService.login(request));
            }

            @Test
            void shouldThrowWhenUserNotFoundAfterAuthentication() {
                String email = "missing@example.com";
                String recaptchaToken = "recaptcha-token";
                AuthRequestDto request = new AuthRequestDto(email, "pass", recaptchaToken);

                when(authenticationManager.authenticate(any()))
                        .thenReturn(mock(Authentication.class));
                when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

                assertThrows(BadCredentialsException.class, () -> authService.login(request));
            }
        }
    }

    @Nested
    class RegisterTests {

        @Nested
        class Positive {

            @Test
            void shouldRegisterNewUserSuccessfully() {
                RegisterRequestDto request = new RegisterRequestDto();
                request.setEmail("new@example.com");
                request.setPassword("securePass");
                request.setFirstName("Alice");
                request.setLastName("Doe");
                request.setPhoneNumber("12345678");
                request.setPrivacyPolicyAccepted(true);

                when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
                when(passwordEncoder.encode(any())).thenReturn("hashed-password");
                when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                authService.register(request);

                verify(userRepository).save(any(User.class));
                verify(emailTokenRepository).save(any());
                verify(emailService).sendVerificationEmail(any(), anyString());
            }
        }

        @Nested
        class Negative {

            @Test
            void shouldThrowWhenEmailAlreadyExists() {
                RegisterRequestDto request = new RegisterRequestDto();
                request.setEmail("existing@example.com");

                when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

                assertThrows(IllegalArgumentException.class, () -> authService.register(request));
            }
        }
    }
    @Nested
    class VerifyEmailTests {
        @Nested
        class Positive {
            @Test
            void shouldVerifyEmailSuccessfully() {
                // Arrange
                String token = "verification-token";
                User user = new User();
                user.setId(1);
                user.setEmail("test@example.com");
                user.setEmailVerified(false);

                EmailToken emailToken = new EmailToken(user, token, EmailToken.TokenType.VERIFICATION, LocalDateTime.now().plusHours(24));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                authService.verifyEmail(token);

                // Assert
                assertTrue(user.getEmailVerified());
                verify(userRepository).save(user);
                verify(emailTokenRepository).save(emailToken);
                assertNotNull(emailToken.getUsedAt());
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowWhenTokenNotFound() {
                // Arrange
                String token = "invalid-token";
                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.verifyEmail(token));
            }

            @Test
            void shouldThrowWhenTokenAlreadyUsed() {
                // Arrange
                String token = "used-token";
                EmailToken emailToken = new EmailToken(new User(), token, EmailToken.TokenType.VERIFICATION, LocalDateTime.now().plusHours(24));
                emailToken.setUsedAt(LocalDateTime.now());

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> authService.verifyEmail(token));
            }

            @Test
            void shouldThrowWhenTokenExpired() {
                // Arrange
                String token = "expired-token";
                EmailToken emailToken = new EmailToken(new User(), token, EmailToken.TokenType.VERIFICATION, LocalDateTime.now().minusHours(1));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> authService.verifyEmail(token));
            }

            @Test
            void shouldThrowWhenTokenTypeInvalid() {
                // Arrange
                String token = "wrong-type-token";
                EmailToken emailToken = new EmailToken(new User(), token, EmailToken.TokenType.RESET, LocalDateTime.now().plusHours(24));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> authService.verifyEmail(token));
            }

            @Test
            void shouldThrowWhenTokenNotAssociatedWithUser() {
                // Arrange
                String token = "no-user-token";
                EmailToken emailToken = new EmailToken(null, token, EmailToken.TokenType.VERIFICATION, LocalDateTime.now().plusHours(24));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> authService.verifyEmail(token));
            }
        }
    }

    @Nested
    class TwoFactorAuthTests {
        @Nested
        class Positive {
            @Test
            void shouldVerify2FACodeSuccessfully() {
                // Arrange
                String email = "test@example.com";
                Integer code = 123456;

                User user = new User();
                user.setId(1);
                user.setEmail(email);
                Role role = new Role();
                role.setId(1);
                role.setName("USER");
                user.setRole(role);

                when(twoFactorCodeService.verifyCode(email, code)).thenReturn(true);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(jwtUtil.generateToken(email)).thenReturn("jwt-token");

                // Act
                AuthResponseDto response = authService.verify2FA(email, code);

                // Assert
                assertNotNull(response);
                assertEquals("jwt-token", response.getToken());
                assertEquals(email, response.getEmail());
            }

            @Test
            void shouldSend2FACodeSuccessfully() {
                // Arrange
                String email = "test@example.com";

                // Act
                authService.send2FACode(email);

                // Assert
                verify(twoFactorCodeService).sendVerificationCode(email);
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowWhenVerifyingInvalidCode() {
                // Arrange
                String email = "test@example.com";
                Integer code = 123456;

                when(twoFactorCodeService.verifyCode(email, code)).thenReturn(false);

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.verify2FA(email, code));
            }

            @Test
            void shouldThrowWhenUserNotFoundDuring2FAVerification() {
                // Arrange
                String email = "nonexistent@example.com";
                Integer code = 123456;

                when(twoFactorCodeService.verifyCode(email, code)).thenReturn(true);
                when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.verify2FA(email, code));
            }
        }
    }

    @Nested
    class ForgotPasswordTests {
        @Nested
        class Positive {
            @Test
            void shouldInitiatePasswordResetSuccessfully() {
                // Arrange
                String email = "test@example.com";
                User user = new User();
                user.setEmail(email);

                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(emailTokenRepository.save(any(EmailToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                authService.forgotPassword(email);

                // Assert
                verify(emailTokenRepository).save(any(EmailToken.class));
                verify(emailService).sendPasswordResetEmail(eq(user), anyString());
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowWhenUserNotFound() {
                // Arrange
                String email = "nonexistent@example.com";
                when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.forgotPassword(email));
            }
        }
    }

    @Nested
    class ResetPasswordTests {
        @Nested
        class Positive {
            @Test
            void testResetPassword_Success() {
                // Arrange
                String token = "reset-token";
                String newPassword = "NewPassword123!";
                User user = new User();
                user.setId(1);
                user.setEmail("test@example.com");
                user.setPasswordHash("oldPasswordHash");

                EmailToken emailToken = new EmailToken(user, token, EmailToken.TokenType.RESET, LocalDateTime.now().plusMinutes(10));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(passwordEncoder.encode(newPassword)).thenReturn("newPasswordHash");

                // Act
                authService.resetPassword(token, newPassword);

                // Assert
                assertEquals("newPasswordHash", user.getPasswordHash());
                verify(emailTokenRepository).save(emailToken);
                assertNotNull(emailToken.getUsedAt());
            }
        }

        @Nested
        class Negative {
            @Test
            void testResetPassword_InvalidToken() {
                // Arrange
                String token = "invalid-token";
                String newPassword = "NewPassword123!";

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.resetPassword(token, newPassword));
            }

            @Test
            void testResetPassword_ExpiredToken() {
                // Arrange
                String token = "expired-token";
                String newPassword = "NewPassword123!";

                EmailToken emailToken = new EmailToken(null, token, EmailToken.TokenType.RESET, LocalDateTime.now().minusMinutes(10));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> authService.resetPassword(token, newPassword));
            }

            @Test
            void testResetPassword_AlreadyUsedToken() {
                // Arrange
                String token = "used-token";
                String newPassword = "NewPassword123!";

                EmailToken emailToken = new EmailToken(null, token, EmailToken.TokenType.RESET, LocalDateTime.now().plusMinutes(10));
                emailToken.setUsedAt(LocalDateTime.now());

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class , () -> authService.resetPassword(token, newPassword));
            }

            @Test
            void testResetPassword_WrongTokenType() {
                // Arrange
                String token = "wrong-type-token";
                String newPassword = "NewPassword123!";

                EmailToken emailToken = new EmailToken(new User(), token, EmailToken.TokenType.VERIFICATION, LocalDateTime.now().plusMinutes(10));

                when(emailTokenRepository.findByToken(token)).thenReturn(Optional.of(emailToken));

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> authService.resetPassword(token, newPassword));
            }
        }
    }

    @Nested
    class ChangePasswordTests {
        @Nested
        class Positive {
            @Test
            void shouldChangePasswordSuccessfully() {
                // Arrange
                String email = "test@example.com";
                String oldPassword = "OldPassword123!";
                String newPassword = "NewPassword123!";
                String confirmNewPassword = "NewPassword123!";

                User user = new User();
                user.setEmail(email);
                user.setPasswordHash("oldPasswordHash");

                // Mock SecurityContext and Authentication
                SecurityContext securityContext = mock(SecurityContext.class);
                Authentication authentication = mock(Authentication.class);
                SecurityContextHolder.setContext(securityContext);

                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getName()).thenReturn(email);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
                when(passwordEncoder.encode(newPassword)).thenReturn("newPasswordHash");

                ChangePasswordDto changePasswordDto = new ChangePasswordDto(oldPassword, newPassword, confirmNewPassword);

                // Act
                authService.changePassword(changePasswordDto);

                // Assert
                assertEquals("newPasswordHash", user.getPasswordHash());
                verify(userRepository).save(user);
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowWhenOldPasswordDoesNotMatch() {
                // Arrange
                String email = "test@example.com";
                String oldPassword = "WrongPassword123!";
                String newPassword = "NewPassword123!";
                String confirmNewPassword = "NewPassword123!";

                User user = new User();
                user.setEmail(email);
                user.setPasswordHash("oldPasswordHash");

                // Mock SecurityContext and Authentication
                SecurityContext securityContext = mock(SecurityContext.class);
                Authentication authentication = mock(Authentication.class);
                SecurityContextHolder.setContext(securityContext);

                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getName()).thenReturn(email);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(false);

                ChangePasswordDto changePasswordDto = new ChangePasswordDto(oldPassword, newPassword, confirmNewPassword);

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.changePassword(changePasswordDto));
            }

            @Test
            void shouldThrowWhenNewPasswordSameAsOld() {
                // Arrange
                String email = "test@example.com";
                String oldPassword = "Password123!";
                String newPassword = "Password123!";
                String confirmNewPassword = "Password123!";

                User user = new User();
                user.setEmail(email);
                user.setPasswordHash("passwordHash");

                // Mock SecurityContext and Authentication
                SecurityContext securityContext = mock(SecurityContext.class);
                Authentication authentication = mock(Authentication.class);
                SecurityContextHolder.setContext(securityContext);

                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getName()).thenReturn(email);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(true);

                ChangePasswordDto changePasswordDto = new ChangePasswordDto(oldPassword, newPassword, confirmNewPassword);

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.changePassword(changePasswordDto));
            }

            @Test
            void shouldThrowWhenNewPasswordAndConfirmationDoNotMatch() {
                // Arrange
                String email = "test@example.com";
                String oldPassword = "OldPassword123!";
                String newPassword = "NewPassword123!";
                String confirmNewPassword = "DifferentPassword123!";

                User user = new User();
                user.setEmail(email);
                user.setPasswordHash("oldPasswordHash");

                // Mock SecurityContext and Authentication
                SecurityContext securityContext = mock(SecurityContext.class);
                Authentication authentication = mock(Authentication.class);
                SecurityContextHolder.setContext(securityContext);

                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getName()).thenReturn(email);
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(true);

                ChangePasswordDto changePasswordDto = new ChangePasswordDto(oldPassword, newPassword, confirmNewPassword);

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> authService.changePassword(changePasswordDto));
            }
        }
    }
}

