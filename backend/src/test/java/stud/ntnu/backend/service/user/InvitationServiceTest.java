package stud.ntnu.backend.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.household.InvitationRepository;
import stud.ntnu.backend.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InvitationService invitationService;

    // Test fixtures
    private User inviter;
    private User invitee;
    private Household household;
    private Invitation invitation;
    private String token;

    @BeforeEach
    void setUp() {
        // Set up inviter
        inviter = new User();
        inviter.setId(1);
        inviter.setEmail("inviter@example.com");
        inviter.setFirstName("Inviter");
        inviter.setLastName("User");

        // Set up invitee
        invitee = new User();
        invitee.setId(2);
        invitee.setEmail("invitee@example.com");
        invitee.setFirstName("Invitee");
        invitee.setLastName("User");

        // Set up household
        household = new Household("Test Household", "123 Test St", 3);
        household.setId(1);
        inviter.setHousehold(household);

        // Set up token
        token = "test-token-123";

        // Set up invitation
        invitation = new Invitation(inviter, invitee.getEmail(), household, token, LocalDateTime.now().plusHours(24));
        invitation.setId(1);
    }

    @Nested
    @DisplayName("Create Household Invitation Tests")
    class CreateHouseholdInvitationTests {
        @Nested
        @DisplayName("Positive Tests")
        class Positive {
            @Test
            @DisplayName("Should create household invitation successfully")
            void shouldCreateHouseholdInvitationSuccessfully() {
                // Arrange
                when(userRepository.findByEmail(eq(inviter.getEmail()))).thenReturn(Optional.of(inviter));
                when(userRepository.findByEmail(eq(invitee.getEmail()))).thenReturn(Optional.of(invitee));
                when(invitationRepository.hasPendingInvitationForEmail(
                        eq(household),
                        eq(invitee.getEmail()),
                        any(LocalDateTime.class))
                ).thenReturn(false);

                when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

                when(notificationService.createNotification(
                        eq(invitee),
                        eq(Notification.PreferenceType.system),
                        eq(Notification.TargetType.invitation),
                        eq(invitation.getId()),
                        anyString()
                )).thenReturn(new Notification());

                // Act
                HouseholdInviteResponseDto result = invitationService.createHouseholdInvitation(
                        inviter.getEmail(), invitee.getEmail());

                // Assert
                assertNotNull(result);
                assertEquals(invitee.getEmail(), result.getInvitedEmail());
                assertEquals(household.getId(), result.getHouseholdId());
                assertEquals(household.getName(), result.getHouseholdName());
                assertNotNull(result.getExpiresAt());

                // Verify interactions
                verify(userRepository).findByEmail(eq(inviter.getEmail()));
                verify(userRepository).findByEmail(eq(invitee.getEmail()));
                verify(invitationRepository).hasPendingInvitationForEmail(
                        eq(household), eq(invitee.getEmail()), any(LocalDateTime.class));
                verify(invitationRepository).save(any(Invitation.class));
                verify(notificationService).createNotification(
                        eq(invitee),
                        eq(Notification.PreferenceType.system),
                        eq(Notification.TargetType.invitation),
                        eq(invitation.getId()),
                        anyString()
                );
                verify(notificationService).sendNotification(any(Notification.class));
            }
        }

        @Nested
        @DisplayName("Negative Tests")
        class Negative {
            @Test
            @DisplayName("Should throw exception when inviter not found")
            void shouldThrowExceptionWhenInviterNotFound() {
                // Arrange
                when(userRepository.findByEmail(inviter.getEmail())).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.createHouseholdInvitation(inviter.getEmail(), invitee.getEmail()));

                assertEquals("Inviter not found", exception.getMessage());
                verify(userRepository).findByEmail(inviter.getEmail());
                verify(userRepository, never()).findByEmail(invitee.getEmail());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when invitee not found")
            void shouldThrowExceptionWhenInviteeNotFound() {
                // Arrange
                when(userRepository.findByEmail(inviter.getEmail())).thenReturn(Optional.of(inviter));
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.createHouseholdInvitation(inviter.getEmail(), invitee.getEmail()));

                assertEquals("Invitee not found", exception.getMessage());
                verify(userRepository).findByEmail(inviter.getEmail());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when inviter tries to invite themselves")
            void shouldThrowExceptionWhenInviterInvitesThemselves() {
                // Arrange
                String sameEmail = "same@example.com";
                User inviter = new User(); // Mocked inviter object
                when(userRepository.findByEmail(sameEmail)).thenReturn(Optional.of(inviter));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.createHouseholdInvitation(sameEmail, sameEmail));

                assertEquals("You cannot invite yourself to your own household", exception.getMessage());
                verify(userRepository, times(1)).findByEmail(sameEmail); // Verify it was called once
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when inviter doesn't have a household")
            void shouldThrowExceptionWhenInviterDoesntHaveHousehold() {
                // Arrange
                User inviterWithoutHousehold = new User();
                inviterWithoutHousehold.setId(3);
                inviterWithoutHousehold.setEmail("inviter-no-household@example.com");
                inviterWithoutHousehold.setHousehold(null); // No household

                when(userRepository.findByEmail(inviterWithoutHousehold.getEmail())).thenReturn(Optional.of(inviterWithoutHousehold));
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.of(invitee));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.createHouseholdInvitation(inviterWithoutHousehold.getEmail(), invitee.getEmail()));

                assertEquals("Inviter doesn't have a household", exception.getMessage());
                verify(userRepository).findByEmail(inviterWithoutHousehold.getEmail());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when invitee already has a household")
            void shouldThrowExceptionWhenInviteeAlreadyHasHousehold() {
                // Arrange
                User inviteeWithHousehold = new User();
                inviteeWithHousehold.setId(4);
                inviteeWithHousehold.setEmail("invitee-with-household@example.com");
                inviteeWithHousehold.setHousehold(new Household("Another Household", "456 Another St", 2));

                when(userRepository.findByEmail(inviter.getEmail())).thenReturn(Optional.of(inviter));
                when(userRepository.findByEmail(inviteeWithHousehold.getEmail())).thenReturn(Optional.of(inviteeWithHousehold));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.createHouseholdInvitation(inviter.getEmail(), inviteeWithHousehold.getEmail()));

                assertEquals("User already belongs to a household and cannot be invited", exception.getMessage());
                verify(userRepository).findByEmail(inviter.getEmail());
                verify(userRepository).findByEmail(inviteeWithHousehold.getEmail());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }
        }
    }

    @Nested
    @DisplayName("Accept Invitation Tests")
    class AcceptInvitationTests {
        @Nested
        @DisplayName("Positive Tests")
        class Positive {
            @Test
            @DisplayName("Should accept invitation successfully")
            void shouldAcceptInvitationSuccessfully() {
                // Arrange
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.of(invitee));
                when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
                when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);
                when(userRepository.save(any(User.class))).thenReturn(invitee);
                when(notificationService.createNotification(
                        eq(inviter),
                        eq(Notification.PreferenceType.system),
                        eq(Notification.TargetType.invitation),
                        eq(invitation.getId()),
                        anyString()
                )).thenReturn(new Notification());

                // Act
                Household result = invitationService.acceptInvitation(invitee.getEmail(), token);

                // Assert
                assertNotNull(result);
                assertEquals(household.getId(), result.getId());
                assertEquals(household.getName(), result.getName());

                // Verify interactions
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository).findByToken(token);
                verify(invitationRepository).save(invitation);
                verify(userRepository).save(invitee);
                verify(notificationService).createNotification(
                        eq(inviter),
                        eq(Notification.PreferenceType.system),
                        eq(Notification.TargetType.invitation),
                        eq(invitation.getId()),
                        anyString()
                );
                verify(notificationService).sendNotification(any(Notification.class));
            }
        }

        @Nested
        @DisplayName("Negative Tests")
        class Negative {
            @Test
            @DisplayName("Should throw exception when user not found")
            void shouldThrowExceptionWhenUserNotFound() {
                // Arrange
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.acceptInvitation(invitee.getEmail(), token));

                assertEquals("User not found", exception.getMessage());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository, never()).findByToken(anyString());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when user already has a household")
            void shouldThrowExceptionWhenUserAlreadyHasHousehold() {
                // Arrange
                User userWithHousehold = new User();
                userWithHousehold.setId(5);
                userWithHousehold.setEmail("user-with-household@example.com");
                userWithHousehold.setHousehold(new Household("Another Household", "456 Another St", 2));

                when(userRepository.findByEmail(userWithHousehold.getEmail())).thenReturn(Optional.of(userWithHousehold));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.acceptInvitation(userWithHousehold.getEmail(), token));

                assertEquals("User already has a household. Leave your current household before accepting a new invitation.", exception.getMessage());
                verify(userRepository).findByEmail(userWithHousehold.getEmail());
                verify(invitationRepository, never()).findByToken(anyString());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when token is invalid")
            void shouldThrowExceptionWhenTokenIsInvalid() {
                // Arrange
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.of(invitee));
                when(invitationRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.acceptInvitation(invitee.getEmail(), "invalid-token"));

                assertEquals("Invalid token", exception.getMessage());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository).findByToken("invalid-token");
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when invitation is not for this user")
            void shouldThrowExceptionWhenInvitationNotForThisUser() {
                // Arrange
                User differentUser = new User();
                differentUser.setId(6);
                differentUser.setEmail("different@example.com");

                Invitation invitationForDifferentUser = new Invitation(inviter, "another@example.com", household, token, LocalDateTime.now().plusHours(24));
                invitationForDifferentUser.setId(2);

                when(userRepository.findByEmail(differentUser.getEmail())).thenReturn(Optional.of(differentUser));
                when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitationForDifferentUser));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.acceptInvitation(differentUser.getEmail(), token));

                assertEquals("Token is not for this user", exception.getMessage());
                verify(userRepository).findByEmail(differentUser.getEmail());
                verify(invitationRepository).findByToken(token);
                verify(invitationRepository, never()).save(any(Invitation.class));
            }
        }
    }

    @Nested
    @DisplayName("Decline Invitation Tests")
    class DeclineInvitationTests {
        @Nested
        @DisplayName("Positive Tests")
        class Positive {
            @Test
            @DisplayName("Should decline invitation successfully")
            void shouldDeclineInvitationSuccessfully() {
                // Arrange
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.of(invitee));
                when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
                when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);
                when(notificationService.createNotification(
                        eq(inviter),
                        eq(Notification.PreferenceType.system),
                        eq(Notification.TargetType.invitation),
                        eq(invitation.getId()),
                        anyString()
                )).thenReturn(new Notification());

                // Act
                invitationService.declineInvitation(invitee.getEmail(), token);

                // Verify interactions
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository).findByToken(token);
                verify(invitationRepository).save(invitation);
                verify(notificationService).createNotification(
                        eq(inviter),
                        eq(Notification.PreferenceType.system),
                        eq(Notification.TargetType.invitation),
                        eq(invitation.getId()),
                        anyString()
                );
                verify(notificationService).sendNotification(any(Notification.class));
            }
        }

        @Nested
        @DisplayName("Negative Tests")
        class Negative {
            @Test
            @DisplayName("Should throw exception when user not found")
            void shouldThrowExceptionWhenUserNotFound() {
                // Arrange
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.declineInvitation(invitee.getEmail(), token));

                assertEquals("User not found", exception.getMessage());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository, never()).findByToken(anyString());
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when token is invalid")
            void shouldThrowExceptionWhenTokenIsInvalid() {
                // Arrange
                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.of(invitee));
                when(invitationRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.declineInvitation(invitee.getEmail(), "invalid-token"));

                assertEquals("Invalid token", exception.getMessage());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository).findByToken("invalid-token");
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when invitation is not for this user")
            void shouldThrowExceptionWhenInvitationNotForThisUser() {
                // Arrange
                User differentUser = new User();
                differentUser.setId(6);
                differentUser.setEmail("different@example.com");

                Invitation invitationForDifferentUser = new Invitation(inviter, "another@example.com", household, token, LocalDateTime.now().plusHours(24));
                invitationForDifferentUser.setId(2);

                when(userRepository.findByEmail(differentUser.getEmail())).thenReturn(Optional.of(differentUser));
                when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitationForDifferentUser));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.declineInvitation(differentUser.getEmail(), token));

                assertEquals("Token is not for this user", exception.getMessage());
                verify(userRepository).findByEmail(differentUser.getEmail());
                verify(invitationRepository).findByToken(token);
                verify(invitationRepository, never()).save(any(Invitation.class));
            }

            @Test
            @DisplayName("Should throw exception when invitation is not pending")
            void shouldThrowExceptionWhenInvitationNotPending() {
                // Arrange
                // Create an invitation that has already been accepted
                Invitation acceptedInvitation = new Invitation(inviter, invitee.getEmail(), household, token, LocalDateTime.now().plusHours(24));
                acceptedInvitation.setId(3);
                acceptedInvitation.accept(); // Mark as accepted

                when(userRepository.findByEmail(invitee.getEmail())).thenReturn(Optional.of(invitee));
                when(invitationRepository.findByToken(token)).thenReturn(Optional.of(acceptedInvitation));

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.declineInvitation(invitee.getEmail(), token));

                assertEquals("Invitation is not pending", exception.getMessage());
                verify(userRepository).findByEmail(invitee.getEmail());
                verify(invitationRepository).findByToken(token);
                verify(invitationRepository, never()).save(any(Invitation.class));
            }
        }
    }

    @Nested
    @DisplayName("Get Pending Invitations Tests")
    class GetPendingInvitationsTests {
        @Nested
        @DisplayName("Get Pending Invitations For User Tests")
        class GetPendingInvitationsForUserTests {
            @Test
            @DisplayName("Should return pending invitations for user")
            void shouldReturnPendingInvitationsForUser() {
                // Arrange
                List<Invitation> pendingInvitations = Collections.singletonList(invitation);
                when(invitationRepository.findPendingByInviteeEmail(eq(invitee.getEmail()), any(LocalDateTime.class)))
                        .thenReturn(pendingInvitations);

                // Act
                List<Invitation> result = invitationService.getPendingInvitationsForUser(invitee.getEmail());

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(invitation, result.get(0));

                // Verify interactions
                verify(invitationRepository).findPendingByInviteeEmail(eq(invitee.getEmail()), any(LocalDateTime.class));
            }

            @Test
            @DisplayName("Should return empty list when no pending invitations for user")
            void shouldReturnEmptyListWhenNoPendingInvitationsForUser() {
                // Arrange
                when(invitationRepository.findPendingByInviteeEmail(eq(invitee.getEmail()), any(LocalDateTime.class)))
                        .thenReturn(Collections.emptyList());

                // Act
                List<Invitation> result = invitationService.getPendingInvitationsForUser(invitee.getEmail());

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty());

                // Verify interactions
                verify(invitationRepository).findPendingByInviteeEmail(eq(invitee.getEmail()), any(LocalDateTime.class));
            }
        }

        @Nested
        @DisplayName("Get Pending Invitations For Household Tests")
        class GetPendingInvitationsForHouseholdTests {
            @Test
            @DisplayName("Should return pending invitations for household")
            void shouldReturnPendingInvitationsForHousehold() {
                // Arrange
                List<Invitation> pendingInvitations = Collections.singletonList(invitation);
                when(householdRepository.findById(household.getId())).thenReturn(Optional.of(household));
                when(invitationRepository.findPendingByHousehold(eq(household), any(LocalDateTime.class)))
                        .thenReturn(pendingInvitations);

                // Act
                List<Invitation> result = invitationService.getPendingInvitationsForHousehold(household.getId());

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(invitation, result.get(0));

                // Verify interactions
                verify(householdRepository).findById(household.getId());
                verify(invitationRepository).findPendingByHousehold(eq(household), any(LocalDateTime.class));
            }

            @Test
            @DisplayName("Should throw exception when household not found")
            void shouldThrowExceptionWhenHouseholdNotFound() {
                // Arrange
                when(householdRepository.findById(999)).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        invitationService.getPendingInvitationsForHousehold(999));

                assertEquals("Household not found", exception.getMessage());
                verify(householdRepository).findById(999);
                verify(invitationRepository, never()).findPendingByHousehold(any(Household.class), any(LocalDateTime.class));
            }

            @Test
            @DisplayName("Should return empty list when no pending invitations for household")
            void shouldReturnEmptyListWhenNoPendingInvitationsForHousehold() {
                // Arrange
                when(householdRepository.findById(household.getId())).thenReturn(Optional.of(household));
                when(invitationRepository.findPendingByHousehold(eq(household), any(LocalDateTime.class)))
                        .thenReturn(Collections.emptyList());

                // Act
                List<Invitation> result = invitationService.getPendingInvitationsForHousehold(household.getId());

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty());

                // Verify interactions
                verify(householdRepository).findById(household.getId());
                verify(invitationRepository).findPendingByHousehold(eq(household), any(LocalDateTime.class));
            }
        }
    }

}