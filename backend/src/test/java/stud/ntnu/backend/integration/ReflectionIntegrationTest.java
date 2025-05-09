package stud.ntnu.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import stud.ntnu.backend.dto.user.CreateReflectionDto;
import stud.ntnu.backend.dto.user.UpdateReflectionDto;
import stud.ntnu.backend.dto.user.ReflectionResponseDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.util.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReflectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private CrisisEventRepository crisisEventRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private User testUser;
    private Role userRole;
    private Household testHousehold;
    private CrisisEvent testCrisisEvent;

    @BeforeEach
    void setUp() {
        // Clear repositories
        userRepository.deleteAll();
        roleRepository.deleteAll();
        householdRepository.deleteAll();
        crisisEventRepository.deleteAll();

        // Create and save user role
        userRole = new Role("USER");
        roleRepository.save(userRole);

        // Create test household
        testHousehold = new Household();
        testHousehold.setName("Test Household");
        testHousehold.setAddress("Test Address");
        testHousehold.setLatitude(new BigDecimal("63.4305"));
        testHousehold.setLongitude(new BigDecimal("10.3951"));
        testHousehold.setPopulationCount(1);
        householdRepository.save(testHousehold);

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
        testUser.setHousehold(testHousehold);
        userRepository.save(testUser);

        // Create test crisis event
        testCrisisEvent = new CrisisEvent();
        testCrisisEvent.setName("Test Crisis");
        testCrisisEvent.setDescription("Test crisis description");
        testCrisisEvent.setActive(true);
        testCrisisEvent.setCreatedByUser(testUser);
        testCrisisEvent.setEpicenterLatitude(new BigDecimal("63.4305"));
        testCrisisEvent.setEpicenterLongitude(new BigDecimal("10.3951"));
        testCrisisEvent.setRadius(new BigDecimal("1000"));
        testCrisisEvent.setSeverity(CrisisEvent.Severity.green);
        testCrisisEvent.setStartTime(LocalDateTime.now());
        testCrisisEvent.setUpdatedAt(LocalDateTime.now());
        crisisEventRepository.save(testCrisisEvent);

        // Generate JWT token for user
        userToken = "Bearer " + jwtUtil.generateToken(testUser.getEmail());
    }

    @Test
    void createReflection_WithValidData_ShouldCreateReflection() throws Exception {
        CreateReflectionDto createDto = new CreateReflectionDto();
        createDto.setContent("Test reflection content");
        createDto.setShared(true);
        createDto.setCrisisEventId(testCrisisEvent.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content").value("Test reflection content"))
            .andExpect(jsonPath("$.shared").value(true))
            .andExpect(jsonPath("$.crisisEventId").value(testCrisisEvent.getId()))
            .andExpect(jsonPath("$.userFirstName").value("Test"))
            .andExpect(jsonPath("$.userLastName").value("User"));
    }

    @Test
    void createReflection_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateReflectionDto createDto = new CreateReflectionDto();
        // Missing required content field

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getMyReflections_ShouldReturnUserReflections() throws Exception {
        // First create a reflection
        CreateReflectionDto createDto = new CreateReflectionDto();
        createDto.setContent("Test reflection content");
        createDto.setShared(true);
        createDto.setCrisisEventId(testCrisisEvent.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // Then get reflections
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/reflections/my")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].content").value("Test reflection content"));
    }

    @Test
    void updateReflection_WithValidData_ShouldUpdateReflection() throws Exception {
        // First create a reflection
        CreateReflectionDto createDto = new CreateReflectionDto();
        createDto.setContent("Original content");
        createDto.setShared(true);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ReflectionResponseDto createdReflection = objectMapper.readValue(response, ReflectionResponseDto.class);

        // Then update it
        UpdateReflectionDto updateDto = new UpdateReflectionDto();
        updateDto.setContent("Updated content");
        updateDto.setShared(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/reflections/" + createdReflection.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content").value("Updated content"))
            .andExpect(jsonPath("$.shared").value(false));
    }

    @Test
    void deleteReflection_ShouldSoftDeleteReflection() throws Exception {
        // First create a reflection
        CreateReflectionDto createDto = new CreateReflectionDto();
        createDto.setContent("Test reflection content");
        createDto.setShared(true);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ReflectionResponseDto createdReflection = objectMapper.readValue(response, ReflectionResponseDto.class);

        // Then delete it
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/reflections/" + createdReflection.getId())
            .header("Authorization", userToken))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify it's not returned in getMyReflections
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/reflections/my")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getSharedReflections_ShouldReturnSharedReflections() throws Exception {
        // Create another user in the same household
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setPasswordHash(passwordEncoder.encode("password123"));
        otherUser.setRole(userRole);
        otherUser.setPhoneNumber("87654321");
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setEmailVerified(true);
        otherUser.setPrivacyAccepted(true);
        otherUser.setIsUsing2FA(false);
        otherUser.setLocationSharingEnabled(false);
        otherUser.setHousehold(testHousehold);
        userRepository.save(otherUser);

        // Generate token for other user
        String otherUserToken = "Bearer " + jwtUtil.generateToken(otherUser.getEmail());

        // Create a shared reflection with the other user
        CreateReflectionDto createDto = new CreateReflectionDto();
        createDto.setContent("Shared reflection content");
        createDto.setShared(true);
        createDto.setCrisisEventId(testCrisisEvent.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", otherUserToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // Then get shared reflections as the test user
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/reflections/shared")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].content").value("Shared reflection content"))
            .andExpect(jsonPath("$.content[0].userFirstName").value("Other"))
            .andExpect(jsonPath("$.content[0].userLastName").value("User"));
    }
/**
    @Test
    void getHouseholdReflections_ShouldReturnHouseholdReflections() throws Exception {
        // First create a shared reflection
        CreateReflectionDto createDto = new CreateReflectionDto();
        createDto.setContent("Household reflection content");
        createDto.setShared(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reflections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // Then get household reflections
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/reflections/household")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].content").value("Household reflection content"));
    }
    */
} 