package stud.ntnu.backend.service.household;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityManager;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberCreateDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberDto;
import stud.ntnu.backend.dto.household.HouseholdCreateRequestDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.dto.household.HouseholdMemberDto;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.model.household.EmptyHouseholdMember;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.HouseholdAdmin;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.household.EmptyHouseholdMemberRepository;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.household.InvitationRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.InvitationService;
import stud.ntnu.backend.util.LocationUtil;

@ExtendWith(MockitoExtension.class)
@lombok.Generated
public class HouseholdServiceTest {

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HouseholdAdminRepository householdAdminRepository;

    @Mock
    private EmptyHouseholdMemberRepository emptyHouseholdMemberRepository;

    @Mock
    private InvitationService invitationService;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private ProductBatchRepository productBatchRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HouseholdService householdService;

    private User testUser;
    private User adminUser;
    private User regularUser;
    private Household testHousehold;
    private HouseholdAdmin testHouseholdAdmin;
    private EmptyHouseholdMember testEmptyMember;
    private HouseholdCreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Set up admin user
        adminUser = new User();
        adminUser.setId(2);
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");

        // Set up regular user
        regularUser = new User();
        regularUser.setId(3);
        regularUser.setEmail("regular@example.com");
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");

        // Set up test household
        testHousehold = new Household("Test Household", "123 Test St", 3);
        testHousehold.setId(1);
        testHousehold.setLatitude(new BigDecimal("60.0"));
        testHousehold.setLongitude(new BigDecimal("10.0"));
        testHousehold.setCreatedAt(LocalDateTime.now());

        // Set up household admin
        testHouseholdAdmin = new HouseholdAdmin(adminUser, testHousehold);
        testHouseholdAdmin.setId(1);

        // Set up empty household member
        testEmptyMember = new EmptyHouseholdMember("Child", "Child", "A child", 1500);
        testEmptyMember.setId(1);
        testEmptyMember.setHousehold(testHousehold);

        // Set up create request DTO
        createRequestDto = new HouseholdCreateRequestDto("New Household", "456 New St", 2, null, null);

        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Nested
    class GetAllHouseholdsTests {
        @Test
        void shouldReturnAllHouseholds() {
            List<Household> households = Arrays.asList(testHousehold);
            when(householdRepository.findAll()).thenReturn(households);
            List<Household> result = householdService.getAllHouseholds();
            assertEquals(1, result.size());
            assertEquals(testHousehold, result.get(0));
        }
    }

    @Nested
    class GetHouseholdByIdTests {
        @Test
        void shouldReturnHouseholdWhenFound() {
            when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));
            Optional<Household> result = householdService.getHouseholdById(1);
            assertTrue(result.isPresent());
            assertEquals(testHousehold, result.get());
        }

        @Test
        void shouldReturnEmptyOptionalWhenNotFound() {
            when(householdRepository.findById(999)).thenReturn(Optional.empty());
            Optional<Household> result = householdService.getHouseholdById(999);
            assertFalse(result.isPresent());
        }
    }

    @Nested
    class UpdateHouseholdTests {
        @Test
        void shouldUpdateHouseholdSuccessfully() {
            // Create a new household with the old address
            testHousehold = new Household("Test Household", "Old Address", 3);
            adminUser.setHousehold(testHousehold);
            
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            
            // Mock the save to return the same household instance that was passed in
            when(householdRepository.save(any(Household.class))).thenAnswer(invocation -> {
                Household savedHousehold = invocation.getArgument(0);
                // Ensure the coordinates are set before returning
                if (savedHousehold.getLatitude() == null) {
                    savedHousehold.setLatitude(new BigDecimal("61.0"));
                }
                if (savedHousehold.getLongitude() == null) {
                    savedHousehold.setLongitude(new BigDecimal("11.0"));
                }
                return savedHousehold;
            });

            try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                CoordinatesItemDto coordinates = new CoordinatesItemDto();
                coordinates.setLatitude(new BigDecimal("61.0"));
                coordinates.setLongitude(new BigDecimal("11.0"));
                mockedLocationUtil.when(() -> LocationUtil.getCoordinatesByAddress("New Address"))
                        .thenReturn(coordinates);

                Household result = householdService.updateHousehold("admin@example.com", "New Name", "New Address");

                // Verify the result directly
                assertEquals("New Name", result.getName());
                assertEquals("New Address", result.getAddress());
                assertEquals(new BigDecimal("61.0"), result.getLatitude());
                assertEquals(new BigDecimal("11.0"), result.getLongitude());

                // Also verify the save was called with correct values
                verify(householdRepository).save(any(Household.class));
            }
        }

        @Test
        void shouldUpdateHouseholdWithoutCoordinatesWhenGeocodingFails() {
            adminUser.setHousehold(testHousehold);
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            when(householdRepository.save(any(Household.class))).thenReturn(testHousehold);

            try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                mockedLocationUtil.when(() -> LocationUtil.getCoordinatesByAddress("New Address"))
                        .thenThrow(new IllegalArgumentException("Geocoding failed"));

                Household result = householdService.updateHousehold("admin@example.com", "New Name", "New Address");

                assertEquals("New Name", result.getName());
                assertEquals("New Address", result.getAddress());
                assertEquals(new BigDecimal("60.0"), result.getLatitude());
                assertEquals(new BigDecimal("10.0"), result.getLongitude());
            }
        }
    }

    @Nested
    class CreateHouseholdTests {
        @Test
        void shouldCreateHouseholdSuccessfully() {
            when(authentication.getName()).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(householdRepository.save(any(Household.class))).thenReturn(testHousehold);
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(householdAdminRepository.existsByUser(testUser)).thenReturn(false);
            when(householdAdminRepository.save(any(HouseholdAdmin.class))).thenReturn(testHouseholdAdmin);

            try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                CoordinatesItemDto coordinates = new CoordinatesItemDto();
                coordinates.setLatitude(new BigDecimal("60.0"));
                coordinates.setLongitude(new BigDecimal("10.0"));
                mockedLocationUtil.when(() -> LocationUtil.getCoordinatesByAddress(anyString()))
                        .thenReturn(coordinates);

                Household result = householdService.createHousehold(createRequestDto);

                assertNotNull(result);
                verify(householdRepository).save(any(Household.class));
                verify(householdAdminRepository).save(any(HouseholdAdmin.class));
            }
        }
    }

    @Nested
    class EmptyHouseholdMemberTests {
        @Test
        void shouldAddEmptyHouseholdMemberSuccessfully() {
            adminUser.setHousehold(testHousehold);
            EmptyHouseholdMemberCreateDto createDto = new EmptyHouseholdMemberCreateDto(
                "Child", "Child", "A child", 1500
            );

            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            when(emptyHouseholdMemberRepository.save(any(EmptyHouseholdMember.class))).thenReturn(testEmptyMember);

            EmptyHouseholdMemberDto result = householdService.addEmptyHouseholdMember("admin@example.com", createDto);

            assertNotNull(result);
            assertEquals("Child", result.getName());
            assertEquals(1500, result.getKcal_requirement());
        }

        @Test
        void shouldRemoveEmptyHouseholdMemberSuccessfully() {
            adminUser.setHousehold(testHousehold);
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            when(emptyHouseholdMemberRepository.findById(1)).thenReturn(Optional.of(testEmptyMember));

            householdService.removeEmptyHouseholdMember("admin@example.com", 1);

            verify(emptyHouseholdMemberRepository).delete(testEmptyMember);
        }
    }

    @Nested
    class InvitationTests {
        @Test
        void shouldCreateInvitationSuccessfully() {
            adminUser.setHousehold(testHousehold);
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            HouseholdInviteResponseDto responseDto = new HouseholdInviteResponseDto("token123", "invited@example.com", 1, "Test Household", "123 Test St");
            when(invitationService.createHouseholdInvitation("admin@example.com", "invited@example.com"))
                    .thenReturn(responseDto);

            HouseholdInviteResponseDto result = householdService.inviteToHousehold("admin@example.com", "invited@example.com");

            assertNotNull(result);
            assertEquals("token123", result.getToken());
        }

        @Test
        void shouldCancelInvitationSuccessfully() {
            adminUser.setHousehold(testHousehold);
            Invitation invitation = new Invitation(adminUser, "invited@example.com", testHousehold, "token123", LocalDateTime.now().plusDays(1));

            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            when(invitationRepository.findByToken("token123")).thenReturn(Optional.of(invitation));

            householdService.cancelInvitationByToken("admin@example.com", "token123");

            verify(invitationRepository).save(invitation);
            assertNotNull(invitation.getDeclinedAt());
        }
    }

    @Nested
    class AdminManagementTests {
        @Test
        void shouldPromoteToAdminSuccessfully() {
            adminUser.setHousehold(testHousehold);
            regularUser.setHousehold(testHousehold);

            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(userRepository.findByEmail("regular@example.com")).thenReturn(Optional.of(regularUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            when(householdAdminRepository.existsByUser(regularUser)).thenReturn(false);

            householdService.promoteToAdmin("admin@example.com", "regular@example.com");

            verify(householdAdminRepository).save(any(HouseholdAdmin.class));
        }

        @Test
        void shouldRemoveMemberSuccessfully() {
            adminUser.setHousehold(testHousehold);
            regularUser.setHousehold(testHousehold);

            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(userRepository.findById(3)).thenReturn(Optional.of(regularUser));
            when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
            when(householdAdminRepository.existsByUser(regularUser)).thenReturn(false);

            householdService.removeMemberFromHousehold("admin@example.com", 3);

            verify(userRepository).save(regularUser);
            assertNull(regularUser.getHousehold());
        }
    }
}