package stud.ntnu.backend.service.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.household.InvitationRepository;
import stud.ntnu.backend.repository.user.UserRepository;

/**
 * Service for managing household invitations. Handles creation, retrieval, updating, and deletion of
 * invitations between users and households. Provides functionality for inviting users to households,
 * accepting/declining invitations, and managing invitation states.
 */
@Service
public class InvitationService {

  private final InvitationRepository invitationRepository;
  private final UserRepository userRepository;
  private final HouseholdRepository householdRepository;
  private final NotificationService notificationService;

  /**
   * Constructs a new InvitationService with required dependencies.
   *
   * @param invitationRepository repository for invitation operations
   * @param userRepository repository for user operations
   * @param householdRepository repository for household operations
   * @param notificationService service for notification operations
   */
  public InvitationService(InvitationRepository invitationRepository,
                          UserRepository userRepository,
                          HouseholdRepository householdRepository,
                          NotificationService notificationService) {
    this.invitationRepository = invitationRepository;
    this.userRepository = userRepository;
    this.householdRepository = householdRepository;
    this.notificationService = notificationService;
  }

  /**
   * Retrieves all invitations in the system.
   *
   * @return list of all invitations
   */
  public List<Invitation> getAllInvitations() {
    return invitationRepository.findAll();
  }

  /**
   * Retrieves a specific invitation by its unique identifier.
   *
   * @param id the unique identifier of the invitation
   * @return an Optional containing the invitation if found, empty otherwise
   */
  public Optional<Invitation> getInvitationById(Integer id) {
    return invitationRepository.findById(id);
  }

  /**
   * Persists an invitation to the database.
   *
   * @param invitation the invitation entity to save
   * @return the saved invitation with generated ID
   */
  public Invitation saveInvitation(Invitation invitation) {
    return invitationRepository.save(invitation);
  }

  /**
   * Removes an invitation from the system by its ID.
   *
   * @param id the unique identifier of the invitation to delete
   */
  public void deleteInvitation(Integer id) {
    invitationRepository.deleteById(id);
  }

  /**
   * Creates a new household invitation and sends a notification to the invitee.
   * Performs validation checks for both inviter and invitee.
   *
   * @param inviterEmail the email of the user sending the invitation
   * @param inviteeEmail the email of the user being invited
   * @return the invitation response containing the token and household details
   * @throws IllegalStateException if the inviter or invitee is not found, if the inviter doesn't
   *                               have a household, or if the household already has a pending invitation
   */
  @Transactional
  public HouseholdInviteResponseDto createHouseholdInvitation(String inviterEmail, String inviteeEmail) {
    User inviter = userRepository.findByEmail(inviterEmail)
        .orElseThrow(() -> new IllegalStateException("Inviter not found"));

    if (inviterEmail.equals(inviteeEmail)) {
        throw new IllegalStateException("You cannot invite yourself to your own household");
    }

    User invitee = userRepository.findByEmail(inviteeEmail)
        .orElseThrow(() -> new IllegalStateException("Invitee not found"));

    if (invitee.getHousehold() != null) {
        throw new IllegalStateException("User already belongs to a household and cannot be invited");
    }

    Household household = inviter.getHousehold();
    if (household == null) {
      throw new IllegalStateException("Inviter doesn't have a household");
    }

    if (invitationRepository.hasPendingInvitationForEmail(household, inviteeEmail, LocalDateTime.now())) {
      throw new IllegalStateException("There is already a pending invitation for this user");
    }

    String token = UUID.randomUUID().toString();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

    Invitation invitation = new Invitation(inviter, inviteeEmail, household, token, expiresAt);
    invitation = invitationRepository.save(invitation);

    String notificationMessage = String.format(
        "You have been invited to join %s's household. Please check your invitations.",
        inviter.getName());

    Notification notification = notificationService.createNotification(
        invitee,
        Notification.PreferenceType.system,
        Notification.TargetType.invitation,
        invitation.getId(),
        notificationMessage
    );

    notificationService.sendNotification(notification);

    return new HouseholdInviteResponseDto(
        token,
        inviteeEmail,
        household.getId(),
        household.getName(),
        expiresAt.format(DateTimeFormatter.ISO_DATE_TIME)
    );
  }

  /**
   * Processes the acceptance of a household invitation.
   * Updates user's household association and sends notifications.
   *
   * @param email the email of the user accepting the invitation
   * @param token the unique invitation token
   * @return the household that the user joined
   * @throws IllegalStateException if the user is not found, the token is invalid or expired,
   *                               or if the user already belongs to a household
   */
  @Transactional
  public Household acceptInvitation(String email, String token) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    if (user.getHousehold() != null) {
        throw new IllegalStateException("User already has a household. Leave your current household before accepting a new invitation.");
    }

    Invitation invitation = invitationRepository.findByToken(token)
        .orElseThrow(() -> new IllegalStateException("Invalid token"));

    if (!invitation.getInviteeEmail().equals(email)) {
      throw new IllegalStateException("Token is not for this user");
    }

    if (!invitation.isPending()) {
      throw new IllegalStateException("Invitation is not pending");
    }

    invitation.accept();
    invitationRepository.save(invitation);

    Household household = invitation.getHousehold();
    user.setHousehold(household);
    userRepository.save(user);

    String notificationMessage = String.format(
        "%s has accepted your invitation to join your household.",
        user.getName());

    Notification notification = notificationService.createNotification(
        invitation.getInviterUser(),
        Notification.PreferenceType.system,
        Notification.TargetType.invitation,
        invitation.getId(),
        notificationMessage
    );

    notificationService.sendNotification(notification);

    return household;
  }

  /**
   * Processes the decline of a household invitation.
   * Updates invitation status and sends notifications.
   *
   * @param email the email of the user declining the invitation
   * @param token the unique invitation token
   * @throws IllegalStateException if the user is not found, the token is invalid or expired,
   *                               or if the invitation is not pending
   */
  @Transactional
  public void declineInvitation(String email, String token) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Invitation invitation = invitationRepository.findByToken(token)
        .orElseThrow(() -> new IllegalStateException("Invalid token"));

    if (!invitation.getInviteeEmail().equals(email)) {
      throw new IllegalStateException("Token is not for this user");
    }

    if (!invitation.isPending()) {
      throw new IllegalStateException("Invitation is not pending");
    }

    invitation.decline();
    invitationRepository.save(invitation);

    String notificationMessage = String.format(
        "%s has declined your invitation to join your household.",
        user.getName());

    Notification notification = notificationService.createNotification(
        invitation.getInviterUser(),
        Notification.PreferenceType.system,
        Notification.TargetType.invitation,
        invitation.getId(),
        notificationMessage
    );

    notificationService.sendNotification(notification);
  }

  /**
   * Retrieves all pending invitations for a specific user.
   *
   * @param email the email of the user
   * @return a list of pending invitations for the user
   */
  public List<Invitation> getPendingInvitationsForUser(String email) {
    return invitationRepository.findPendingByInviteeEmail(email, LocalDateTime.now());
  }

  /**
   * Retrieves all pending invitations for a specific household.
   *
   * @param householdId the unique identifier of the household
   * @return a list of pending invitations for the household
   * @throws IllegalStateException if the household is not found
   */
  public List<Invitation> getPendingInvitationsForHousehold(Integer householdId) {
    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    return invitationRepository.findPendingByHousehold(household, LocalDateTime.now());
  }
}