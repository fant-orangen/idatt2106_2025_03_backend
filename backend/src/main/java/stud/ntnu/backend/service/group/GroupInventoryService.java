package stud.ntnu.backend.service.group;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.SearchUtil;

/**
 * Service for managing group inventory contributions and shared resources. This service handles: -
 * Creation and management of group inventory contributions - Product type and batch management
 * within groups - Access control for group inventory operations - Search and retrieval of shared
 * resources
 */
@Service
@RequiredArgsConstructor
public class GroupInventoryService {

  private final GroupInventoryContributionRepository groupInventoryContributionRepository;
  private final ProductTypeRepository productTypeRepository;
  private final GroupRepository groupRepository;
  private final ProductBatchRepository productBatchRepository;
  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;
  private final SearchUtil searchUtil;

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
   * @param groupId  The ID of the group
   * @param email    The email of the current user
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  public Page<ProductTypeDto> getContributedProductTypes(Integer groupId, String email,
      Pageable pageable) {
    // Get user's household
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    var household = user.getHousehold();
    if (household == null) {
      throw new IllegalArgumentException("User is not in a household");
    }

    // Validate that the user's household is a member of the group
    boolean isMember = groupRepository.existsByIdAndMemberHouseholds_Id(groupId, household.getId());
    if (!isMember) {
      throw new IllegalArgumentException("User's household is not a member of this group");
    }

    // Get all product types contributed to the group by any household
    Page<ProductType> page = productTypeRepository.findContributedProductTypesByGroup(groupId,
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

  /**
   * Retrieves all product batches of a specific type that have been contributed to a group.
   *
   * @param groupId       The ID of the group to search within
   * @param productTypeId The ID of the product type to filter by
   * @param pageable      pagination information
   * @return a page of ProductBatchDto containing the contributed batches
   */
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
   * Removes a contributed product batch from a group if it belongs to the user's household.
   *
   * @param productBatchId the id of the product batch
   * @param email          the email of the user requesting the removal
   * @return true if removed, false if not found
   * @throws IllegalArgumentException if user not found or not in a household
   * @throws SecurityException        if the batch doesn't belong to user's household
   */
  public boolean removeContributedBatch(Integer productBatchId, String email) {
    if (productBatchId == null || email == null) {
      throw new IllegalArgumentException("Product batch ID and email cannot be null");
    }

    // Get user's household
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    var household = user.getHousehold();
    if (household == null) {
      throw new IllegalArgumentException("User is not in a household");
    }

    // Find the contribution for this batch
    var contribution = groupInventoryContributionRepository.findByProductBatchId(productBatchId)
        .orElse(null);

    // Check if contribution exists
    if (contribution == null) {
      return false;
    }

    // Verify the contribution belongs to user's household
    if (!contribution.getHousehold().getId().equals(household.getId())) {
      throw new SecurityException("Not authorized to remove this contribution");
    }

    groupInventoryContributionRepository.delete(contribution);
    return true;
  }

  /**
   * Adds a product batch to a group's shared inventory. Performs several validation checks: -
   * Verifies that the batch exists - Verifies that the group exists - Checks if the batch is
   * already contributed to another group - Verifies that the user has permission to contribute the
   * batch - Ensures the batch belongs to the user's household
   *
   * @param batchId The ID of the product batch to add
   * @param groupId The ID of the group to add the batch to
   * @param email   The email of the user performing the action
   * @return true if the batch was successfully added, false if any validation check fails
   */
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
   * Search for product types that have at least one batch contributed to the specified group by the
   * user's household. First searches all product types by name using SearchUtil, then filters for
   * those contributed to the group.
   *
   * @param groupId  The ID of the group to search within
   * @param search   The search term to filter product types by name
   * @param email    The email of the current user
   * @param pageable pagination information
   * @return a page of ProductTypeDto matching the search criteria
   * @throws IllegalArgumentException if the user is not found, not in a household, or not a member
   *                                  of the group
   */
  public Page<ProductTypeDto> searchContributedProductTypes(Integer groupId, String search,
      String email, Pageable pageable) {
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

    // Get list of product type IDs that are contributed to this group
    List<Integer> contributedTypeIds = groupInventoryContributionRepository.findProductTypeIdsContributedToGroup(
        groupId);

    // If no contributions exist, return empty page
    if (contributedTypeIds.isEmpty()) {
      return Page.empty(pageable);
    }

    // First use SearchUtil to search among all product types by name
    Page<ProductType> searchResults = searchUtil.searchByDescription(
        ProductType.class,
        "name",
        search != null ? search : "",
        pageable
    );

    // Filter the search results to only include product types that are contributed to the group
    List<ProductType> filteredResults = searchResults.getContent().stream()
        .filter(pt -> contributedTypeIds.contains(pt.getId()))
        .collect(Collectors.toList());

    // Create a new page with the filtered results
    return new PageImpl<>(
        filteredResults.stream()
            .map(pt -> new ProductTypeDto(
                pt.getId(),
                pt.getHousehold() != null ? pt.getHousehold().getId() : null,
                pt.getName(),
                pt.getUnit(),
                pt.getCaloriesPerUnit(),
                pt.getCategory()
            ))
            .collect(Collectors.toList()),
        pageable,
        filteredResults.size()
    );
  }

  /**
   * Checks if a product batch is contributed to any group by the user's household.
   *
   * @param productBatchId the ID of the product batch to check
   * @param email          the email of the user making the request
   * @return true if the batch exists, belongs to the user's household, and is contributed to at
   * least one group
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

  /**
   * Get the total number of units of a specific product type that have been contributed to a group
   * across all households.
   *
   * @param productTypeId The ID of the product type
   * @param groupId       The ID of the group
   * @return The total number of units contributed to the group
   * @throws IllegalArgumentException if the product type or group doesn't exist
   */
  public Integer getTotalUnitsForProductType(Integer productTypeId, Integer groupId) {
    // Validate inputs
    if (productTypeId == null || groupId == null) {
      throw new IllegalArgumentException("Product type ID and group ID cannot be null");
    }

    // Validate that the product type exists
    if (!productTypeRepository.existsById(productTypeId)) {
      throw new IllegalArgumentException("Product type not found");
    }

    // Validate that the group exists
    if (!groupRepository.existsById(groupId)) {
      throw new IllegalArgumentException("Group not found");
    }

    // Get the sum of units from the repository across all households
    return groupInventoryContributionRepository.sumTotalUnitsForProductTypeAndGroup(
        productTypeId,
        groupId);
  }
}