package stud.ntnu.backend.controller.crisis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver; // Import this
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventChangeDto;
import stud.ntnu.backend.dto.map.CrisisEventPreviewDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.crisis.CrisisEventService;
import stud.ntnu.backend.service.user.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CrisisEventControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CrisisEventService crisisEventService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CrisisEventController crisisEventController;

    // Test data
    private Principal adminPrincipal;
    private Principal regularUserPrincipal;
    private Principal nonExistentUserPrincipal;

    private User adminUser;
    private User regularUser;
    private CrisisEvent testCrisisEvent;
    private CreateCrisisEventDto createCrisisEventDto;
    private UpdateCrisisEventDto updateCrisisEventDto;
    private CrisisEventPreviewDto crisisEventPreviewDto;

    private final String BASE_URL = "/api";
    private final String ADMIN_ONLY_MESSAGE = "Only administrators can create crisis events";
    private final String USER_NOT_FOUND_MESSAGE = "User not found";

    @BeforeEach
    void setUp() {
        // Configure MockMvc with the PageableHandlerMethodArgumentResolver
        mockMvc = MockMvcBuilders.standaloneSetup(crisisEventController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For handling Java 8 date/time types

        // Setup Principals
        adminPrincipal = new UsernamePasswordAuthenticationToken("admin@example.com", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        regularUserPrincipal = new UsernamePasswordAuthenticationToken("user@example.com", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        nonExistentUserPrincipal = new UsernamePasswordAuthenticationToken("unknown@example.com", null,
                Collections.emptyList());

        // Setup User accounts
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(adminRole);
        adminUser.setId(1);

        Role userRole = new Role();
        userRole.setName("USER");
        regularUser = new User();
        regularUser.setEmail("user@example.com");
        regularUser.setRole(userRole);
        regularUser.setId(2);

        // Setup test crisis event
        testCrisisEvent = new CrisisEvent(
                "Test Crisis",
                new BigDecimal("60.0"),
                new BigDecimal("10.0"),
                new BigDecimal("5.0"),
                LocalDateTime.now(),
                adminUser
        );
        testCrisisEvent.setId(1);
        testCrisisEvent.setDescription("Test description");
        testCrisisEvent.setSeverity(CrisisEvent.Severity.yellow);

        // Setup DTOs
        createCrisisEventDto = new CreateCrisisEventDto(
                new BigDecimal("60.0"),
                new BigDecimal("10.0"),
                "Test Address",
                new BigDecimal("5.0"),
                CrisisEvent.Severity.yellow,
                "Test description",
                "Test Crisis",
                LocalDateTime.now(),
                null
        );

        updateCrisisEventDto = new UpdateCrisisEventDto(
                "Updated Crisis",
                "Updated description",
                CrisisEvent.Severity.red,
                new BigDecimal("61.0"),
                new BigDecimal("11.0"),
                new BigDecimal("6.0"),
                null
        );

        crisisEventPreviewDto = new CrisisEventPreviewDto(
                1,
                "Test Crisis",
                CrisisEvent.Severity.yellow,
                LocalDateTime.now()
        );
    }

    // Helper methods to mock user service responses
    private void mockUserAsAdmin(Principal principal, boolean isAdmin) {
        if (principal == null || principal.getName() == null) return;

        if (isAdmin) {
            when(userService.getUserByEmail(principal.getName())).thenReturn(Optional.of(adminUser));
        } else {
            when(userService.getUserByEmail(principal.getName())).thenReturn(Optional.of(regularUser));
        }
    }

    private void mockUserAsNonExistent(Principal principal) {
        if (principal == null || principal.getName() == null) return;
        when(userService.getUserByEmail(principal.getName())).thenReturn(Optional.empty());
    }

    @Nested
    @DisplayName("Create Crisis Event Tests")
    class CreateCrisisEventTests {

        @Test
        @DisplayName("Admin can create crisis event")
        void adminCanCreateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            when(crisisEventService.createCrisisEvent(any(CreateCrisisEventDto.class), any(User.class)))
                    .thenReturn(testCrisisEvent);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/admin/crisis-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCrisisEventDto))
                            .principal(adminPrincipal))
                    .andExpect(status().isOk());

            verify(crisisEventService).createCrisisEvent(any(CreateCrisisEventDto.class), any(User.class));
        }

        @Test
        @DisplayName("Non-admin cannot create crisis event")
        void nonAdminCannotCreateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsAdmin(regularUserPrincipal, false);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/admin/crisis-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCrisisEventDto))
                            .principal(regularUserPrincipal))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(ADMIN_ONLY_MESSAGE));

            verify(crisisEventService, never()).createCrisisEvent(any(CreateCrisisEventDto.class), any(User.class));
        }

        @Test
        @DisplayName("Non-existent user cannot create crisis event")
        void nonExistentUserCannotCreateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsNonExistent(nonExistentUserPrincipal);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/admin/crisis-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCrisisEventDto))
                            .principal(nonExistentUserPrincipal))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(USER_NOT_FOUND_MESSAGE));

            verify(crisisEventService, never()).createCrisisEvent(any(CreateCrisisEventDto.class), any(User.class));
        }

        @Test
        @DisplayName("Service exception results in bad request")
        void serviceExceptionResultsInBadRequest() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            when(crisisEventService.createCrisisEvent(any(CreateCrisisEventDto.class), any(User.class)))
                    .thenThrow(new IllegalArgumentException("Invalid data"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/admin/crisis-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCrisisEventDto))
                            .principal(adminPrincipal))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid data"));
        }
    }

    @Nested
    @DisplayName("Update Crisis Event Tests")
    class UpdateCrisisEventTests {

        @Test
        @DisplayName("Admin can update crisis event")
        void adminCanUpdateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            when(crisisEventService.updateCrisisEvent(anyInt(), any(UpdateCrisisEventDto.class)))
                    .thenReturn(testCrisisEvent);

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateCrisisEventDto))
                            .principal(adminPrincipal))
                    .andExpect(status().isOk());

            verify(crisisEventService).updateCrisisEvent(eq(1), any(UpdateCrisisEventDto.class));
        }

        @Test
        @DisplayName("Non-admin cannot update crisis event")
        void nonAdminCannotUpdateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsAdmin(regularUserPrincipal, false);

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateCrisisEventDto))
                            .principal(regularUserPrincipal))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Only administrators can update crisis events"));

            verify(crisisEventService, never()).updateCrisisEvent(anyInt(), any(UpdateCrisisEventDto.class));
        }

        @Test
        @DisplayName("Update non-existent crisis event returns not found")
        void updateNonExistentCrisisEventReturnsNotFound() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            when(crisisEventService.updateCrisisEvent(anyInt(), any(UpdateCrisisEventDto.class)))
                    .thenReturn(null);

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateCrisisEventDto))
                            .principal(adminPrincipal))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Service exception results in bad request")
        void serviceExceptionResultsInBadRequest() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            when(crisisEventService.updateCrisisEvent(anyInt(), any(UpdateCrisisEventDto.class)))
                    .thenThrow(new IllegalArgumentException("Invalid data"));

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateCrisisEventDto))
                            .principal(adminPrincipal))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid data"));
        }
    }

    @Nested
    @DisplayName("Get Crisis Event Tests")
    class GetCrisisEventTests {

        @Test
        @DisplayName("Get all crisis event previews")
        void getAllCrisisEventPreviews() throws Exception {
            // Arrange
            Page<CrisisEventPreviewDto> previewPage = new PageImpl<>(
                    Collections.singletonList(crisisEventPreviewDto),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.getAllCrisisEventPreviews(any(Pageable.class)))
                    .thenReturn(previewPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/all/previews")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(crisisEventPreviewDto.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(crisisEventPreviewDto.getName()))
                    .andExpect(jsonPath("$.content[0].severity").value(crisisEventPreviewDto.getSeverity().toString()));

            verify(crisisEventService).getAllCrisisEventPreviews(any(Pageable.class));
        }

        @Test
        @DisplayName("Get inactive crisis event previews")
        void getInactiveCrisisEventPreviews() throws Exception {
            // Arrange
            Page<CrisisEventPreviewDto> previewPage = new PageImpl<>(
                    Collections.singletonList(crisisEventPreviewDto),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.getInactiveCrisisEventPreviews(any(Pageable.class)))
                    .thenReturn(previewPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/inactive/previews")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(crisisEventPreviewDto.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(crisisEventPreviewDto.getName()))
                    .andExpect(jsonPath("$.content[0].severity").value(crisisEventPreviewDto.getSeverity().toString()));

            verify(crisisEventService).getInactiveCrisisEventPreviews(any(Pageable.class));
        }

        @Test
        @DisplayName("Get all crisis events")
        void getAllCrisisEvents() throws Exception {
            // Arrange
            Page<CrisisEvent> eventsPage = new PageImpl<>(
                    Collections.singletonList(testCrisisEvent),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.getAllCrisisEvents(any(Pageable.class)))
                    .thenReturn(eventsPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/all")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(testCrisisEvent.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(testCrisisEvent.getName()))
                    .andExpect(jsonPath("$.content[0].severity").value(testCrisisEvent.getSeverity().toString()));

            verify(crisisEventService).getAllCrisisEvents(any(Pageable.class));
        }
        /**
         @Test
         @DisplayName("Get crisis event by ID")
         void getCrisisEventById() throws Exception {
         // Arrange
         when(crisisEventService.getCrisisEventDetailsById(anyInt()))
         .thenReturn(Optional.of(testCrisisEvent));

         // Act & Assert
         mockMvc.perform(get(BASE_URL + "/public/crisis-events/1"))
         .andExpect(status().isOk());

         verify(crisisEventService).getCrisisEventDetailsById(1);
         }
         */

        @Test
        @DisplayName("Get non-existent crisis event returns not found")
        void getNonExistentCrisisEventReturnsNotFound() throws Exception {
            // Arrange
            when(crisisEventService.getCrisisEventDetailsById(anyInt()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/999"))
                    .andExpect(status().isNotFound());

            verify(crisisEventService).getCrisisEventDetailsById(999);
        }

        @Test
        @DisplayName("Service exception results in bad request")
        void serviceExceptionResultsInBadRequest() throws Exception {
            // Arrange
            when(crisisEventService.getCrisisEventDetailsById(anyInt()))
                    .thenThrow(new IllegalArgumentException("Invalid ID"));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid ID"));
        }
    }

    @Nested
    @DisplayName("Deactivate Crisis Event Tests")
    class DeactivateCrisisEventTests {

        @Test
        @DisplayName("Admin can deactivate crisis event")
        void adminCanDeactivateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            doNothing().when(crisisEventService).deactivateCrisisEvent(anyInt());

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/deactivate/1")
                            .principal(adminPrincipal))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Crisis event deactivated"));

            verify(crisisEventService).deactivateCrisisEvent(1);
        }

        @Test
        @DisplayName("Non-admin cannot deactivate crisis event")
        void nonAdminCannotDeactivateCrisisEvent() throws Exception {
            // Arrange
            mockUserAsAdmin(regularUserPrincipal, false);

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/deactivate/1")
                            .principal(regularUserPrincipal))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Only administrators can deactivate crisis events"));

            verify(crisisEventService, never()).deactivateCrisisEvent(anyInt());
        }

        @Test
        @DisplayName("Service exception results in bad request")
        void serviceExceptionResultsInBadRequest() throws Exception {
            // Arrange
            mockUserAsAdmin(adminPrincipal, true);
            doThrow(new IllegalArgumentException("Invalid ID")).when(crisisEventService).deactivateCrisisEvent(anyInt());

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/admin/crisis-events/deactivate/1")
                            .principal(adminPrincipal))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid ID"));
        }
    }

    @Nested
    @DisplayName("Get Crisis Event Changes Tests")
    class GetCrisisEventChangesTests {

        @Test
        @DisplayName("Get crisis event changes")
        void getCrisisEventChanges() throws Exception {
            // Arrange
            CrisisEventChangeDto changeDto = new CrisisEventChangeDto(
                    1, 1, "level_change", "green", "yellow", 1, "Admin User", LocalDateTime.now(), LocalDateTime.now()
            );
            Page<CrisisEventChangeDto> changesPage = new PageImpl<>(
                    Collections.singletonList(changeDto),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.getCrisisEventChanges(anyInt(), any(Pageable.class)))
                    .thenReturn(changesPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/1/changes")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(regularUserPrincipal)) // Assuming regular user can access this
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(changeDto.getId()))
                    .andExpect(jsonPath("$.content[0].crisisEventId").value(changeDto.getCrisisEventId()))
                    .andExpect(jsonPath("$.content[0].changeType").value(changeDto.getChangeType()));

            verify(crisisEventService).getCrisisEventChanges(eq(1), any(Pageable.class));
        }

        @Test
        @DisplayName("Get changes for non-existent crisis event returns not found")
        void getChangesForNonExistentCrisisEventReturnsNotFound() throws Exception {
            // Arrange
            when(crisisEventService.getCrisisEventChanges(anyInt(), any(Pageable.class)))
                    .thenThrow(new IllegalStateException("Crisis event not found")); // Assuming this is the exception type

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/999/changes")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(regularUserPrincipal))
                    .andExpect(status().isNotFound()); // Or whatever status your controller/exception handler returns
        }

        @Test
        @DisplayName("Service exception results in bad request")
        void serviceExceptionResultsInBadRequest() throws Exception {
            // Arrange
            when(crisisEventService.getCrisisEventChanges(anyInt(), any(Pageable.class)))
                    .thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/1/changes")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(regularUserPrincipal))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Service error"));
        }
    }

    @Nested
    @DisplayName("Get Crisis Events Affecting User Tests")
    class GetCrisisEventsAffectingUserTests {

        @Test
        @DisplayName("Get crisis events affecting current user")
        void getCrisisEventsAffectingCurrentUser() throws Exception {
            // Arrange
            mockUserAsAdmin(regularUserPrincipal, false); // regularUser is set up
            Page<CrisisEvent> eventsPage = new PageImpl<>(
                    Collections.singletonList(testCrisisEvent),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.getCrisisEventsAffectingUser(any(User.class), any(Pageable.class)))
                    .thenReturn(eventsPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/user/crisis-events/current-user")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(regularUserPrincipal))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(testCrisisEvent.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(testCrisisEvent.getName()));

            verify(crisisEventService).getCrisisEventsAffectingUser(eq(regularUser), any(Pageable.class));
        }

        @Test
        @DisplayName("Get all crisis event previews affecting current user")
        void getAllCrisisEventPreviewsAffectingUser() throws Exception {
            // Arrange
            mockUserAsAdmin(regularUserPrincipal, false); // regularUser is set up
            Page<CrisisEventPreviewDto> previewPage = new PageImpl<>(
                    Collections.singletonList(crisisEventPreviewDto),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.getCrisisEventPreviewsAffectingUserSortedBySeverity(any(User.class), any(Pageable.class)))
                    .thenReturn(previewPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/user/crisis-events/all/current-user")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(regularUserPrincipal))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(crisisEventPreviewDto.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(crisisEventPreviewDto.getName()));

            verify(crisisEventService).getCrisisEventPreviewsAffectingUserSortedBySeverity(eq(regularUser), any(Pageable.class));
        }

        @Test
        @DisplayName("User not found results in bad request")
        void userNotFoundResultsInBadRequest() throws Exception {
            // Arrange
            mockUserAsNonExistent(nonExistentUserPrincipal);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/user/crisis-events/current-user")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(nonExistentUserPrincipal))
                    .andExpect(status().isBadRequest()); // As per your controller logic for user not found

            verify(crisisEventService, never()).getCrisisEventsAffectingUser(any(User.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Service exception results in bad request")
        void serviceExceptionResultsInBadRequest() throws Exception {
            // Arrange
            mockUserAsAdmin(regularUserPrincipal, false); // regularUser is set up
            when(crisisEventService.getCrisisEventsAffectingUser(any(User.class), any(Pageable.class)))
                    .thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/user/crisis-events/current-user")
                            .param("page", "0")
                            .param("size", "10")
                            .principal(regularUserPrincipal))
                    .andExpect(status().isBadRequest()); // Assuming global exception handler returns bad request
        }
    }

    @Nested
    @DisplayName("Search Crisis Events Tests")
    class SearchCrisisEventsTests {

        @Test
        @DisplayName("Search crisis events by name")
        void searchCrisisEventsByName() throws Exception {
            // Arrange
            Page<CrisisEvent> eventsPage = new PageImpl<>(
                    Collections.singletonList(testCrisisEvent),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.searchCrisisEvents(anyString(), anyBoolean(), any(Pageable.class)))
                    .thenReturn(eventsPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/search")
                            .param("nameSearch", "Test")
                            .param("isActive", "true")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(testCrisisEvent.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(testCrisisEvent.getName()));

            verify(crisisEventService).searchCrisisEvents(eq("Test"), eq(true), any(Pageable.class));
        }

        @Test
        @DisplayName("Search with default parameters")
        void searchWithDefaultParameters() throws Exception {
            // Arrange
            Page<CrisisEvent> eventsPage = new PageImpl<>(
                    Collections.singletonList(testCrisisEvent),
                    PageRequest.of(0, 10),
                    1
            );
            when(crisisEventService.searchCrisisEvents(isNull(), eq(true), any(Pageable.class))) // Expect null for nameSearch if not provided
                    .thenReturn(eventsPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/public/crisis-events/search")
                            .param("page", "0")
                            .param("size", "10"))
                    // isActive defaults to true in controller, nameSearch to null
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(testCrisisEvent.getId()));

            // Verify that the service was called with null nameSearch and true isActive (default)
            verify(crisisEventService).searchCrisisEvents(isNull(), eq(true), any(Pageable.class));
        }
    }
}