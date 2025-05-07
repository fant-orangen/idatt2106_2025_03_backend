package stud.ntnu.backend.service.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing group inventory contributions. Handles creation, retrieval, updating, and
 * deletion of group inventory contributions.
 */
@Service
public class GroupInventoryService {

  private final GroupInventoryContributionRepository groupInventoryContributionRepository;
  private final ProductTypeRepository productTypeRepository;
  private final GroupRepository groupRepository;
  private final ProductBatchRepository productBatchRepository;
  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;
  private final SearchUtil searchUtil;
  private final Logger logger = LoggerFactory.getLogger(GroupInventoryService.class);

  /**
   * Constructor for dependency injection.
   *
   * @param groupInventoryContributionRepository repository for group inventory contribution
   *                                             operations
   * @param productTypeRepository                repository for product type operations
   * @param groupRepository                      repository for group operations
   * @param productBatchRepository               repository for product batch operations
   * @param householdRepository                  repository for household operations
   * @param userRepository                       repository for user operations
   * @param searchUtil                           utility for search operations
   */
  @Autowired
  public GroupInventoryService(
      GroupInventoryContributionRepository groupInventoryContributionRepository,
      ProductTypeRepository productTypeRepository,
      GroupRepository groupRepository,
      ProductBatchRepository productBatchRepository,
      HouseholdRepository householdRepository,
      UserRepository userRepository,
      SearchUtil searchUtil) {
    this.groupInventoryContributionRepository = groupInventoryContributionRepository;
    this.productTypeRepository = productTypeRepository;
    this.groupRepository = groupRepository;
    this.productBatchRepository = productBatchRepository;
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
    this.searchUtil = searchUtil;
  }

  /**
   * Retrieves all group inventory contributions.
   *
   * @return list of all group inventory contributions
   */
  public List<GroupInventoryContribution> getAllContributions() {
    return groupInventoryContributionRepository.findAll();
  }

  /**
   * Retrieves a group inventory contribution by its ID.
   *
   * @param id the ID of the group inventory contribution
   * @return an Optional containing the group inventory contribution if found
   */
  public Optional<GroupInventoryContribution> getContributionById(Integer id) {
    return groupInventoryContributionRepository.findById(id);
  }

  /**
   * Saves a group inventory contribution.
   *
   * @param contribution the group inventory contribution to save
   * @return the saved group inventory contribution
   */
  public GroupInventoryContribution saveContribution(GroupInventoryContribution contribution) {
    return groupInventoryContributionRepository.save(contribution);
  }

  /**
   * Deletes a group inventory contribution by its ID.
   *
   * @param id the ID of the group inventory contribution to delete
   */
  public void deleteContribution(Integer id) {
    groupInventoryContributionRepository.deleteById(id);
  }

  /**
   * Get all product types that have batches contributed to the specified group.
   *
   * @param groupId The ID of the group
   * @param email The email of the current user
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  public Page<ProductTypeDto> getContributedProductTypes(Integer groupId, String email, Pageable pageable) {
    // Get user's household
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    var household = user.getHousehold();
    if (household == null) {
        throw new IllegalArgumentException("User is not in a household");
    }

    Page<ProductType> page = productTypeRepository.findContributedProductTypesByGroup(
        groupId,
        household.getId(),
        pageable);
    return page.map(pt -> new ProductTypeDto(
        pt.getId(),
        pt.getHousehold() != null ? pt.getHousehold().getId() : null,
        pt.getName(),
        pt.getUnit(),
        pt.getCaloriesPerUnit(),
        pt.getCategory()
    ));
  }

  public Page<ProductBatchDto> getContributedProductBatchesByType(Integer groupId,
      Integer productTypeId, Pageable pageable) {
    Page<ProductBatch> page = groupInventoryContributionRepository.findContributedProductBatchesByGroupAndProductType(
        groupId, productTypeId, pageable);
    return page.map(batch -> new ProductBatchDto(
        batch.getId(),
        batch.getProductType().getId(),
        batch.getProductType().getName(),
        batch.getDateAdded(),
        batch.getExpirationTime(),
        batch.getNumber()
    ));
  }

  /**
   * Counts the number of group inventory contributions for a given product batch.
   * <p>
   * This logic exists because there isn't supposed to be more than one group per product batch.
   *
   * @param productBatchId the id of the product batch
   * @return the number of group inventory contributions for the batch
   */
  public int countGroupContributionsForBatch(Integer productBatchId) {
    return (int) groupInventoryContributionRepository.findAll().stream()
        .filter(gic -> gic.getProduct() != null && gic.getProduct().getId().equals(productBatchId))
        .count();
  }

  /**
   * Removes a contributed product batch from a group if and only if it is contributed to exactly
   * one group.
   * <p>
   * This logic exists because there isn't supposed to be more than one group per product batch.
   *
   * @param productBatchId the id of the product batch
   * @return true if removed, false otherwise
   */
  public boolean removeContributedBatch(Integer productBatchId) {
    List<GroupInventoryContribution> contributions = groupInventoryContributionRepository.findAll()
        .stream()
        .filter(gic -> gic.getProduct() != null && gic.getProduct().getId().equals(productBatchId))
        .toList();
    if (contributions.size() != 1) {
      return false;
    }
    groupInventoryContributionRepository.delete(contributions.get(0));
    return true;
  }

  public boolean addBatchToGroup(Integer batchId, Integer groupId, String email) {
    // Check if batch exists
    ProductBatch batch = productBatchRepository.findById(batchId).orElse(null);
    if (batch == null) {
      return false;
    }
    // Check if group exists
    Group group = groupRepository.findById(groupId).orElse(null);
    if (group == null) {
      return false;
    }
    // Check if batch is already contributed to any group
    boolean alreadyContributed = groupInventoryContributionRepository.findAll().stream()
        .anyMatch(gic -> gic.getProduct() != null && gic.getProduct().getId().equals(batchId));
    if (alreadyContributed) {
      return false;
    }
    // Find user's household
    Household household = userRepository.findByEmail(email)
        .map(user -> user.getHousehold()).orElse(null);
    if (household == null) {
      return false;
    }
    // Check if the product type of the batch is associated with the user's household
    ProductType productType = batch.getProductType();
    if (productType == null || !household.getId().equals(productType.getHousehold().getId())) {
      return false;
    }
    // Create and save contribution
    GroupInventoryContribution contribution = new GroupInventoryContribution(group, household,
        batch);
    groupInventoryContributionRepository.save(contribution);
    return true;
  }

  /**
   * Search for product types that have at least one batch contributed to the specified group by the user's household.
   *
   * @param groupId The ID of the group to search within
   * @param search The search term to filter product types by name
   * @param email The email of the current user
   * @param pageable pagination information
   * @return a page of ProductTypeDto matching the search criteria
   * @throws IllegalArgumentException if the user is not found, not in a household, or not a member of the group
   */
  public Page<ProductTypeDto> searchContributedProductTypes(Integer groupId, String search, String email, Pageable pageable) {
    // Validate group exists
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    // Get user's household
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    var household = user.getHousehold();
    if (household == null) {
        throw new IllegalArgumentException("User is not in a household");
    }

    // Validate user's household is member of the group
    boolean isMember = groupRepository.existsByIdAndMemberHouseholds_Id(groupId, household.getId());
    if (!isMember) {
        throw new IllegalArgumentException("User's household is not a member of this group");
    }

    // Handle empty search string
    String searchTerm = search != null ? search.trim() : "";

    // Use the repository to get matching product types
    Page<ProductType> searchResults = groupInventoryContributionRepository
        .findContributedProductTypesByGroupAndHouseholdAndNameContaining(
            groupId,
            household.getId(),
            searchTerm,
            pageable
        );

    // Convert to DTOs
    return searchResults.map(pt -> new ProductTypeDto(
        pt.getId(),
        pt.getHousehold() != null ? pt.getHousehold().getId() : null,
        pt.getName(),
        pt.getUnit(),
        pt.getCaloriesPerUnit(),
        pt.getCategory()
    ));
  }

  /**
   * Checks if a product batch is contributed to any group by the user's household.
   * 
   * @param productBatchId the ID of the product batch to check
   * @param email the email of the user making the request
   * @return true if the batch exists, belongs to the user's household, and is contributed to at least one group
   * @throws IllegalArgumentException if the user is not found
   */
  public boolean isContributedToGroup(Integer productBatchId, String email) {
    // Check if batch exists
    ProductBatch batch = productBatchRepository.findById(productBatchId).orElse(null);
    if (batch == null) {
      return false;
    } 
    // Check if the batch is contributed to a group associated with the user's household
    // Get user's household
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    var household = user.getHousehold();
    if (household == null) {
        return false;
    }

    // Check if batch belongs to user's household
    if (!batch.getProductType().getHousehold().getId().equals(household.getId())) {
        throw new IllegalArgumentException("Batch does not belong to user's household");
    }

    // Check if batch is contributed to any group
    return groupInventoryContributionRepository.existsByProductBatchId(productBatchId);
  }
}