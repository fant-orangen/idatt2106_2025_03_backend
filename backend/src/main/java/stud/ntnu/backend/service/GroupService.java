package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.service.InventoryService;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Service for managing groups. Handles creation, retrieval, updating, and deletion of groups.
 */
@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupMembershipRepository groupMembershipRepository;
  private final InventoryService inventoryService;

  /**
   * Constructor for dependency injection.
   *
   * @param groupRepository           repository for group operations
   * @param groupMembershipRepository repository for group membership operations
   * @param inventoryService          service for inventory operations
   */
  @Autowired
  public GroupService(GroupRepository groupRepository,
      GroupMembershipRepository groupMembershipRepository, InventoryService inventoryService) {
    this.groupRepository = groupRepository;
    this.groupMembershipRepository = groupMembershipRepository;
    this.inventoryService = inventoryService;
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
}