package stud.ntnu.backend.service.household;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import stud.ntnu.backend.dto.household.EmptyHouseholdMemberCreateDto;
import stud.ntnu.backend.dto.household.EmptyHouseholdMemberDto;
import stud.ntnu.backend.dto.household.HouseholdCreateRequestDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.dto.household.HouseholdInviteResponseDto;
import stud.ntnu.backend.dto.household.HouseholdMemberDto;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.household.EmptyHouseholdMember;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.HouseholdAdmin;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.repository.household.EmptyHouseholdMemberRepository;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.household.InvitationRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.InvitationService;
import stud.ntnu.backend.util.LocationUtil;

/**
 * Service for managing households and their members. Handles all household-related operations
 * including: - Household creation, update, and deletion - Member management (adding, removing,
 * promoting to admin) - Invitation handling - Empty member management - Household data retrieval
 * and search
 */
@Service
@RequiredArgsConstructor
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

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Retrieves all households in the system. This method should be used with caution as it returns
   * all households without pagination.
   *
   * @return a list of all households
   */
  public List<Household> getAllHouseholds() {
    return householdRepository.findAll();
  }

  /**
   * Retrieves a household by its ID.
   *
   * @param id the ID of the household to retrieve
   * @return an Optional containing the household if found, empty otherwise
   */
  public Optional<Household> getHouseholdById(Integer id) {
    return householdRepository.findById(id);
  }

  /**
   * Saves a household entity. This is a low-level method used internally by other service methods.
   *
   * @param household the household entity to save
   * @return the saved household with updated information (e.g., generated ID)
   */
  public Household saveHousehold(Household household) {
    return householdRepository.save(household);
  }

  /**
   * Updates a household's name and address. Only household admins can perform this operation. If a
   * new address is provided, attempts to geocode it to update coordinates.
   *
   * @param email   the email of the user attempting the update
   * @param name    the new name for the household
   * @param address the new address for the household
   * @return the updated household
   * @throws IllegalStateException if the user is not found, doesn't have a household, or is not an
   *                               admin
   */
  @Transactional
  public Household updateHousehold(String email, String name, String address) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    if (!isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("Only household admins can update households");
    }

    if (!name.equals(household.getName()) && householdRepository.existsByName(name)) {
      throw new IllegalStateException("A household with this name already exists");
    }

    // Validate address before making any changes
    CoordinatesItemDto coordinates = LocationUtil.getCoordinatesByAddress(address);
    if (coordinates == null || coordinates.getLatitude() == null || coordinates.getLongitude() == null) {
      throw new IllegalStateException("Invalid address: Could not find coordinates");
    }

    // Only update if validation passes
    household.setName(name);
    household.setAddress(address);
    household.setLatitude(coordinates.getLatitude());
    household.setLongitude(coordinates.getLongitude());

    return householdRepository.save(household);
  }

  /**
   * Hard deletes a household and all its related data. This operation: - Unlinks all users from the
   * household - Removes all admin records - Deletes all invitations - Removes empty household
   * members - Deletes product types and batches - Removes group memberships - Finally deletes the
   * household itself
   *
   * @param id the ID of the household to delete
   * @throws IllegalStateException if the household is not found
   */
  @Transactional
  public void deleteHousehold(Integer id) {
    Household household = householdRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    try {
      List<User> householdUsers = userRepository.findByHousehold(household);
      for (User user : householdUsers) {
        user.setHousehold(null);
        userRepository.save(user);
      }

      householdAdminRepository.deleteAll(householdAdminRepository.findByHousehold(household));

      invitationRepository.deleteAll(invitationRepository.findByHousehold(household));

      emptyHouseholdMemberRepository.deleteAll(
          emptyHouseholdMemberRepository.findByHousehold(household));

      List<ProductType> productTypes = entityManager.createQuery(
              "SELECT pt FROM ProductType pt WHERE pt.household.id = :householdId", ProductType.class)
          .setParameter("householdId", household.getId())
          .getResultList();

      for (ProductType productType : productTypes) {
        productBatchRepository.deleteAll(
            entityManager.createQuery(
                    "SELECT pb FROM ProductBatch pb WHERE pb.productType.id = :productTypeId",
                    ProductBatch.class)
                .setParameter("productTypeId", productType.getId())
                .getResultList()
        );
      }

      productTypeRepository.deleteAll(productTypes);

      groupMembershipRepository.deleteAll(
          entityManager.createQuery(
                  "SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId",
                  GroupMembership.class)
              .setParameter("householdId", household.getId())
              .getResultList()
      );

      householdRepository.delete(household);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * Hard deletes the current user's household. Only household admins can perform this operation.
   * This is a convenience method that delegates to {@link #deleteHousehold(Integer)}.
   *
   * @param email the email of the user attempting to delete their household
   * @throws IllegalStateException if the user is not found, doesn't have a household, or is not an
   *                               admin
   */
  @Transactional
  public void deleteCurrentHousehold(String email) {
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
      throw new IllegalStateException("Only household admins can delete households");
    }

    // Call the deleteHousehold method to handle the actual deletion
    deleteHousehold(household.getId());
  }

  /**
   * Creates a new household for a user. The creating user automatically becomes an admin of the new
   * household. If an address is provided, attempts to geocode it to set coordinates.
   *
   * @param requestDto the DTO containing the household creation details
   * @return the newly created household
   * @throws IllegalStateException if the user already has a household
   */
  @Transactional
  public Household createHousehold(HouseholdCreateRequestDto requestDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    if (user.getHousehold() != null) {
      throw new IllegalStateException("User already has a household");
    }

    if (householdRepository.existsByName(requestDto.getName())) {
      throw new IllegalStateException("A household with this name already exists");
    }

    Household household = new Household(requestDto.getName(), requestDto.getAddress(),
        requestDto.getPopulationCount());

    if (requestDto.getAddress() != null && !requestDto.getAddress().trim().isEmpty()) {
      CoordinatesItemDto coordinates = LocationUtil.getCoordinatesByAddress(requestDto.getAddress());
      if (coordinates == null || coordinates.getLatitude() == null || coordinates.getLongitude() == null) {
        throw new IllegalStateException("Invalid address: Could not find coordinates");
      }
      household.setLatitude(coordinates.getLatitude());
      household.setLongitude(coordinates.getLongitude());
    } else {
      household.setLatitude(requestDto.getLatitude());
      household.setLongitude(requestDto.getLongitude());
    }

    household = householdRepository.save(household);
    user.setHousehold(household);
    user = userRepository.save(user);

    if (householdAdminRepository.existsByUser(user)) {
      throw new IllegalStateException("User is already a household admin");
    }

    HouseholdAdmin admin = new HouseholdAdmin(user, household);
    householdAdminRepository.save(admin);

    return household;
  }

  /**
   * Invites a user to join a household. Only household admins can send invitations. The invitation
   * includes a token that the invitee can use to accept or decline.
   *
   * @param inviterEmail the email of the admin sending the invitation
   * @param inviteeEmail the email of the user being invited
   * @return a response DTO containing the invitation token and related information
   * @throws IllegalStateException if the inviter is not found or is not an admin
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
   * Accepts a household invitation using the provided token. This operation: - Validates the token
   * - Checks if the invitation is still pending - Moves the user to the new household
   *
   * @param email the email of the user accepting the invitation
   * @param token the invitation token
   * @return the joined household
   * @throws IllegalStateException if the token is invalid or expired
   */
  public Household joinHousehold(String email, String token) {
    // Delegate to the invitation service
    return invitationService.acceptInvitation(email, token);
  }

  /**
   * Retrieves the current user's household information. The returned DTO includes: - Basic
   * household information (name, address, etc.) - List of all household members with their roles -
   * Household coordinates if available
   *
   * @param email the email of the user
   * @return a DTO containing the household information
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
   * Leaves the current household. Users cannot leave if they are the last admin of the household.
   * If the user is an admin but not the last one, their admin status is removed before leaving.
   *
   * @param email the email of the user leaving the household
   * @throws IllegalStateException if the user is not found, doesn't have a household, or is the
   *                               last admin of the household
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
    updatePopulationCount(household);
  }

  /**
   * Gets all members of the current user's household. The returned list includes both regular
   * members and admins, with a flag indicating their admin status.
   *
   * @param email the email of the user
   * @return list of household member DTOs
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
   * Gets only non-admin members of the current user's household. This is useful for admin
   * operations that should only target regular members.
   *
   * @param email the email of the user
   * @return list of non-admin household member DTOs
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
   * Gets all empty members (non-user entities) of the current user's household. Empty members
   * represent household members that don't have user accounts, such as children or pets.
   *
   * @param email the email of the user
   * @return list of empty household member DTOs
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public List<EmptyHouseholdMemberDto> getEmptyHouseholdMembers(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    List<EmptyHouseholdMember> members = emptyHouseholdMemberRepository.findByHousehold(household);

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
   * Adds an empty household member to the current user's household. Only household admins can add
   * empty members. Empty members are used to represent household members without user accounts.
   *
   * @param email     the email of the admin adding the member
   * @param createDto the DTO containing the empty member's information
   * @return the created empty member DTO
   * @throws IllegalStateException if the user is not found, doesn't have a household, or is not an
   *                               admin
   */
  @Transactional
  public EmptyHouseholdMemberDto addEmptyHouseholdMember(String email,
      EmptyHouseholdMemberCreateDto createDto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

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

    // Return the DTO
    updatePopulationCount(household);
    return new EmptyHouseholdMemberDto(
        member.getId(),
        member.getName(),
        member.getType(),
        member.getDescription(),
        member.getKcalRequirement()
    );
  }

  /**
   * Removes an empty household member. Only household admins can remove empty members. The empty
   * member must belong to the admin's household.
   *
   * @param email    the email of the admin removing the member
   * @param memberId the ID of the empty member to remove
   * @throws IllegalStateException if the user is not found, is not an admin, or the member doesn't
   *                               belong to their household
   */
  @Transactional
  public void removeEmptyHouseholdMember(String email, Integer memberId) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Check if the user is a household admin
    if (!isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("Only household admins can remove empty household members");
    }

    // Find the empty household member
    EmptyHouseholdMember member = emptyHouseholdMemberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalStateException("Empty household member not found"));

    // Check if the member belongs to the user's household
    if (member.getHousehold() == null || !member.getHousehold().getId().equals(household.getId())) {
      throw new IllegalStateException(
          "Empty household member does not belong to the user's household");
    }

    // Delete the member
    emptyHouseholdMemberRepository.delete(member);
    updatePopulationCount(household);
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
   * Declines a household invitation. This operation marks the invitation as declined and prevents
   * it from being used.
   *
   * @param email the email of the user declining the invitation
   * @param token the invitation token
   * @throws IllegalStateException if the token is invalid or expired
   */
  public void declineHouseholdInvitation(String email, String token) {
    invitationService.declineInvitation(email, token);
  }

  /**
   * Gets all pending invitations for a household. This includes invitations that haven't been
   * accepted or declined yet.
   *
   * @param householdId the ID of the household
   * @return list of pending invitations
   * @throws IllegalStateException if the household is not found
   */
  public List<Invitation> getPendingInvitationsForHousehold(Integer householdId) {
    return invitationService.getPendingInvitationsForHousehold(householdId);
  }

  /**
   * Cancels a pending invitation. Only household admins can cancel invitations for their household.
   * The invitation must belong to the admin's household.
   *
   * @param adminEmail the email of the admin canceling the invitation
   * @param token      the token of the invitation to cancel
   * @throws IllegalStateException if the admin is not found, is not an admin, or the invitation
   *                               doesn't belong to their household
   */
  @Transactional
  public void cancelInvitationByToken(String adminEmail, String token) {
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
    if (invitation.getHousehold() == null || !invitation.getHousehold().getId()
        .equals(household.getId())) {
      throw new IllegalStateException("Invitation does not belong to the admin's household");
    }

    // Check if the invitation is still pending
    if (!invitation.isPending()) {
      throw new IllegalStateException("Invitation is not pending");
    }

    // Cancel the invitation by setting declinedAt
    invitation.setDeclinedAt(LocalDateTime.now());
    invitationRepository.save(invitation);
  }

  /**
   * Promotes a household member to admin status. Only existing admins can promote other members.
   * The member must be in the same household as the admin.
   *
   * @param adminEmail the email of the current admin
   * @param userEmail  the email of the user to promote
   * @throws IllegalStateException if either user is not found, the admin is not an admin, or the
   *                               users are not in the same household
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

    if (adminHousehold == null || userHousehold == null || !adminHousehold.getId()
        .equals(userHousehold.getId())) {
      throw new IllegalStateException("User is not in the same household");
    }

    // Check if user is already an admin
    if (isUserHouseholdAdmin(user)) {
      throw new IllegalStateException("User is already a household admin");
    }

    // Create a new household admin
    HouseholdAdmin householdAdmin = new HouseholdAdmin(user, adminHousehold);
    householdAdminRepository.save(householdAdmin);
  }

  /**
   * Removes a member from the household. Only household admins can remove members. Cannot remove
   * the last admin of a household.
   *
   * @param adminEmail the email of the admin removing the member
   * @param memberId   the ID of the member to remove
   * @throws IllegalStateException if the admin is not found, is not an admin, the member is not
   *                               found, or is the last admin
   */
  @Transactional
  public void removeMemberFromHousehold(String adminEmail, Integer memberId) {
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
    updatePopulationCount(household);
  }

  /**
   * Converts a Household entity to a HouseholdDto. This is a helper method used internally to
   * transform entities to DTOs.
   *
   * @param household the household entity to convert
   * @return the household DTO with member information
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
   * Updates the population count for a household by calculating the total number of members.
   * This includes both registered users and empty household members (excluding pets).
   * The count is persisted to the database.
   *
   * @param household the household entity to update the population count for
   * @throws IllegalArgumentException if the household parameter is null
   */
  public void updatePopulationCount(Household household) {
    int userCount = userRepository.findByHousehold(household).size();
    int emptyCount = (int) emptyHouseholdMemberRepository.findByHousehold(household)
        .stream()
        .filter(member -> member.getType() != null && !member.getType().equalsIgnoreCase("pet"))
        .count();
    household.setPopulationCount(userCount + emptyCount);
    householdRepository.save(household);
  }
}
