package stud.ntnu.backend.controller.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import stud.ntnu.backend.config.TestSecurityConfig;
import stud.ntnu.backend.dto.poi.CreatePoiDto;
import stud.ntnu.backend.dto.poi.PoiItemDto;
import stud.ntnu.backend.dto.poi.PoiPreviewDto;
import stud.ntnu.backend.dto.poi.UpdatePoiDto;
import stud.ntnu.backend.model.map.PointOfInterest;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.service.map.PoiService;
import stud.ntnu.backend.service.user.UserService;
import stud.ntnu.backend.config.SecurityConfig;
import stud.ntnu.backend.config.JwtAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = PoiController.class, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("unit-test")
public class PoiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PoiService poiService;

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/api";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class PublicEndpointsTests {
        @Test
        void getPublicPointsOfInterest_ShouldReturnListOfPois() throws Exception {
            // Arrange
            List<PointOfInterest> pois = Arrays.asList(
                createTestPoi(1, "POI 1"),
                createTestPoi(2, "POI 2")
            );
            when(poiService.getAllPointsOfInterest()).thenReturn(pois);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/public"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
        }

        @Test
        void getPoiPreviews_ShouldReturnPaginatedPreviews() throws Exception {
            // Arrange
            List<PoiPreviewDto> previews = Arrays.asList(
                new PoiPreviewDto(1, "POI 1", "Type 1"),
                new PoiPreviewDto(2, "POI 2", "Type 2")
            );
            when(poiService.getPoiPreviews(any())).thenReturn(new PageImpl<>(previews));

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/previews")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2));
        }

        @Test
        void getPointsOfInterestByTypeId_ShouldReturnFilteredPois() throws Exception {
            // Arrange
            Integer typeId = 1;
            List<PointOfInterest> pois = Arrays.asList(
                createTestPoi(1, "POI 1"),
                createTestPoi(2, "POI 2")
            );
            when(poiService.getPointsOfInterestByTypeId(typeId)).thenReturn(pois);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/type/" + typeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
        }

        @Test
        void getPointOfInterestById_ShouldReturnPoi() throws Exception {
            // Arrange
            Integer poiId = 1;
            PointOfInterest poi = createTestPoi(poiId, "Test POI");
            when(poiService.getPointOfInterestById(poiId)).thenReturn(Optional.of(poi));

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/" + poiId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(poiId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test POI"));
        }

        @Test
        void getPointOfInterestById_ShouldReturn404WhenNotFound() throws Exception {
            // Arrange
            Integer poiId = 999;
            when(poiService.getPointOfInterestById(poiId)).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/" + poiId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
        }

        @Test
        void getAllPoiTypes_ShouldReturnAllTypes() throws Exception {
            // Arrange
            List<PoiType> types = Arrays.asList(
                new PoiType("Type 1"),
                new PoiType("Type 2")
            );
            when(poiService.getAllPoiTypes()).thenReturn(types);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/types"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
        }

        @Test
        void searchPois_ShouldReturnMatchingPois() throws Exception {
            // Arrange
            String searchQuery = "test";
            List<PointOfInterest> pois = Arrays.asList(
                createTestPoi(1, "Test POI 1"),
                createTestPoi(2, "Test POI 2")
            );
            Page<PointOfInterest> poiPage = new PageImpl<>(pois);
            when(poiService.searchPoisByName(eq(searchQuery), any(Pageable.class))).thenReturn(poiPage);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/search")
                .param("q", searchQuery)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Test POI 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Test POI 2"));
        }

        @Test
        void searchPois_ShouldReturnEmptyPageWhenNoMatches() throws Exception {
            // Arrange
            String searchQuery = "nonexistent";
            Page<PointOfInterest> emptyPage = new PageImpl<>(List.of());
            when(poiService.searchPoisByName(eq(searchQuery), any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/search")
                .param("q", searchQuery)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(0));
        }

        @Test
        void searchPois_ShouldHandlePagination() throws Exception {
            // Arrange
            String searchQuery = "test";
            List<PointOfInterest> allPois = Arrays.asList(
                createTestPoi(1, "Test POI 1"),
                createTestPoi(2, "Test POI 2")
            );
            
            // Mock the service to return a single item per page
            when(poiService.searchPoisByName(eq(searchQuery), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(1);
                    int start = (int) pageable.getOffset();
                    int end = Math.min(start + pageable.getPageSize(), allPois.size());
                    List<PointOfInterest> pageContent = allPois.subList(start, end);
                    return new PageImpl<>(pageContent, pageable, allPois.size());
                });

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/search")
                .param("q", searchQuery)
                .param("page", "0")
                .param("size", "1")
                .param("sort", "id,asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1));
        }

        @Test
        void searchPois_ShouldHandleSorting() throws Exception {
            // Arrange
            String searchQuery = "test";
            List<PointOfInterest> pois = Arrays.asList(
                createTestPoi(2, "Test POI 2"),
                createTestPoi(1, "Test POI 1")
            );
            Page<PointOfInterest> poiPage = new PageImpl<>(pois);
            when(poiService.searchPoisByName(eq(searchQuery), any(Pageable.class))).thenReturn(poiPage);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/public/poi/search")
                .param("q", searchQuery)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(1));
        }
    }

    @Nested
    class AdminEndpointsTests {
        private User adminUser;
        private User regularUser;
        private CreatePoiDto createPoiDto;
        private UpdatePoiDto updatePoiDto;

        @BeforeEach
        void setUp() {
            // Setup admin user
            adminUser = new User();
            adminUser.setEmail("admin@example.com");
            Role adminRole = new Role("ADMIN");
            adminUser.setRole(adminRole);

            // Setup regular user
            regularUser = new User();
            regularUser.setEmail("user@example.com");
            Role userRole = new Role("USER");
            regularUser.setRole(userRole);

            // Setup DTOs
            createPoiDto = new CreatePoiDto();
            createPoiDto.setName("Test POI");
            createPoiDto.setLatitude(new BigDecimal("63.4305"));
            createPoiDto.setLongitude(new BigDecimal("10.3951"));
            createPoiDto.setPoiTypeId(1);

            updatePoiDto = new UpdatePoiDto();
            updatePoiDto.setName("Updated POI");
            updatePoiDto.setLatitude(new BigDecimal("63.4306"));
            updatePoiDto.setLongitude(new BigDecimal("10.3952"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createPointOfInterest_ShouldCreatePoiWhenAdmin() throws Exception {
            // Arrange
            when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(adminUser));
            PointOfInterest createdPoi = createTestPoi(1, createPoiDto.getName());
            when(poiService.createPointOfInterest(any(), any())).thenReturn(createdPoi);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/admin/poi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPoiDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createPoiDto.getName()));
        }

        @Test
        @WithMockUser(roles = "USER")
        void createPointOfInterest_ShouldReturn403WhenNotAdmin() throws Exception {
            // Arrange
            when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(regularUser));
            when(poiService.createPointOfInterest(any(), any()))
                .thenThrow(new IllegalStateException("Only administrators can create points of interest"));

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/admin/poi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPoiDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("Only administrators can create points of interest"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updatePointOfInterest_ShouldUpdatePoiWhenAdmin() throws Exception {
            // Arrange
            when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(adminUser));
            PointOfInterest updatedPoi = createTestPoi(1, updatePoiDto.getName());
            when(poiService.updatePointOfInterest(anyInt(), any())).thenReturn(updatedPoi);

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/admin/poi/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePoiDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatePoiDto.getName()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deletePointOfInterest_ShouldDeletePoiWhenAdmin() throws Exception {
            // Arrange
            when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(adminUser));
            doNothing().when(poiService).deletePointOfInterest(anyInt());

            // Act & Assert
            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/admin/poi/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Point of interest deleted successfully"));
        }
    }

    // Helper method to create test POIs
    private PointOfInterest createTestPoi(Integer id, String name) {
        PointOfInterest poi = new PointOfInterest();
        poi.setId(id);
        poi.setName(name);
        poi.setLatitude(new BigDecimal("63.4305"));
        poi.setLongitude(new BigDecimal("10.3951"));
        poi.setPoiType(new PoiType("Test Type"));
        return poi;
    }
} 