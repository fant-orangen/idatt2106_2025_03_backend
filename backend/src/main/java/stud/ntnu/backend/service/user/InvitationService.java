package stud.ntnu.backend.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.household.InvitationRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.model.household.Invitation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing invitations. Handles creation, retrieval, updating, and deletion of
 * invitations.
 */
@Service
public class InvitationService {

  private final InvitationRepository invitationRepository;
  private final UserRepository userRepository;
  private final HouseholdRepository householdRepository;
  private final NotificationService notificationService;
  private static final Logger log = LoggerFactory.getLogger(InvitationService.class);

  /**
   * Constructor for dependency injection.
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
   * Retrieves all invitations.
   *
   * @return list of all invitations
   */
  public List<Invitation> getAllInvitations() {
    return invitationRepository.findAll();
  }

  /**
   * Retrieves an invitation by its ID.
   *
   * @param id the ID of the invitation
   * @return an Optional containing the invitation if found
   */
  public Optional<Invitation> getInvitationById(Integer id) {
    return invitationRepository.findById(id);
  }

  /**
   * Saves an invitation.
   *
   * @param invitation the invitation to save
   * @return the saved invitation
   */
  public Invitation saveInvitation(Invitation invitation) {
    return invitationRepository.save(invitation);
  }

  /**
   * Deletes an invitation by its ID.
   *
   * @param id the ID of the invitation to delete
   */
  public void deleteInvitation(Integer id) {
    invitationRepository.deleteById(id);
  }

  /**
   * Creates a household invitation and sends a notification to the invitee.
   *
   * @param inviterEmail the email of the user sending the invitation
   * @param inviteeEmail the email of the user being invited
   * @return the invitation response containing the token
   * @throws IllegalStateException if the inviter or invitee is not found, if the inviter doesn't
   *                               have a household, or if the household already has a pending invitation
   */
  @Transactional
  public HouseholdInviteResponseDto createHouseholdInvitation(String inviterEmail, String inviteeEmail) {
    User inviter = userRepository.findByEmail(inviterEmail)
        .orElseThrow(() -> new IllegalStateException("Inviter not found"));

    // Check if user is trying to invite themselves
    if (inviterEmail.equals(inviteeEmail)) {
        throw new IllegalStateException("You cannot invite yourself to your own household");
    }

    // Check if invitee exists
    User invitee = userRepository.findByEmail(inviteeEmail)
        .orElseThrow(() -> new IllegalStateException("Invitee not found"));

    // Check if invitee already has a household
    if (invitee.getHousehold() != null) {
        throw new IllegalStateException("User already belongs to a household and cannot be invited");
    }

    // Check if inviter has a household
    Household household = inviter.getHousehold();
    if (household == null) {
      throw new IllegalStateException("Inviter doesn't have a household");
    }

    // Check if there's already a pending invitation for this email from this household
    if (invitationRepository.hasPendingInvitationForEmail(household, inviteeEmail, LocalDateTime.now())) {
      throw new IllegalStateException("There is already a pending invitation for this user");
    }

    // Generate a token
    String token = UUID.randomUUID().toString();

    // Set expiration time (24 hours from now)
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

    // Create and save the invitation
    Invitation invitation = new Invitation(inviter, inviteeEmail, household, token, expiresAt);
    invitation = invitationRepository.save(invitation);

    // Send notification to invitee
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

    log.info("Created household invitation from {} to {}", inviterEmail, inviteeEmail);

    // Return the invitation response
    return new HouseholdInviteResponseDto(
        token,
        inviteeEmail,
        household.getId(),
        household.getName(),
        expiresAt.format(DateTimeFormatter.ISO_DATE_TIME)
    );
  }

  /**
   * Accepts a household invitation.
   *
   * @param email the email of the user accepting the invitation
   * @param token the invitation token
   * @return the household that the user joined
   * @throws IllegalStateException if the user is not found, the token is invalid or expired, or the
   *                               household is not found
   */
  @Transactional
  public Household acceptInvitation(String email, String token) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user already has a household
    if (user.getHousehold() != null) {
        throw new IllegalStateException("User already has a household. Leave your current household before accepting a new invitation.");
    }

    // Find the invitation by token using the repository method
    Invitation invitation = invitationRepository.findByToken(token)
        .orElseThrow(() -> new IllegalStateException("Invalid token"));

    // Check if the invitation is for the current user
    if (!invitation.getInviteeEmail().equals(email)) {
      throw new IllegalStateException("Token is not for this user");
    }

    // Check if the invitation is pending
    if (!invitation.isPending()) {
      throw new IllegalStateException("Invitation is not pending");
    }

    // Accept the invitation
    invitation.accept();
    invitationRepository.save(invitation);

    // Update the user with the new household
    Household household = invitation.getHousehold();
    user.setHousehold(household);
    userRepository.save(user);

    // Send notification to the inviter
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

    log.info("User {} accepted invitation to household {}", email, household.getId());

    return household;
  }

  /**
   * Declines a household invitation.
   *
   * @param email the email of the user declining the invitation
   * @param token the invitation token
   * @throws IllegalStateException if the user is not found, the token is invalid or expired
   */
  @Transactional
  public void declineInvitation(String email, String token) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Find the invitation by token using the repository method
    Invitation invitation = invitationRepository.findByToken(token)
        .orElseThrow(() -> new IllegalStateException("Invalid token"));

    // Check if the invitation is for the current user
    if (!invitation.getInviteeEmail().equals(email)) {
      throw new IllegalStateException("Token is not for this user");
    }

    // Check if the invitation is pending
    if (!invitation.isPending()) {
      throw new IllegalStateException("Invitation is not pending");
    }

    // Decline the invitation
    invitation.decline();
    invitationRepository.save(invitation);

    // Send notification to the inviter
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

    log.info("User {} declined invitation to household {}", email, invitation.getHousehold().getId());
  }

  /**
   * Gets all pending invitations for a user.
   *
   * @param email the email of the user
   * @return a list of pending invitations
   */
  public List<Invitation> getPendingInvitationsForUser(String email) {
    return invitationRepository.findPendingByInviteeEmail(email, LocalDateTime.now());
  }

  /**
   * Gets all pending invitations for a household.
   *
   * @param householdId the ID of the household
   * @return a list of pending invitations
   * @throws IllegalStateException if the household is not found
   */
  public List<Invitation> getPendingInvitationsForHousehold(Integer householdId) {
    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    return invitationRepository.findPendingByHousehold(household, LocalDateTime.now());
  }
}