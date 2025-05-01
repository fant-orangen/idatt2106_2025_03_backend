package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.model.household.Household;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.model.inventory.ProductBatch;

/**
 * Service for managing groups. Handles creation, retrieval, updating, and deletion of groups.
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

  /**
   * Constructor for dependency injection.
   *
   * @param groupRepository           repository for group operations
   * @param groupMembershipRepository repository for group membership operations
   * @param inventoryService          service for inventory operations
   * @param householdAdminRepository  repository for household admin operations
   * @param userRepository          repository for user operations
   * @param productTypeRepository     repository for product type operations
   * @param groupInventoryContributionRepository repository for group inventory contribution operations
   */
  @Autowired
  public GroupService(GroupRepository groupRepository,
      GroupMembershipRepository groupMembershipRepository, InventoryService inventoryService,
      HouseholdAdminRepository householdAdminRepository, UserRepository userRepository,
      ProductTypeRepository productTypeRepository, GroupInventoryContributionRepository groupInventoryContributionRepository) {
    this.groupRepository = groupRepository;
    this.groupMembershipRepository = groupMembershipRepository;
    this.inventoryService = inventoryService;
    this.householdAdminRepository = householdAdminRepository;
    this.userRepository = userRepository;
    this.productTypeRepository = productTypeRepository;
    this.groupInventoryContributionRepository = groupInventoryContributionRepository;
  }

  /**
   * Retrieves all groups.
   *
   * @return list of all groups
   */
  public List<Group> getAllGroups() {
    return groupRepository.findAll();
  }

  /**
   * Retrieves a group by its ID.
   *
   * @param id the ID of the group
   * @return an Optional containing the group if found
   */
  public Optional<Group> getGroupById(Integer id) {
    return groupRepository.findById(id);
  }

  /**
   * Saves a group.
   *
   * @param group the group to save
   * @return the saved group
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
   * @param email the user's email
   * @return GroupSummaryDto or null if not found
   */
  public GroupSummaryDto getCurrentUserGroup(String email) {
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    Optional<GroupMembership> membershipOpt = groupMembershipRepository.findCurrentByHouseholdId(householdId, LocalDateTime.now());
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

  public Page<GroupSummaryDto> getCurrentUserGroups(String email, Pageable pageable) {
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    Page<GroupMembership> memberships = groupMembershipRepository.findAllCurrentByHouseholdId(householdId, LocalDateTime.now(), pageable);
    return memberships.map(membership -> {
      Group group = membership.getGroup();
      GroupSummaryDto dto = new GroupSummaryDto();
      dto.setId(group.getId());
      dto.setName(group.getName());
      dto.setCreatedAt(group.getCreatedAt());
      return dto;
    });
  }

  public boolean removeHouseholdFromGroup(String email, Integer groupId) {
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null || !householdAdminRepository.existsByUser(user)) {
      return false;
    }
    Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
    Optional<GroupMembership> membershipOpt = groupMembershipRepository.findCurrentByHouseholdId(householdId, LocalDateTime.now())
      .filter(m -> m.getGroup().getId().equals(groupId));
    if (membershipOpt.isEmpty()) {
      return false;
    }
    GroupMembership membership = membershipOpt.get();
    membership.setLeftAt(LocalDateTime.now());
    groupMembershipRepository.save(membership);
    return true;
  }

  public List<HouseholdDto> getCurrentHouseholdsInGroup(Integer groupId) {
    List<GroupMembership> memberships = groupMembershipRepository.findAllCurrentByGroupId(groupId, LocalDateTime.now());
    return memberships.stream().map(membership -> {
      Household h = membership.getHousehold();
      HouseholdDto dto = new HouseholdDto();
      dto.setId(h.getId());
      dto.setName(h.getName());
      dto.setAddress(h.getAddress());
      dto.setPopulationCount(h.getPopulationCount());
      dto.setLatitude(h.getLatitude());
      dto.setLongitude(h.getLongitude());
      dto.setMembers(null); // or Collections.emptyList() for summary
      return dto;
    }).collect(Collectors.toList());
  }

  
}