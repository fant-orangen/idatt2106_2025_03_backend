package stud.ntnu.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.AuthResponseDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
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
}