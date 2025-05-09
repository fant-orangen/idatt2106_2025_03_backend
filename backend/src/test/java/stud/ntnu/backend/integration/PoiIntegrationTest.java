package stud.ntnu.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import stud.ntnu.backend.dto.poi.CreatePoiDto;
import stud.ntnu.backend.model.map.PointOfInterest;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.repository.map.PointOfInterestRepository;
import stud.ntnu.backend.repository.map.PoiTypeRepository;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.util.JwtUtil;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PoiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointOfInterestRepository poiRepository;

    @Autowired
    private PoiTypeRepository poiTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private PoiType testPoiType;

    @BeforeEach
    void setUp() {
        // Clear repositories
        poiRepository.deleteAll();
        poiTypeRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create test POI type
        testPoiType = new PoiType("Test Type");
        poiTypeRepository.save(testPoiType);

        // Create and save admin role
        Role adminRole = new Role("ADMIN");
        roleRepository.save(adminRole);

        // Create admin user
        User adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setPasswordHash(passwordEncoder.encode("password"));
        adminUser.setRole(adminRole);
        adminUser.setPhoneNumber("12345678");  // Required field
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmailVerified(true);
        adminUser.setPrivacyAccepted(true);
        adminUser.setIsUsing2FA(false);
        adminUser.setLocationSharingEnabled(false);
        userRepository.save(adminUser);

        // Generate JWT token for admin
        adminToken = "Bearer " + jwtUtil.generateToken(adminUser.getEmail());
    }

    @Test
    void createAndSearchPoi_ShouldWorkEndToEnd() throws Exception {
        // Create a POI
        CreatePoiDto createDto = new CreatePoiDto();
        createDto.setName("Test POI");
        createDto.setLatitude(new BigDecimal("63.4305"));
        createDto.setLongitude(new BigDecimal("10.3951"));
        createDto.setPoiTypeId(testPoiType.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/poi")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.name").value("Test POI"));

        // Search for the created POI
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/poi/search")
            .param("q", "Test")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "id,asc"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Test POI"));
    }

    @Test
    void createMultiplePoisAndTestPagination() throws Exception {
        // Create multiple POIs
        for (int i = 1; i <= 3; i++) {
            CreatePoiDto createDto = new CreatePoiDto();
            createDto.setName("Test POI " + i);
            createDto.setLatitude(new BigDecimal("63.4305"));
            createDto.setLongitude(new BigDecimal("10.3951"));
            createDto.setPoiTypeId(testPoiType.getId());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/poi")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // Test pagination with page size 2
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/poi/search")
            .param("q", "Test")
            .param("page", "0")
            .param("size", "2")
            .param("sort", "id,asc"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false));

        // Test second page
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/poi/search")
            .param("q", "Test")
            .param("page", "1")
            .param("size", "2")
            .param("sort", "id,asc"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void testPoiTypeFiltering() throws Exception {
        // Create POIs of different types
        PoiType type1 = new PoiType("Type 1");
        PoiType type2 = new PoiType("Type 2");
        poiTypeRepository.saveAll(List.of(type1, type2));

        // Create POIs
        for (int i = 1; i <= 2; i++) {
            CreatePoiDto createDto = new CreatePoiDto();
            createDto.setName("POI " + i);
            createDto.setLatitude(new BigDecimal("63.4305"));
            createDto.setLongitude(new BigDecimal("10.3951"));
            createDto.setPoiTypeId(i == 1 ? type1.getId() : type2.getId());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/poi")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // Test filtering by type
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/poi/type/" + type1.getId()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("POI 1"));
    }
} 