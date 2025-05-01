package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing group inventory contributions. Handles creation, retrieval, updating, and
 * deletion of group inventory contributions.
 */
@Service
public class GroupInventoryService {

  private final GroupInventoryContributionRepository groupInventoryContributionRepository;
  private final ProductTypeRepository productTypeRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param groupInventoryContributionRepository repository for group inventory contribution
   *                                             operations
   * @param productTypeRepository repository for product type operations
   */
  @Autowired
  public GroupInventoryService(GroupInventoryContributionRepository groupInventoryContributionRepository,
                               ProductTypeRepository productTypeRepository) {
    this.groupInventoryContributionRepository = groupInventoryContributionRepository;
    this.productTypeRepository = productTypeRepository;
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

  public Page<ProductTypeDto> getContributedProductTypes(Integer groupId, String category, Pageable pageable) {
    Page<ProductType> page = productTypeRepository.findContributedProductTypesByGroupAndCategory(groupId, category, pageable);
    return page.map(pt -> ProductTypeDto.builder()
      .id(pt.getId())
      .householdId(pt.getHousehold() != null ? pt.getHousehold().getId() : null)
      .name(pt.getName())
      .unit(pt.getUnit())
      .caloriesPerUnit(pt.getCaloriesPerUnit())
      .category(pt.getCategory())
      .build());
  }

  public Page<ProductBatchDto> getContributedProductBatchesByType(Integer groupId, Integer productTypeId, Pageable pageable) {
    Page<ProductBatch> page = groupInventoryContributionRepository.findContributedProductBatchesByGroupAndProductType(groupId, productTypeId, pageable);
    return page.map(batch -> ProductBatchDto.builder()
      .id(batch.getId())
      .productTypeId(batch.getProductType().getId())
      .productTypeName(batch.getProductType().getName())
      .dateAdded(batch.getDateAdded())
      .expirationTime(batch.getExpirationTime())
      .number(batch.getNumber())
      .build());
  }

  /**
   * Counts the number of group inventory contributions for a given product batch.
   *
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
   * Removes a contributed product batch from a group if and only if it is contributed to exactly one group.
   *
   * This logic exists because there isn't supposed to be more than one group per product batch.
   *
   * @param productBatchId the id of the product batch
   * @return true if removed, false otherwise
   */
  public boolean removeContributedBatch(Integer productBatchId) {
    List<GroupInventoryContribution> contributions = groupInventoryContributionRepository.findAll().stream()
      .filter(gic -> gic.getProduct() != null && gic.getProduct().getId().equals(productBatchId))
      .toList();
    if (contributions.size() != 1) {
      return false;
    }
    groupInventoryContributionRepository.delete(contributions.get(0));
    return true;
  }
}