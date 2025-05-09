package stud.ntnu.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import stud.ntnu.backend.dto.user.UserProfileDto;
import stud.ntnu.backend.dto.user.UserUpdateDto;
import stud.ntnu.backend.dto.user.UserPreferencesDto;
import stud.ntnu.backend.dto.user.UserHistoryDto;
import stud.ntnu.backend.dto.user.UserBasicInfoDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.SafetyConfirmation;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.user.EmailTokenRepository;
import stud.ntnu.backend.repository.user.SafetyConfirmationRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailTokenRepository emailTokenRepository;

    @Mock
    private SafetyConfirmationRepository safetyConfirmationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class BasicOperationsTests {
        @Test
        void shouldGetAllUsers() {
            // Arrange
            User user1 = new User();
            user1.setId(1);
            User user2 = new User();
            user2.setId(2);
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.findAll()).thenReturn(expectedUsers);

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertEquals(expectedUsers, result);
            verify(userRepository).findAll();
        }

        @Test
        void shouldGetUserById() {
            // Arrange
            Integer userId = 1;
            User expectedUser = new User();
            expectedUser.setId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

            // Act
            Optional<User> result = userService.getUserById(userId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(expectedUser, result.get());
            verify(userRepository).findById(userId);
        }

        @Test
        void shouldGetUserByEmail() {
            // Arrange
            String email = "test@example.com";
            User expectedUser = new User();
            expectedUser.setEmail(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

            // Act
            Optional<User> result = userService.getUserByEmail(email);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(expectedUser, result.get());
            verify(userRepository).findByEmail(email);
        }

        @Test
        void shouldGetUserIdByEmail() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setId(1);
            user.setEmail(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act
            Integer result = userService.getUserIdByEmail(email);

            // Assert
            assertEquals(1, result);
            verify(userRepository).findByEmail(email);
        }

        @Test
        void shouldThrowExceptionWhenGettingUserIdByEmailForNonExistentUser() {
            // Arrange
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> userService.getUserIdByEmail(email));
            verify(userRepository).findByEmail(email);
        }

        @Test
        void shouldSaveUser() {
            // Arrange
            User user = new User();
            user.setEmail("test@example.com");

            when(userRepository.save(user)).thenReturn(user);

            // Act
            User result = userService.saveUser(user);

            // Assert
            assertEquals(user, result);
            verify(userRepository).save(user);
        }

        @Test
        void shouldDeleteUser() {
            // Arrange
            Integer userId = 1;

            // Act
            userService.deleteUser(userId);

            // Assert
            verify(userRepository).deleteById(userId);
        }
    }

    @Nested
    class ProfileManagementTests {
        @Test
        void shouldGetUserProfile() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setId(1);
            user.setEmail(email);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setHomeAddress("123 Main St");
            user.setHomeLatitude(new BigDecimal("63.4305"));
            user.setHomeLongitude(new BigDecimal("10.3951"));
            user.setLocationSharingEnabled(true);
            user.setEmailVerified(true);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act
            UserProfileDto result = userService.getUserProfile(email);

            // Assert
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
            assertEquals(user.getFirstName(), result.getFirstName());
            assertEquals(user.getLastName(), result.getLastName());
            assertEquals(user.getHomeAddress(), result.getHomeAddress());
            assertEquals(user.getHomeLatitude(), result.getHomeLatitude());
            assertEquals(user.getHomeLongitude(), result.getHomeLongitude());
            assertEquals(user.getLocationSharingEnabled(), result.getLocationSharingEnabled());
            assertEquals(user.getEmailVerified(), result.getEmailVerified());
            verify(userRepository).findByEmail(email);
        }

        @Test
        void shouldThrowExceptionWhenGettingProfileForNonExistentUser() {
            // Arrange
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> userService.getUserProfile(email));
            verify(userRepository).findByEmail(email);
        }

        @Test
        void shouldUpdateUserProfile() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setId(1);
            user.setEmail(email);
            user.setFirstName("John");
            user.setLastName("Doe");

            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setFirstName("Jane");
            updateDto.setLastName("Smith");
            updateDto.setHomeAddress("456 Oak St");
            updateDto.setHomeLatitude(new BigDecimal("63.4306"));
            updateDto.setHomeLongitude(new BigDecimal("10.3952"));

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);

            // Act
            UserProfileDto result = userService.updateUserProfile(email, updateDto);

            // Assert
            assertNotNull(result);
            assertEquals(updateDto.getFirstName(), result.getFirstName());
            assertEquals(updateDto.getLastName(), result.getLastName());
            assertEquals(updateDto.getHomeAddress(), result.getHomeAddress());
            assertEquals(updateDto.getHomeLatitude(), result.getHomeLatitude());
            assertEquals(updateDto.getHomeLongitude(), result.getHomeLongitude());
            verify(userRepository).findByEmail(email);
            verify(userRepository).save(user);
        }
    }

    @Nested
    class PreferencesTests {
        @Test
        void shouldUpdateUserPreferences() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setId(1);
            user.setEmail(email);
            user.setLocationSharingEnabled(false);

            UserPreferencesDto preferencesDto = new UserPreferencesDto();
            preferencesDto.setLocationSharingEnabled(true);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);

            // Act
            UserProfileDto result = userService.updateUserPreferences(email, preferencesDto);

            // Assert
            assertNotNull(result);
            assertTrue(result.getLocationSharingEnabled());
            verify(userRepository).findByEmail(email);
            verify(userRepository).save(user);
        }
    }

    @Nested
    class HistoryTests {
        @Test
        void shouldGetUserHistory() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setId(1);
            user.setEmail(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act
            UserHistoryDto result = userService.getUserHistory(email);

            // Assert
            assertNotNull(result);
            assertTrue(result.getCompletedActivities().isEmpty());
            assertTrue(result.getReflections().isEmpty());
            verify(userRepository).findByEmail(email);
        }
    }

    @Nested
    class BasicInfoTests {
        @Test
        void shouldGetUserBasicInfo() {
            // Arrange
            Integer userId = 1;
            User user = new User();
            user.setId(userId);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("test@example.com");
            user.setEmailVerified(true);

            Household household = new Household();
            household.setId(1);
            household.setName("Test Household");
            user.setHousehold(household);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // Act
            UserBasicInfoDto result = userService.getUserBasicInfo(userId);

            // Assert
            assertNotNull(result);
            assertEquals(user.getFirstName(), result.getFirstName());
            assertEquals(user.getLastName(), result.getLastName());
            assertEquals(user.getEmail(), result.getEmail());
            assertEquals(user.getHousehold().getName(), result.getHouseholdName());
            assertEquals(user.getEmailVerified(), result.getEmailVerified());
            verify(userRepository).findById(userId);
        }

        @Test
        void shouldThrowExceptionWhenGettingBasicInfoForNonExistentUser() {
            // Arrange
            Integer userId = 999;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> userService.getUserBasicInfo(userId));
            verify(userRepository).findById(userId);
        }
    }

    @Nested
    class SafetyTests {
        @Test
        void shouldReturnTrueWhenUserIsSafe() {
            // Arrange
            Integer userId = 1;
            User user = new User();
            user.setId(userId);

            SafetyConfirmation safetyConfirmation = new SafetyConfirmation();
            safetyConfirmation.setUser(user);
            safetyConfirmation.setIsSafe(true);
            safetyConfirmation.setSafeAt(LocalDateTime.now().minusHours(12)); // Less than 24 hours ago

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.getReferenceById(userId)).thenReturn(user);
            when(safetyConfirmationRepository.findByUser(user)).thenReturn(Optional.of(safetyConfirmation));

            // Act
            boolean result = userService.isSafe(userId);

            // Assert
            assertTrue(result);
            verify(userRepository).findById(userId);
            verify(safetyConfirmationRepository).findByUser(user);
        }

        @Test
        void shouldReturnFalseWhenUserIsNotSafe() {
            // Arrange
            Integer userId = 1;
            User user = new User();
            user.setId(userId);

            SafetyConfirmation safetyConfirmation = new SafetyConfirmation();
            safetyConfirmation.setUser(user);
            safetyConfirmation.setIsSafe(false);
            safetyConfirmation.setSafeAt(LocalDateTime.now().minusHours(12));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.getReferenceById(userId)).thenReturn(user);
            when(safetyConfirmationRepository.findByUser(user)).thenReturn(Optional.of(safetyConfirmation));

            // Act
            boolean result = userService.isSafe(userId);

            // Assert
            assertFalse(result);
            verify(userRepository).findById(userId);
            verify(safetyConfirmationRepository).findByUser(user);
        }

        @Test
        void shouldReturnFalseWhenSafetyConfirmationIsExpired() {
            // Arrange
            Integer userId = 1;
            User user = new User();
            user.setId(userId);

            SafetyConfirmation safetyConfirmation = new SafetyConfirmation();
            safetyConfirmation.setUser(user);
            safetyConfirmation.setIsSafe(true);
            safetyConfirmation.setSafeAt(LocalDateTime.now().minusHours(25)); // More than 24 hours ago

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.getReferenceById(userId)).thenReturn(user);
            when(safetyConfirmationRepository.findByUser(user)).thenReturn(Optional.of(safetyConfirmation));

            // Act
            boolean result = userService.isSafe(userId);

            // Assert
            assertFalse(result);
            verify(userRepository).findById(userId);
            verify(safetyConfirmationRepository).findByUser(user);
        }

        @Test
        void shouldThrowExceptionWhenCheckingSafetyForNonExistentUser() {
            // Arrange
            Integer userId = 999;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> userService.isSafe(userId));
            verify(userRepository).findById(userId);
            verify(safetyConfirmationRepository, never()).findByUser(any());
        }
    }
} 