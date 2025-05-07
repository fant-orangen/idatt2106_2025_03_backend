package stud.ntnu.backend.service.household;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.household.HouseholdCreateRequestDto;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.dto.household.HouseholdMemberDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberCreateDto;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.household.InvitationRepository;

import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.service.user.InvitationService;
import stud.ntnu.backend.util.LocationUtil;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.repository.household.EmptyHouseholdMemberRepository;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.EmptyHouseholdMember;
import stud.ntnu.backend.model.household.Invitation;

import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.household.HouseholdAdmin;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
  private final EmptyHouseholdMemberRepository emptyHouseholdMemberRepository;
  private final InvitationService invitationService;
  private final InvitationRepository invitationRepository;

  private final ProductTypeRepository productTypeRepository;
  private final ProductBatchRepository productBatchRepository;
  private final GroupMembershipRepository groupMembershipRepository;
  private static final Logger log = LoggerFactory.getLogger(HouseholdService.class);

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Constructor for dependency injection.
   *
   * @param householdRepository            repository for household operations
   * @param userRepository                 repository for user operations
   * @param householdAdminRepository       repository for household admin operations
   * @param emptyHouseholdMemberRepository repository for empty household member operations
   * @param invitationService              service for invitation operations
   * @param invitationRepository           repository for invitation operations

   * @param productTypeRepository          repository for product type operations
   */
  public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository,
      HouseholdAdminRepository householdAdminRepository,
      EmptyHouseholdMemberRepository emptyHouseholdMemberRepository,
      InvitationService invitationService,
      InvitationRepository invitationRepository,

      ProductTypeRepository productTypeRepository,
      ProductBatchRepository productBatchRepository,
      GroupMembershipRepository groupMembershipRepository) {
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
    this.householdAdminRepository = householdAdminRepository;
    this.emptyHouseholdMemberRepository = emptyHouseholdMemberRepository;
    this.invitationService = invitationService;
    this.invitationRepository = invitationRepository;

    this.productTypeRepository = productTypeRepository;
    this.productBatchRepository = productBatchRepository;
    this.groupMembershipRepository = groupMembershipRepository;
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
   * Updates a household's name and address. Only household admins can update households.
   *
   * @param email the email of the user updating the household
   * @param name the new name for the household
   * @param address the new address for the household
   * @return the updated household
   * @throws IllegalStateException if the user is not found, doesn't have a household, or is not an admin
   */
  @Transactional
  public Household updateHousehold(String email, String name, String address) {
    log.info("User {} attempting to update household with name: {} and address: {}", email, name, address);

    // Check if the user exists
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user has a household
    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Check if the user is an admin
    if (!isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("Only household admins can update households");
    }

    // Update the household
    household.setName(name);
    household.setAddress(address);

    // If the address changed, try to update the coordinates
    if (!address.equals(household.getAddress())) {
      try {
        CoordinatesItemDto coordinates = LocationUtil.getCoordinatesByAddress(address);
        if (coordinates != null) {
          household.setLatitude(coordinates.getLatitude());
          household.setLongitude(coordinates.getLongitude());
          log.info("Updated coordinates for household {}: lat={}, lng={}",
              household.getId(), coordinates.getLatitude(), coordinates.getLongitude());
        }
      } catch (Exception e) {
        log.warn("Failed to update coordinates for household {}: {}", household.getId(), e.getMessage());
        // Continue without updating coordinates
      }
    }

    Household updatedHousehold = householdRepository.save(household);
    log.info("Successfully updated household {}", updatedHousehold.getId());

    return updatedHousehold;
  }

  /**
   * Hard deletes a household by its ID.
   * This method removes all household-related data but preserves user accounts.
   *
   * @param id the ID of the household to delete
   * @throws IllegalStateException if the household is not found
   */
  @Transactional
  public void deleteHousehold(Integer id) {
    log.info("Attempting to hard delete household with ID: {}", id);

    Household household = householdRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    try {
      List<User> householdUsers = userRepository.findByHousehold(household);
      for (User user : householdUsers) {
        user.setHousehold(null);
        userRepository.save(user);
      }
      log.info("Unlinked {} users from household {}", householdUsers.size(), id);

      householdAdminRepository.deleteAll(householdAdminRepository.findByHousehold(household));

      invitationRepository.deleteAll(invitationRepository.findByHousehold(household));

      emptyHouseholdMemberRepository.deleteAll(emptyHouseholdMemberRepository.findByHousehold(household));

      List<ProductType> productTypes = entityManager.createQuery(
          "SELECT pt FROM ProductType pt WHERE pt.household.id = :householdId", ProductType.class)
          .setParameter("householdId", household.getId())
          .getResultList();

      for (ProductType productType : productTypes) {
        productBatchRepository.deleteAll(
          entityManager.createQuery(
            "SELECT pb FROM ProductBatch pb WHERE pb.productType.id = :productTypeId", ProductBatch.class)
            .setParameter("productTypeId", productType.getId())
            .getResultList()
        );
      }

      productTypeRepository.deleteAll(productTypes);

      groupMembershipRepository.deleteAll(
        entityManager.createQuery(
          "SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId", GroupMembership.class)
          .setParameter("householdId", household.getId())
          .getResultList()
      );

      householdRepository.delete(household);
      log.info("Successfully deleted household {}", id);
    } catch (Exception e) {
      log.error("Error deleting household {}: {}", id, e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Hard deletes the current user's household. Only household admins can delete households.
   * This permanently removes the household and all related data from the database.
   *
   * @param email the email of the user deleting the household
   * @throws IllegalStateException if the user is not found, doesn't have a household, or is not an admin
   */
  @Transactional
  public void deleteCurrentHousehold(String email) {
    log.info("User {} attempting to hard delete household", email);

    try {
      // Check if the user exists
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new IllegalStateException("User not found"));
      log.info("Found user with ID: {}", user.getId());

      // Check if the user has a household
      Household household = user.getHousehold();
      if (household == null) {
        throw new IllegalStateException("User doesn't have a household");
      }
      log.info("Found household with ID: {}", household.getId());

      // Check if the user is an admin
      if (!isUserHouseholdAdmin(user)) {
        throw new IllegalStateException("Only household admins can delete households");
      }
      log.info("User is confirmed as household admin");

      // Call the deleteHousehold method to handle the actual deletion
      deleteHousehold(household.getId());

    } catch (Exception e) {
      log.error("Error hard deleting household: {}", e.getMessage(), e);
      throw e; // Re-throw the exception to be handled by the controller
    }
  }

  /**
   * Creates a new household for the current authenticated user. Checks if the user already has a
   * household before creating a new one.
   *
   * @param requestDto the household creation request
   * @return the created household
   * @throws IllegalStateException if the user already has a household
   */
  @Transactional
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
    Household household = new Household(requestDto.getName(), requestDto.getAddress(),
        requestDto.getPopulationCount());

    if (requestDto.getAddress() != null && !requestDto.getAddress().trim().isEmpty()) {
      try {
        log.info("Attempting to geocode address: {}", requestDto.getAddress());
        // Call the LocationUtil to get coordinates from the address
        CoordinatesItemDto coordinates = LocationUtil.getCoordinatesByAddress(
            requestDto.getAddress());

        if (coordinates != null && coordinates.getLatitude() != null
            && coordinates.getLongitude() != null) {
          // Set the latitude and longitude on the household object
          household.setLatitude(coordinates.getLatitude());
          household.setLongitude(coordinates.getLongitude());
          log.info("Successfully geocoded address to Lat: {}, Lon: {}", coordinates.getLatitude(),
              coordinates.getLongitude());
        } else {
          log.warn("Geocoding returned null or incomplete coordinates for address: {}",
              requestDto.getAddress());
          // Keep household.latitude and household.longitude as null
        }
      } catch (IllegalArgumentException e) {
        // Log the error if geocoding fails, but allow household creation without coordinates
        log.error("Could not geocode address '{}': {}", requestDto.getAddress(), e.getMessage());
        // Keep household.latitude and household.longitude as null
      } catch (Exception e) {
        // Catch unexpected errors during geocoding
        log.error("Unexpected error during geocoding for address '{}': {}", requestDto.getAddress(),
            e.getMessage(), e);
        // Keep household.latitude and household.longitude as null
      }
    } else {
      log.info("No address provided, skipping geocoding.");
      // Set optional coordinates from DTO if they were manually provided (though the goal is to calculate)
      // This part is less relevant now as we prioritize calculation, but keep it for flexibility
      household.setLatitude(requestDto.getLatitude());
      household.setLongitude(requestDto.getLongitude());
    }

    // Save the household first
    household = householdRepository.save(household);

    // Update the user with the new household
    user.setHousehold(household);
    user = userRepository.save(user);

    // Check if user is already an admin
    if (householdAdminRepository.existsByUser(user)) {
      throw new IllegalStateException("User is already a household admin");
    }

    // Create and save the household admin
    HouseholdAdmin admin = new HouseholdAdmin(user, household);
    householdAdminRepository.save(admin);

    return household;
  }

  /**
   * Switches a user to a different household.
   *
   * @param email       the email of the user
   * @param householdId the ID of the household to switch to
   * @return the updated household
   * @throws IllegalStateException if the user or household is not found
   */
  @Transactional
  public Household switchHousehold(String email, Integer householdId) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household currentHousehold = user.getHousehold();
    Household targetHousehold = householdRepository.findById(householdId)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    // Check if user is an admin and if they are the last admin
    boolean isAdmin = householdAdminRepository.existsByUser(user);
    if (isAdmin && currentHousehold != null) {
      // Count the number of admins in the current household
      long adminCount = currentHousehold.getUsers().stream()
          .filter(householdAdminRepository::existsByUser)
          .count();

      // If this is the last admin, prevent switching
      if (adminCount <= 1) {
        throw new IllegalStateException("The last household admin cannot switch households");
      }

      // Remove admin status before switching
      HouseholdAdmin admin = householdAdminRepository.findByUser(user)
          .orElseThrow(() -> new IllegalStateException("Admin record not found"));
      householdAdminRepository.delete(admin);
    }

    // Update the user with the new household
    user.setHousehold(targetHousehold);
    userRepository.save(user);

    return targetHousehold;
  }

  /**
   * Invites a user to join a household.
   *
   * @param inviterEmail the email of the user sending the invitation
   * @param inviteeEmail the email of the user being invited
   * @return the invitation response containing the token
   * @throws IllegalStateException if the inviter or invitee is not found, if the inviter doesn't
   *                               have a household, or if the household already has a pending invitation
   */
  public HouseholdInviteResponseDto inviteToHousehold(String inviterEmail, String inviteeEmail) {
    // Check if the user is an admin of the household
    User inviter = userRepository.findByEmail(inviterEmail)
        .orElseThrow(() -> new IllegalStateException("Inviter not found"));

    if (!isUserHouseholdAdmin(inviter)) {
      throw new IllegalStateException("Only household admins can send invitations");
    }

    // Delegate to the invitation service
    return invitationService.createHouseholdInvitation(inviterEmail, inviteeEmail);
  }

  /**
   * Joins a household using an invitation token.
   *
   * @param email the email of the user joining the household
   * @param token the invitation token
   * @return the joined household
   * @throws IllegalStateException if the user is not found, the token is invalid or expired, or the
   *                               household is not found
   */
  public Household joinHousehold(String email, String token) {
    // Delegate to the invitation service
    return invitationService.acceptInvitation(email, token);
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
    List<HouseholdMemberDto> members = household.getUsers().stream()
        .map(user -> {
          boolean isAdmin = householdAdminRepository.existsByUser(user);
          return new HouseholdMemberDto(
              user.getId(),
              user.getEmail(),
              user.getFirstName(),
              user.getLastName(),
              isAdmin
          );
        })
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
   * Leaves the current household if the user is not a household admin or if there are other admins.
   *
   * @param email the email of the user leaving the household
   * @throws IllegalStateException if the user is the last household admin or doesn't have a household
   */
  @Transactional
  public void leaveHousehold(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Check if user is a household admin
    boolean isAdmin = householdAdminRepository.existsByUser(user);

    if (isAdmin) {
      // Count the number of admins in the household
      long adminCount = household.getUsers().stream()
          .filter(householdAdminRepository::existsByUser)
          .count();

      // If this is the last admin, prevent leaving
      if (adminCount <= 1) {
        throw new IllegalStateException("The last household admin cannot leave the household");
      }

      // If there are other admins, remove admin status first
      HouseholdAdmin admin = householdAdminRepository.findByUser(user)
          .orElseThrow(() -> new IllegalStateException("Admin record not found"));
      householdAdminRepository.delete(admin);
    }

    // Remove user from household
    user.setHousehold(null);
    userRepository.save(user);

    log.info("User {} left household {}", email, household.getId());
  }

  /**
   * Gets all members of the current user's household.
   *
   * @param email the email of the user
   * @return list of household members
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public List<HouseholdMemberDto> getHouseholdMembers(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    return userRepository.findByHousehold(household).stream()
        .map(member -> {
          boolean isAdmin = householdAdminRepository.existsByUser(member);
          return new HouseholdMemberDto(
              member.getId(),
              member.getEmail(),
              member.getFirstName(),
              member.getLastName(),
              isAdmin
          );
        })
        .collect(Collectors.toList());
  }

  /**
   * Gets only non-admin members of the current user's household.
   *
   * @param email the email of the user
   * @return list of non-admin household members
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public List<HouseholdMemberDto> getNonAdminHouseholdMembers(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    return userRepository.findByHousehold(household).stream()
        .map(member -> {
          boolean isAdmin = householdAdminRepository.existsByUser(member);
          return new HouseholdMemberDto(
              member.getId(),
              member.getEmail(),
              member.getFirstName(),
              member.getLastName(),
              isAdmin
          );
        })
        .filter(memberDto -> !memberDto.isAdmin()) // Filter out admin members
        .collect(Collectors.toList());
  }

  /**
   * Gets all empty members of the current user's household.
   *
   * @param email the email of the user
   * @return list of empty household members
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public List<EmptyHouseholdMemberDto> getEmptyHouseholdMembers(String email) {
    log.info("Fetching empty members for user: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));
    log.info("Found user: {}", user.getId());

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }
    log.info("Found household: {}", household.getId());

    List<EmptyHouseholdMember> members = emptyHouseholdMemberRepository.findByHousehold(household);
    log.info("Found {} empty members", members.size());

    return members.stream()
        .map(member -> new EmptyHouseholdMemberDto(
            member.getId(),
            member.getName(),
            member.getType(),
            member.getDescription(),
            member.getKcalRequirement()
        ))
        .collect(Collectors.toList());
  }

  /**
   * Adds an empty household member to the current user's household.
   *
   * @param email     the email of the user
   * @param createDto the empty household member creation DTO
   * @return the created empty household member DTO
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  @Transactional
  public EmptyHouseholdMemberDto addEmptyHouseholdMember(String email,
      EmptyHouseholdMemberCreateDto createDto) {
    log.info("Adding empty member for user: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));
    log.info("Found user: {}", user.getId());

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }
    log.info("Found household: {}", household.getId());

    // Check if the user is a household admin
    if (!isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("Only household admins can add empty household members");
    }

    // Create the empty household member
    EmptyHouseholdMember member = new EmptyHouseholdMember(
        createDto.getName(),
        createDto.getType(),
        createDto.getDescription(),
        createDto.getKcalRequirement()
    );
    member.setHousehold(household);

    // Save the member
    member = emptyHouseholdMemberRepository.save(member);
    log.info("Created empty member with ID: {}", member.getId());

    // Return the DTO
    return new EmptyHouseholdMemberDto(
        member.getId(),
        member.getName(),
        member.getType(),
        member.getDescription(),
        member.getKcalRequirement()
    );
  }

  /**
   * Removes an empty household member from the current user's household.
   *
   * @param email    the email of the user
   * @param memberId the ID of the empty household member to remove
   * @throws IllegalStateException if the user is not found, doesn't have a household,
   *                               or the member doesn't belong to the user's household
   */
  @Transactional
  public void removeEmptyHouseholdMember(String email, Integer memberId) {
    log.info("Removing empty member with ID {} for user: {}", memberId, email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));
    log.info("Found user: {}", user.getId());

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }
    log.info("Found household: {}", household.getId());

    // Check if the user is a household admin
    if (!isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("Only household admins can remove empty household members");
    }

    // Find the empty household member
    EmptyHouseholdMember member = emptyHouseholdMemberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalStateException("Empty household member not found"));

    // Check if the member belongs to the user's household
    if (member.getHousehold() == null || !member.getHousehold().getId().equals(household.getId())) {
      throw new IllegalStateException("Empty household member does not belong to the user's household");
    }

    // Delete the member
    emptyHouseholdMemberRepository.delete(member);
    log.info("Removed empty member with ID: {}", memberId);
  }

  /**
   * Checks if a user is an admin of their household.
   *
   * @param user the user to check
   * @return true if the user is an admin of their household, false otherwise
   */
  public boolean isUserHouseholdAdmin(User user) {
    return householdAdminRepository.existsByUser(user);
  }

  /**
   * Declines a household invitation.
   *
   * @param email the email of the user declining the invitation
   * @param token the invitation token
   * @throws IllegalStateException if the user is not found, the token is invalid or expired
   */
  public void declineHouseholdInvitation(String email, String token) {
    invitationService.declineInvitation(email, token);
  }

  /**
   * Gets all pending invitations for a household.
   *
   * @param householdId the ID of the household
   * @return a list of pending invitations
   * @throws IllegalStateException if the household is not found
   */
  public List<Invitation> getPendingInvitationsForHousehold(Integer householdId) {
    return invitationService.getPendingInvitationsForHousehold(householdId);
  }

  /**
   * Cancels a pending invitation by token. Only household admins can cancel invitations.
   *
   * @param adminEmail the email of the admin canceling the invitation
   * @param token the token of the invitation to cancel
   * @throws IllegalStateException if the admin is not found, is not an admin, or the invitation is not found
   */
  @Transactional
  public void cancelInvitationByToken(String adminEmail, String token) {
    log.info("User {} attempting to cancel invitation with token {}", adminEmail, token);

    // Check if the user exists
    User admin = userRepository.findByEmail(adminEmail)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user has a household
    Household household = admin.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Check if the user is an admin
    if (!isUserHouseholdAdmin(admin)) {
      throw new IllegalStateException("Only household admins can cancel invitations");
    }

    // Find the invitation by token
    Invitation invitation = invitationRepository.findByToken(token)
        .orElseThrow(() -> new IllegalStateException("Invitation not found"));

    // Check if the invitation belongs to the admin's household
    if (invitation.getHousehold() == null || !invitation.getHousehold().getId().equals(household.getId())) {
      throw new IllegalStateException("Invitation does not belong to the admin's household");
    }

    // Check if the invitation is still pending
    if (!invitation.isPending()) {
      throw new IllegalStateException("Invitation is not pending");
    }

    // Cancel the invitation by setting declinedAt
    invitation.setDeclinedAt(LocalDateTime.now());
    invitationRepository.save(invitation);

    log.info("Invitation with token {} canceled by admin {}", token, adminEmail);
  }

  /**
   * Promotes a user to household admin.
   *
   * @param adminEmail the email of the current admin
   * @param userEmail the email of the user to promote
   * @throws IllegalStateException if the admin or user is not found, if the admin is not an admin,
   *                              or if the user is not in the same household
   */
  @Transactional
  public void promoteToAdmin(String adminEmail, String userEmail) {
    User admin = userRepository.findByEmail(adminEmail)
        .orElseThrow(() -> new IllegalStateException("Admin not found"));

    if (!isUserHouseholdAdmin(admin)) {
      throw new IllegalStateException("Only household admins can promote users");
    }

    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household adminHousehold = admin.getHousehold();
    Household userHousehold = user.getHousehold();

    if (adminHousehold == null || userHousehold == null || !adminHousehold.getId().equals(userHousehold.getId())) {
      throw new IllegalStateException("User is not in the same household");
    }

    // Check if user is already an admin
    if (isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("User is already a household admin");
    }

    // Create a new household admin
    HouseholdAdmin householdAdmin = new HouseholdAdmin(user, adminHousehold);
    householdAdminRepository.save(householdAdmin);

    log.info("User {} promoted to admin of household {}", userEmail, adminHousehold.getId());
  }

  /**
   * Removes a member from the household. Only household admins can remove members.
   *
   * @param adminEmail the email of the admin removing the member
   * @param memberId   the ID of the member to remove
   * @throws IllegalStateException if the admin or member is not found, if the admin is not an admin,
   *                               if the member is not in the same household, or if the member is the last admin
   */
  @Transactional
  public void removeMemberFromHousehold(String adminEmail, Integer memberId) {
    log.info("Admin {} removing member {} from household", adminEmail, memberId);

    // Check if the admin exists and is an admin
    User admin = userRepository.findByEmail(adminEmail)
        .orElseThrow(() -> new IllegalStateException("Admin not found"));

    if (!isUserHouseholdAdmin(admin)) {
      throw new IllegalStateException("Only household admins can remove members");
    }

    Household household = admin.getHousehold();
    if (household == null) {
      throw new IllegalStateException("Admin doesn't have a household");
    }

    // Find the member to remove
    User member = userRepository.findById(memberId)
        .orElseThrow(() -> new IllegalStateException("Member not found"));

    // Check if the member is in the same household
    if (member.getHousehold() == null || !member.getHousehold().getId().equals(household.getId())) {
      throw new IllegalStateException("Member is not in the same household");
    }

    // Check if the member is an admin
    boolean isMemberAdmin = isUserHouseholdAdmin(member);

    if (isMemberAdmin) {
      // Count the number of admins in the household
      long adminCount = household.getUsers().stream()
          .filter(householdAdminRepository::existsByUser)
          .count();

      // If this is the last admin, prevent removal
      if (adminCount <= 1) {
        throw new IllegalStateException("Cannot remove the last household admin");
      }

      // If there are other admins, remove admin status first
      HouseholdAdmin householdAdmin = householdAdminRepository.findByUser(member)
          .orElseThrow(() -> new IllegalStateException("Admin record not found"));
      householdAdminRepository.delete(householdAdmin);
    }

    // Remove the member from the household
    member.setHousehold(null);
    userRepository.save(member);

    log.info("Member {} removed from household {}", memberId, household.getId());
  }
}
