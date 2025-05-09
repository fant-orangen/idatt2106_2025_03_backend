package stud.ntnu.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import stud.ntnu.backend.dto.auth.AuthRequestDto;
import stud.ntnu.backend.dto.auth.AuthResponseDto;
import stud.ntnu.backend.dto.auth.RegisterRequestDto;
import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.util.JwtUtil;
import stud.ntnu.backend.service.user.RecaptchaService;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RecaptchaService recaptchaService;

    private String userToken;
    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Clear repositories
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create and save user role
        userRole = new Role("USER");
        roleRepository.save(userRole);

        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setRole(userRole);
        testUser.setPhoneNumber("12345678");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmailVerified(true);
        testUser.setPrivacyAccepted(true);
        testUser.setIsUsing2FA(false);
        testUser.setLocationSharingEnabled(false);
        userRepository.save(testUser);

        // Generate JWT token for user
        userToken = "Bearer " + jwtUtil.generateToken(testUser.getEmail());

        // Mock reCAPTCHA verification to always return true for testing
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(true);
    }
/**
    @Test
    void registerUser_ShouldCreateNewUser() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhoneNumber("87654321");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");
        registerRequest.setHomeAddress("Test Address");
        registerRequest.setHomeLatitude(new BigDecimal("63.4305"));
        registerRequest.setHomeLongitude(new BigDecimal("10.3951"));
        registerRequest.setPrivacyPolicyAccepted(true);
        registerRequest.setRecaptchaToken("test-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify user was created
        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setEmail("newuser@example.com");
        authRequest.setPassword("password123");
        authRequest.setRecaptchaToken("test-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.token").exists());
    }
 */

    @Test
    void loginUser_ShouldReturnToken() throws Exception {
        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
        authRequest.setRecaptchaToken("test-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.isUsing2FA").value(false));
    }

    @Test
    void loginUser_WithInvalidCredentials_ShouldReturnForbidden() throws Exception {
        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("wrongpassword");
        authRequest.setRecaptchaToken("test-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void loginUser_WithInvalidRecaptcha_ShouldReturnBadRequest() throws Exception {
        // Override the mock to return false for this test
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(false);

        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
        authRequest.setRecaptchaToken("invalid-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Reset the mock for other tests
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(true);
    }

    @Test
    void getCurrentUser_ShouldReturnUserProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
            .header("Authorization", userToken))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void getCurrentUser_WithoutToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me"))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void getCurrentUser_WithInvalidToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
            .header("Authorization", "Bearer invalid-token"))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUserProfile_ShouldUpdateUserDetails() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("Updated");
        updateDto.setLastName("Name");
        updateDto.setHomeAddress("New Address");
        updateDto.setHomeLatitude(new BigDecimal("63.4305"));
        updateDto.setHomeLongitude(new BigDecimal("10.3951"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/me")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("Name"))
            .andExpect(jsonPath("$.homeAddress").value("New Address"));
    }

    @Test
    void updateUserProfile_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Create an invalid update DTO with invalid data types
        String invalidJson = "{\"firstName\": 123, \"lastName\": true, \"homeLatitude\": \"not-a-number\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/me")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUserPreferences_ShouldUpdatePreferences() throws Exception {
        UserPreferencesDto preferencesDto = new UserPreferencesDto();
        preferencesDto.setLocationSharingEnabled(true);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/user/me/preferences")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.locationSharingEnabled").value(true));
    }

    @Test
    void updateUserPreferences_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Create an invalid preferences DTO with invalid data type
        String invalidJson = "{\"locationSharingEnabled\": \"not-a-boolean\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/user/me/preferences")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getUserHistory_ShouldReturnEmptyHistory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me/history")
            .header("Authorization", userToken))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.completedActivities").isArray())
            .andExpect(jsonPath("$.reflections").isArray());
    }

    @Test
    void getUserHistory_WithoutToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me/history"))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void getUserBasicInfo_ShouldReturnBasicInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/" + testUser.getId() + "/basic-info")
            .header("Authorization", userToken))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void getUserBasicInfo_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/999/basic-info")
            .header("Authorization", userToken))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }

    @Test
    void getUserBasicInfo_WithoutToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/13/basic-info"))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
} 