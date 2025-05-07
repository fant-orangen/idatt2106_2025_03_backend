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
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.InvitationService;
import stud.ntnu.backend.util.LocationUtil;

@ExtendWith(MockitoExtension.class)
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
        testEmptyMember = new EmptyHouseholdMember("Child", "Child", "A child");
        testEmptyMember.setId(1);
        testEmptyMember.setHousehold(testHousehold);
        testEmptyMember.setKcalRequirement(1500);

        // Set up create request DTO
        createRequestDto = new HouseholdCreateRequestDto(
                "New Household",
                "456 New St",
                2,
                null,
                null
        );

        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Nested
    class GetAllHouseholdsTests {
        @Nested
        class Positive {
            @Test
            void shouldReturnAllHouseholds() {
                // Arrange
                List<Household> households = Arrays.asList(testHousehold);
                when(householdRepository.findAll()).thenReturn(households);

                // Act
                List<Household> result = householdService.getAllHouseholds();

                // Assert
                assertEquals(1, result.size());
                assertEquals(testHousehold, result.get(0));
                verify(householdRepository).findAll();
            }
        }
    }

    @Nested
    class GetHouseholdByIdTests {
        @Nested
        class Positive {
            @Test
            void shouldReturnHouseholdWhenFound() {
                // Arrange
                when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));

                // Act
                Optional<Household> result = householdService.getHouseholdById(1);

                // Assert
                assertTrue(result.isPresent());
                assertEquals(testHousehold, result.get());
                verify(householdRepository).findById(1);
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldReturnEmptyOptionalWhenNotFound() {
                // Arrange
                when(householdRepository.findById(999)).thenReturn(Optional.empty());

                // Act
                Optional<Household> result = householdService.getHouseholdById(999);

                // Assert
                assertFalse(result.isPresent());
                verify(householdRepository).findById(999);
            }
        }
    }

    @Nested
    class SaveHouseholdTests {
        @Nested
        class Positive {
            @Test
            void shouldSaveHousehold() {
                // Arrange
                when(householdRepository.save(testHousehold)).thenReturn(testHousehold);

                // Act
                Household result = householdService.saveHousehold(testHousehold);

                // Assert
                assertEquals(testHousehold, result);
                verify(householdRepository).save(testHousehold);
            }
        }
    }

    @Nested
    class DeleteHouseholdTests {
        @Nested
        class Positive {
            @Test
            void shouldDeleteHousehold() {
                // Arrange
                doNothing().when(householdRepository).deleteById(1);

                // Act
                householdService.deleteHousehold(1);

                // Assert
                verify(householdRepository).deleteById(1);
            }
        }
    }

    @Nested
    class DeleteCurrentHouseholdTests {
        @Nested
        class Positive {
            @Test
            void shouldDeleteCurrentHouseholdWhenUserIsAdmin() {
                // Arrange
                adminUser.setHousehold(testHousehold);
                List<User> householdUsers = Arrays.asList(adminUser, regularUser);
                regularUser.setHousehold(testHousehold);

                when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
                when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
                when(userRepository.findByHousehold(testHousehold)).thenReturn(householdUsers);
                when(householdAdminRepository.findByHousehold(testHousehold)).thenReturn(Arrays.asList(testHouseholdAdmin));
                when(emptyHouseholdMemberRepository.findByHousehold(testHousehold)).thenReturn(Arrays.asList(testEmptyMember));

                // Act
                householdService.deleteCurrentHousehold("admin@example.com");

                // Assert
                verify(userRepository).findByEmail("admin@example.com");
                verify(householdAdminRepository).existsByUser(adminUser);
                verify(userRepository).findByHousehold(testHousehold);
                verify(userRepository, times(2)).save(any(User.class));
                verify(householdAdminRepository).findByHousehold(testHousehold);
                verify(householdAdminRepository).deleteAll(anyList());
                verify(emptyHouseholdMemberRepository).findByHousehold(testHousehold);
                verify(emptyHouseholdMemberRepository).deleteAll(anyList());
                verify(householdRepository).delete(testHousehold);
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenUserNotFound() {
                // Arrange
                when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.deleteCurrentHousehold("nonexistent@example.com")
                );
                assertEquals("User not found", exception.getMessage());
                verify(userRepository).findByEmail("nonexistent@example.com");
                verify(householdRepository, never()).delete(any(Household.class));
            }

            @Test
            void shouldThrowExceptionWhenUserDoesNotHaveHousehold() {
                // Arrange
                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.deleteCurrentHousehold("test@example.com")
                );
                assertEquals("User doesn't have a household", exception.getMessage());
                verify(userRepository).findByEmail("test@example.com");
                verify(householdRepository, never()).delete(any(Household.class));
            }

            @Test
            void shouldThrowExceptionWhenUserIsNotAdmin() {
                // Arrange
                regularUser.setHousehold(testHousehold);
                when(userRepository.findByEmail("regular@example.com")).thenReturn(Optional.of(regularUser));
                when(householdAdminRepository.existsByUser(regularUser)).thenReturn(false);

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.deleteCurrentHousehold("regular@example.com")
                );
                assertEquals("Only household admins can delete households", exception.getMessage());
                verify(userRepository).findByEmail("regular@example.com");
                verify(householdAdminRepository).existsByUser(regularUser);
                verify(householdRepository, never()).delete(any(Household.class));
            }
        }
    }

    @Nested
    class CreateHouseholdTests {
        @Nested
        class Positive {
            @Test
            void shouldCreateHouseholdSuccessfully() {
                // Arrange
                when(authentication.getName()).thenReturn("test@example.com");
                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
                when(householdRepository.save(any(Household.class))).thenAnswer(invocation -> {
                    Household savedHousehold = invocation.getArgument(0);
                    savedHousehold.setId(1);
                    return savedHousehold;
                });
                when(userRepository.save(testUser)).thenReturn(testUser);
                when(householdAdminRepository.existsByUser(testUser)).thenReturn(false);
                when(householdAdminRepository.save(any(HouseholdAdmin.class))).thenReturn(testHouseholdAdmin);

                // Mock the LocationUtil static method
                try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                    CoordinatesItemDto coordinates = new CoordinatesItemDto();
                    coordinates.setLatitude(new BigDecimal("60.0"));
                    coordinates.setLongitude(new BigDecimal("10.0"));
                    mockedLocationUtil.when(() -> LocationUtil.getCoordinatesByAddress("456 New St"))
                            .thenReturn(coordinates);

                    // Act
                    Household result = householdService.createHousehold(createRequestDto);

                    // Assert
                    assertNotNull(result);
                    assertEquals(1, result.getId());
                    assertEquals("New Household", result.getName());
                    assertEquals("456 New St", result.getAddress());
                    assertEquals(2, result.getPopulationCount());
                    assertEquals(new BigDecimal("60.0"), result.getLatitude());
                    assertEquals(new BigDecimal("10.0"), result.getLongitude());
                    verify(householdRepository).save(any(Household.class));
                    verify(userRepository).save(testUser);
                    verify(householdAdminRepository).save(any(HouseholdAdmin.class));
                }
            }

            @Test
            void shouldCreateHouseholdWithoutCoordinatesWhenGeocodingFails() {
                // Arrange
                when(authentication.getName()).thenReturn("test@example.com");
                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
                when(householdRepository.save(any(Household.class))).thenAnswer(invocation -> {
                    Household savedHousehold = invocation.getArgument(0);
                    savedHousehold.setId(1);
                    return savedHousehold;
                });
                when(userRepository.save(testUser)).thenReturn(testUser);
                when(householdAdminRepository.existsByUser(testUser)).thenReturn(false);
                when(householdAdminRepository.save(any(HouseholdAdmin.class))).thenReturn(testHouseholdAdmin);

                // Mock the LocationUtil static method to throw an exception
                try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                    mockedLocationUtil.when(() -> LocationUtil.getCoordinatesByAddress("456 New St"))
                            .thenThrow(new IllegalArgumentException("Geocoding failed"));

                    // Act
                    Household result = householdService.createHousehold(createRequestDto);

                    // Assert
                    assertNotNull(result);
                    assertEquals(1, result.getId());
                    assertEquals("New Household", result.getName());
                    assertEquals("456 New St", result.getAddress());
                    assertEquals(2, result.getPopulationCount());
                    assertNull(result.getLatitude());
                    assertNull(result.getLongitude());
                    verify(householdRepository).save(any(Household.class));
                    verify(userRepository).save(testUser);
                    verify(householdAdminRepository).save(any(HouseholdAdmin.class));
                }
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenUserNotFound() {
                // Arrange
                when(authentication.getName()).thenReturn("nonexistent@example.com");
                when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.createHousehold(createRequestDto)
                );
                assertEquals("User not found", exception.getMessage());
                verify(userRepository).findByEmail("nonexistent@example.com");
                verify(householdRepository, never()).save(any(Household.class));
            }

            @Test
            void shouldThrowExceptionWhenUserAlreadyHasHousehold() {
                // Arrange
                testUser.setHousehold(testHousehold);
                when(authentication.getName()).thenReturn("test@example.com");
                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.createHousehold(createRequestDto)
                );
                assertEquals("User already has a household", exception.getMessage());
                verify(userRepository).findByEmail("test@example.com");
                verify(householdRepository, never()).save(any(Household.class));
            }

            @Test
            void shouldThrowExceptionWhenUserIsAlreadyAdmin() {
                // Arrange
                when(authentication.getName()).thenReturn("test@example.com");
                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
                when(householdRepository.save(any(Household.class))).thenAnswer(invocation -> {
                    Household savedHousehold = invocation.getArgument(0);
                    savedHousehold.setId(1);
                    return savedHousehold;
                });
                when(userRepository.save(testUser)).thenReturn(testUser);
                when(householdAdminRepository.existsByUser(testUser)).thenReturn(true);

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.createHousehold(createRequestDto)
                );
                assertEquals("User is already a household admin", exception.getMessage());
                verify(userRepository).findByEmail("test@example.com");
                verify(householdRepository).save(any(Household.class));
                verify(userRepository).save(testUser);
                verify(householdAdminRepository).existsByUser(testUser);
                verify(householdAdminRepository, never()).save(any(HouseholdAdmin.class));
            }
        }
    }

    @Nested
    class SwitchHouseholdTests {
        @Nested
        class Positive {
            @Test
            void shouldSwitchHouseholdSuccessfully() {
                // Arrange
                Household targetHousehold = new Household("Target Household", "789 Target St", 2);
                targetHousehold.setId(2);

                testUser.setHousehold(testHousehold);
                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
                when(householdRepository.findById(2)).thenReturn(Optional.of(targetHousehold));
                when(householdAdminRepository.existsByUser(testUser)).thenReturn(false);
                when(userRepository.save(testUser)).thenReturn(testUser);

                // Act
                Household result = householdService.switchHousehold("test@example.com", 2);

                // Assert
                assertEquals(targetHousehold, result);
                assertEquals(targetHousehold, testUser.getHousehold());
                verify(userRepository).findByEmail("test@example.com");
                verify(householdRepository).findById(2);
                verify(householdAdminRepository).existsByUser(testUser);
                verify(userRepository).save(testUser);
            }
/**
            @Test
            void shouldSwitchHouseholdWhenUserIsAdminButNotLastAdmin() {
                // Arrange
                Household targetHousehold = new Household("Target Household", "789 Target St", 2);
                targetHousehold.setId(2);

                adminUser.setHousehold(testHousehold);
                User otherAdmin = new User();
                otherAdmin.setId(4);
                otherAdmin.setEmail("otheradmin@example.com");
                otherAdmin.setHousehold(testHousehold);
                testHousehold.setUsers(Arrays.asList(adminUser, otherAdmin));

                HouseholdAdmin adminRecord = new HouseholdAdmin(adminUser, testHousehold);
                adminRecord.setId(1);

                when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
                when(householdRepository.findById(2)).thenReturn(Optional.of(targetHousehold));
                when(householdAdminRepository.existsByUser(adminUser)).thenReturn(true);
                when(householdAdminRepository.existsByUser(otherAdmin)).thenReturn(true);
                when(householdAdminRepository.findByUser(adminUser)).thenReturn(Optional.of(adminRecord));
                when(userRepository.save(adminUser)).thenReturn(adminUser);

                // Act
                Household result = householdService.switchHousehold("admin@example.com", 2);

                // Assert
                assertEquals(targetHousehold, result);
                assertEquals(targetHousehold, adminUser.getHousehold());
                verify(userRepository).findByEmail("admin@example.com");
                verify(householdRepository).findById(2);
                verify(householdAdminRepository).existsByUser(adminUser);
                verify(householdAdminRepository).findByUser(adminUser);
                verify(householdAdminRepository).delete(adminRecord);
                verify(userRepository).save(adminUser);
            }
            */
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenUserNotFound() {
                // Arrange
                when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(
                        IllegalStateException.class,
                        () -> householdService.switchHousehold("nonexistent@example.com", 2)
                );
                assertEquals("User not found", exception.getMessage());
                verify(userRepository).findByEmail("nonexistent@example.com");
                verify(householdRepository, never()).findById(anyInt());
            }
        }
    }
}