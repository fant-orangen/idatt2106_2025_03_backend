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
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.AuthService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.security.user.name=test",
    "spring.security.user.password=test",
    "spring.security.user.roles=USER"
})
public class AuthE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Clean up and set up test data
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create test role
        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        // Create test user
        RegisterRequestDto registerRequest = new RegisterRequestDto(
            "alice1@example.com",
            "password",
            "recaptchaToken",
            "Alice1",
            "Smith",
            "1234567890",
            "123 Test St",
            null,
            null,
            true
        );
        authService.register(registerRequest);
    }

    @Test
    void testLoginWithValidCredentials() {
        AuthRequestDto authRequest = new AuthRequestDto("alice1@example.com", "password", "null");
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
}