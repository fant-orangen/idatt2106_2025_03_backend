package stud.ntnu.backend.service.group;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.inventory.InventoryService;
import stud.ntnu.backend.repository.household.InvitationRepository;

/**
 * Service for managing groups. Handles creation, retrieval, updating, and deletion of groups.
 * Provides functionality for managing group memberships and household associations.
 */
@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupMembershipRepository groupMembershipRepository;
  private final InventoryService inventoryService;
  private final HouseholdAdminRepository householdAdminRepository;
  private final UserRepository userRepository;
  private final ProductTypeRepository productTypeRepository;
  private final GroupInventoryContributionRepository groupInventoryContributionRepository;
  private final HouseholdRepository householdRepository;
  private final InvitationRepository invitationRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param groupRepository                      repository for group operations
   * @param groupMembershipRepository            repository for group membership operations
   * @param inventoryService                     service for inventory operations
   * @param householdAdminRepository             repository for household admin operations
   * @param userRepository                       repository for user operations
   * @param productTypeRepository                repository for product type operations
   * @param groupInventoryContributionRepository repository for group inventory contribution operations
   * @param householdRepository                  repository for household operations
   * @param invitationRepository                 repository for invitation operations
   */
  @Autowired
  public GroupService(GroupRepository groupRepository,
      GroupMembershipRepository groupMembershipRepository, InventoryService inventoryService,
      HouseholdAdminRepository householdAdminRepository, UserRepository userRepository,
      ProductTypeRepository productTypeRepository,
      GroupInventoryContributionRepository groupInventoryContributionRepository,
      HouseholdRepository householdRepository,
      InvitationRepository invitationRepository) {
    this.groupRepository = groupRepository;
    this.groupMembershipRepository = groupMembershipRepository;
    this.inventoryService = inventoryService;
    this.householdAdminRepository = householdAdminRepository;
    this.userRepository = userRepository;
    this.productTypeRepository = productTypeRepository;
    this.groupInventoryContributionRepository = groupInventoryContributionRepository;
    this.householdRepository = householdRepository;
    this.invitationRepository = invitationRepository;
  }

  /**
   * Retrieves all groups in the system.
   *
   * @return list of all groups
   */
  public List<Group> getAllGroups() {
    return groupRepository.findAll();
  }

  /**
   * Retrieves a group by its ID.
   *
   * @param id the ID of the group to retrieve
   * @return an Optional containing the group if found, empty otherwise
   */
  public Optional<Group> getGroupById(Integer id) {
    return groupRepository.findById(id);
  }

  /**
   * Saves a group to the database.
   *
   * @param group the group to save
   * @return the saved group with updated fields
   */
  public Group saveGroup(Group group) {
    return groupRepository.save(group);
  }

  /**
   * Deletes a group by its ID.
   *
   * @param id the ID of the group to delete
   */
  public void deleteGroup(Integer id) {
    groupRepository.deleteById(id);
  }

  /**
   * Gets the group associated with the current user's household.
   *
   * @param email the user's email address
   * @return GroupSummaryDto containing group details, or null if no group is found
   */
  public GroupSummaryDto getCurrentUserGroup(String email) {
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    Optional<GroupMembership> membershipOpt = groupMembershipRepository.findCurrentByHouseholdId(
        householdId, LocalDateTime.now());
    if (membershipOpt.isEmpty()) {
      return null;
    }
    Group group = membershipOpt.get().getGroup();
    GroupSummaryDto dto = new GroupSummaryDto();
    dto.setId(group.getId());
    dto.setName(group.getName());
    dto.setCreatedAt(group.getCreatedAt());
    return dto;
  }

  /**
   * Retrieves a paginated list of groups associated with the current user's household.
   *
   * @param email    the user's email address
   * @param pageable pagination information
   * @return a Page of GroupSummaryDto objects
   */
  public Page<GroupSummaryDto> getCurrentUserGroups(String email, Pageable pageable) {
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    Page<GroupMembership> memberships = groupMembershipRepository.findAllCurrentByHouseholdIdAndGroupStatus(
        householdId, LocalDateTime.now(), pageable);
    return memberships.map(membership -> {
      Group group = membership.getGroup();
      GroupSummaryDto dto = new GroupSummaryDto();
      dto.setId(group.getId());
      dto.setName(group.getName());
      dto.setCreatedAt(group.getCreatedAt());
      return dto;
    });
  }

  /**
   * Removes a household from a group if the user is a household admin.
   *
   * @param email   the email of the user requesting the removal
   * @param groupId the ID of the group to remove the household from
   * @return true if the removal was successful, false otherwise
   */
  @Transactional
  public boolean removeHouseholdFromGroup(String email, Integer groupId) {
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null || !householdAdminRepository.existsByUser(user)) {
      return false;
    }
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    Optional<GroupMembership> membershipOpt = groupMembershipRepository.findCurrentByHouseholdIdAndGroupId(
        householdId, groupId, LocalDateTime.now());
    if (membershipOpt.isEmpty()) {
      return false;
    }
    GroupMembership membership = membershipOpt.get();
    membership.setLeftAt(LocalDateTime.now());
    groupMembershipRepository.save(membership);

    groupInventoryContributionRepository.deleteByGroupIdAndHouseholdId(groupId, householdId);
    
    List<GroupMembership> remainingMemberships = groupMembershipRepository.findAllCurrentByGroupId(
        groupId, LocalDateTime.now());
    if (remainingMemberships.isEmpty()) {
      Group group = membership.getGroup();
      group.setStatus(Group.GroupStatus.archived);
      groupRepository.save(group);
    }
    
    return true;
  }

  /**
   * Retrieves all current households in a specific group.
   *
   * @param groupId the ID of the group
   * @return list of HouseholdDto objects representing the households in the group
   */
  public List<HouseholdDto> getCurrentHouseholdsInGroup(Integer groupId) {
    List<GroupMembership> memberships = groupMembershipRepository.findAllCurrentByGroupId(groupId,
        LocalDateTime.now());
    return memberships.stream().map(membership -> {
      Household h = membership.getHousehold();
      HouseholdDto dto = new HouseholdDto();
      dto.setId(h.getId());
      dto.setName(h.getName());
      dto.setAddress(h.getAddress());
      dto.setPopulationCount(h.getPopulationCount());
      dto.setLatitude(h.getLatitude());
      dto.setLongitude(h.getLongitude());
      dto.setMembers(null);
      return dto;
    }).collect(Collectors.toList());
  }

  /**
   * Gets the household ID associated with a user's email.
   *
   * @param email the user's email address
   * @return the household ID, or null if not found
   */
  public Integer getHouseholdIdByUserEmail(String email) {
    return inventoryService.getHouseholdIdByUserEmail(email);
  }

  /**
   * Checks if the user (by email) is a member of any household in the given group.
   *
   * @param groupId the ID of the group to check
   * @param email   the user's email address
   * @return true if the user is a member of a household in the group, false otherwise
   */
  public boolean isUserMemberOfGroup(Integer groupId, String email) {
    Integer userHouseholdId = getHouseholdIdByUserEmail(email);
    List<HouseholdDto> households = getCurrentHouseholdsInGroup(groupId);
    return households.stream()
        .anyMatch(h -> h.getId() != null && h.getId().equals(userHouseholdId));
  }

  /**
   * Creates a new group if the user is a household admin.
   *
   * @param name  the name of the group to create
   * @param email the email of the user creating the group
   * @return true if the group was created successfully, false if the user is not a household admin
   */
  @Transactional
  public boolean createGroup(String name, String email) {
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null || !householdAdminRepository.existsByUser(user)) {
      return false;
    }

    Group group = new Group(name, user);
    group = groupRepository.save(group);

    // Get the household ID of the current user
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    if (householdId == null) {
      return false;
    }

    // Get the household entity
    Optional<Household> householdOpt = householdRepository.findById(householdId);
    if (householdOpt.isEmpty()) {
      return false;
    }

    // Create and save the membership
    GroupMembership membership = new GroupMembership(group, householdOpt.get(), user);
    groupMembershipRepository.save(membership);

    return true;
  }

  /**
   * Creates an invitation for a household to join a group.
   * The invitation will expire in one month.
   *
   * @param householdName the name of the household to invite
   * @param groupId the ID of the group to invite to
   * @param inviterEmail the email of the user sending the invitation
   * @return true if invitation was created successfully, false if household or group not found
   */
  @Transactional
  public boolean inviteHouseholdToGroup(String householdName, Integer groupId, String inviterEmail) {
    // Find the inviter user
    User inviter = userRepository.findByEmail(inviterEmail)
        .orElseThrow(() -> new IllegalStateException("Inviter not found"));

    // Find the group
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new IllegalStateException("Group not found"));

    // Find the household by name
    Household targetHousehold = householdRepository.findByName(householdName)
        .orElseThrow(() -> new IllegalStateException("Household not found"));

    // Check if the household is already a member of the group
    boolean isAlreadyMember = groupMembershipRepository
        .findCurrentByHouseholdIdAndGroupId(targetHousehold.getId(), groupId, LocalDateTime.now())
        .isPresent();
    
    if (isAlreadyMember) {
      throw new IllegalStateException("Household is already a member of this group");
    }

    // Check if there's already a pending invitation
    boolean hasPendingInvitation = groupMembershipRepository
        .existsByGroupIdAndHouseholdIdAndInvitationNotExpired(groupId, targetHousehold.getId(), LocalDateTime.now());
    
    if (hasPendingInvitation) {
      throw new IllegalStateException("There is already a pending invitation for this household");
    }

    // Create the invitation
    Invitation invitation = new Invitation(
        inviter,
        inviterEmail,
        group,
        LocalDateTime.now().plus(1, ChronoUnit.MONTHS)
    );

    // Save the invitation
    invitationRepository.save(invitation);

    return true;
  }
}