// src/test/java/stud/ntnu/backend/e2e/AuthE2ETest.java
package stud.ntnu.backend.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.repository.user.EmailTokenRepository;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.AuthService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.security.user.name=test",
    "spring.security.user.password=test",
    "spring.security.user.roles=USER"
})
public class AuthE2ETest {
    private static final String VALID_PASSWORD = "1!Password";
    private static final String INVALID_PASSWORD = "pas";

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailTokenRepository emailTokenRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    private Role userRole;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Clean up and set up test data
        emailTokenRepository.deleteAll();
        notificationPreferenceRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create test role
        userRole = new Role();
        userRole.setName("USER");
        userRole = roleRepository.save(userRole);
    }

    @Test
    void testRegisterWithValidCredentials() {
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "alice@example.com",
                VALID_PASSWORD,
                "recaptchaToken",
                "Alice",
                "Smith",
                "1234567890",
                "123 Test St",
                null,
                null,
                true
        );
        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200);
    }
    @Test
    void testRegisterWithInvalidCredentials() {
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "bob@example.com",
                INVALID_PASSWORD,
                "recaptchaToken",
                "Bob",
                "Smith",
                "1234567890",
                "123 Test St",
                null,
                null,
                true
        );
        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403);
    }
/**
    @Test
    void testLoginWithValidCredentials() {
        // Create test user
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "alice1@example.com",
                VALID_PASSWORD,
                "recaptchaToken",
                "Alice1",
                "Smith",
                "1234567890",
                "123 Test St",
                null,
                null,
                true
        );
        
        // Ensure the role exists and is properly saved
        assertNotNull(userRole, "User role should not be null");
        assertNotNull(userRole.getId(), "User role ID should not be null");
        
        // Register the user
        authService.register(registerRequest);
        
        // Login with the registered user
        AuthRequestDto authRequest = new AuthRequestDto("alice1@example.com", VALID_PASSWORD, "null");
        given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("email", equalTo("alice1@example.com"));
    }
 */


    @Test
    void testLoginWithInvalidCredentials() {
        AuthRequestDto authRequest = new AuthRequestDto("alice1@example.com", INVALID_PASSWORD, "null");
        given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(403);
    }
}