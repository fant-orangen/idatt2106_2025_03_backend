package stud.ntnu.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.household.HouseholdCreateRequestDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.householdAdmin.HouseholdAdmin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing households. Handles creation, retrieval, updating, and deletion of
 * households.
 */
@Service
public class HouseholdService {

  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;
  private final HouseholdAdminRepository householdAdminRepository;

  // In-memory storage for invitation tokens (in a real application, this would be stored in a database)
  private final List<HouseholdInvitation> invitations = new ArrayList<>();

  /**
   * Constructor for dependency injection.
   *
   * @param householdRepository repository for household operations
   * @param userRepository repository for user operations
   * @param householdAdminRepository repository for household admin operations
   */
  public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository,
      HouseholdAdminRepository householdAdminRepository) {
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
    this.householdAdminRepository = householdAdminRepository;
  }

  /**
   * Retrieves all households.
   *
   * @return list of all households
   */
  public List<Household> getAllHouseholds() {
    return householdRepository.findAll();
  }

  /**
   * Retrieves a household by its ID.
   *
   * @param id the ID of the household
   * @return an Optional containing the household if found
   */
  public Optional<Household> getHouseholdById(Integer id) {
    return householdRepository.findById(id);
  }

  /**
   * Saves a household.
   *
   * @param household the household to save
   * @return the saved household
   */
  public Household saveHousehold(Household household) {
    return householdRepository.save(household);
  }

  /**
   * Deletes a household by its ID.
   *
   * @param id the ID of the household to delete
   */
  public void deleteHousehold(Integer id) {
    householdRepository.deleteById(id);
  }

  /**
   * Creates a new household for the current authenticated user.
   * Checks if the user already has a household before creating a new one.
   *
   * @param requestDto the household creation request
   * @return the created household
   * @throws IllegalStateException if the user already has a household
   */
  public Household createHousehold(HouseholdCreateRequestDto requestDto) {
    // Get the current authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    // Find the user by email
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user already has a household
    if (user.getHousehold() != null) {
      throw new IllegalStateException("User already has a household");
    }

    // Create a new household
    Household household = new Household(requestDto.getName(), requestDto.getAddress(), requestDto.getPopulationCount());

    // Set optional coordinates
    household.setLatitude(requestDto.getLatitude());
    household.setLongitude(requestDto.getLongitude());

    // Save the household
    household = householdRepository.save(household);

    // Update the user with the new household
    user.setHousehold(household);
    userRepository.save(user);

    // Make the user a household admin
    HouseholdAdmin admin = new HouseholdAdmin(user, household);
    householdAdminRepository.save(admin);

    return household;
  }

  /**
   * Switches a user to a different household.
   *
   * @param email the email of the user
   * @param householdId the ID of the household to switch to
   * @return the updated household
   * @throws IllegalStateException if the user or household is not found
   */
  public Household switchHousehold(String email, Integer householdId) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    // Update the user with the new household
    user.setHousehold(household);
    userRepository.save(user);

    return household;
  }

  /**
   * Invites a user to join a household.
   *
   * @param inviterEmail the email of the user sending the invitation
   * @param inviteeEmail the email of the user being invited
   * @return the invitation response containing the token
   * @throws IllegalStateException if the inviter or invitee is not found, or if the inviter doesn't have a household
   */
  public HouseholdInviteResponseDto inviteToHousehold(String inviterEmail, String inviteeEmail) {
    User inviter = userRepository.findByEmail(inviterEmail)
        .orElseThrow(() -> new IllegalStateException("Inviter not found"));

    User invitee = userRepository.findByEmail(inviteeEmail)
        .orElseThrow(() -> new IllegalStateException("Invitee not found"));

    Household household = inviter.getHousehold();
    if (household == null) {
      throw new IllegalStateException("Inviter doesn't have a household");
    }

    // Generate a token
    String token = UUID.randomUUID().toString();

    // Set expiration time (24 hours from now)
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

    // Store the invitation
    HouseholdInvitation invitation = new HouseholdInvitation(token, inviteeEmail, household.getId(), expiresAt);
    invitations.add(invitation);

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
   * Joins a household using an invitation token.
   *
   * @param email the email of the user joining the household
   * @param token the invitation token
   * @return the joined household
   * @throws IllegalStateException if the user is not found, the token is invalid or expired, or the household is not found
   */
  public Household joinHousehold(String email, String token) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Find the invitation by token
    HouseholdInvitation invitation = invitations.stream()
        .filter(inv -> inv.token().equals(token))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Invalid token"));

    // Check if the invitation is for the current user
    if (!invitation.inviteeEmail().equals(email)) {
      throw new IllegalStateException("Token is not for this user");
    }

    // Check if the invitation has expired
    if (invitation.expiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Token has expired");
    }

    // Find the household
    Household household = householdRepository.findById(invitation.householdId())
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    // Update the user with the new household
    user.setHousehold(household);
    userRepository.save(user);

    // Remove the invitation
    invitations.removeIf(inv -> inv.token().equals(token));

    return household;
  }

  /**
   * Gets the current user's household.
   *
   * @param email the email of the user
   * @return the household DTO
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public HouseholdDto getCurrentUserHousehold(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Convert to DTO
    return convertToHouseholdDto(household);
  }

  /**
   * Converts a Household entity to a HouseholdDto.
   *
   * @param household the household entity
   * @return the household DTO
   */
  private HouseholdDto convertToHouseholdDto(Household household) {
    List<HouseholdDto.HouseholdMemberDto> members = household.getUsers().stream()
        .map(user -> new HouseholdDto.HouseholdMemberDto(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        ))
        .collect(Collectors.toList());

    return new HouseholdDto(
        household.getId(),
        household.getName(),
        household.getAddress(),
        household.getPopulationCount(),
        household.getLatitude(),
        household.getLongitude(),
        members
    );
  }

  /**
   * Leaves the current household if the user is not a household admin.
   *
   * @param email the email of the user leaving the household
   * @throws IllegalStateException if the user is a household admin or doesn't have a household
   */
  public void leaveHousehold(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Check if user is a household admin
    if (householdAdminRepository.existsByUser(user)) {
      throw new IllegalStateException("Household admins cannot leave their household");
    }

    // Remove user from household
    user.setHousehold(null);
    userRepository.save(user);
  }

  /**
     * Class representing a household invitation.
     */
    private record HouseholdInvitation(String token, String inviteeEmail, Integer householdId,
                                       LocalDateTime expiresAt) {

  }
}
