package stud.ntnu.backend.controller;

/**
 * Manages contributions to shared group inventories. Enables users to contribute items from their
 * household stock, view shared totals, and manage group-level supply transparency.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.dto.inventory.ContributedProductTypesRequestDto;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ContributedProductBatchesRequestDto;
import stud.ntnu.backend.service.GroupService;
import stud.ntnu.backend.service.GroupInventoryService;
import java.util.Objects;

@RestController
@RequestMapping("/api/groups/inventory")
public class GroupInventoryController {

  private final GroupService groupService;
  private final GroupInventoryService groupInventoryService;

  @Autowired
  public GroupInventoryController(GroupService groupService, GroupInventoryService groupInventoryService) {
    this.groupService = groupService;
    this.groupInventoryService = groupInventoryService;
  }

  /**
   * Get a paginated list of all product types with batches contributed to the given group and category.
   * TODO: untested
   * @param request JSON body with groupId and category
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  @GetMapping("/product-types")
  public ResponseEntity<Page<ProductTypeDto>> getContributedProductTypes(
      @RequestBody ContributedProductTypesRequestDto request, Pageable pageable) {
    if (Objects.isNull(request.getGroupId()) || Objects.isNull(request.getCategory())) {
      return ResponseEntity.badRequest().build();
    }
    Page<ProductTypeDto> page = groupInventoryService.getContributedProductTypes(request.getGroupId(),
        request.getCategory(), pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * Get all product batches for a given product type that are currently contributed to the given group.
   * TODO: untested
   * @param request JSON body with groupId and productTypeId
   * @param pageable pagination information
   * @return a paginated list of product batches
   */
  @GetMapping("/product-types/batches")
  public ResponseEntity<Page<ProductBatchDto>> getContributedProductBatchesByType(
      @RequestBody ContributedProductBatchesRequestDto request, Pageable pageable) {
    if (Objects.isNull(request.getGroupId()) || Objects.isNull(request.getProductTypeId())) {
      return ResponseEntity.badRequest().build();
    }
    Page<ProductBatchDto> page = groupInventoryService.getContributedProductBatchesByType(request.getGroupId(), request.getProductTypeId(), pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * Remove a contributed product batch from a group by ProductBatch id.
   * TODO: untested
   * If the batch is contributed to more than one group, return an error and do not remove it.
   *
   * @param productBatchId the id of the product batch
   * @return 200 OK if removed, 409 Conflict if batch is contributed to more than one group, 404 if not found
   */
  @PatchMapping("/product-batches/{productBatchId}")
  public ResponseEntity<?> removeContributedBatch(@PathVariable Integer productBatchId) {
    int count = groupInventoryService.countGroupContributionsForBatch(productBatchId);
    if (count == 0) {
      return ResponseEntity.notFound().build();
    } else if (count > 1) {
      return ResponseEntity.status(409).body("This product batch is being contributed to more than one group.");
    }
    boolean removed = groupInventoryService.removeContributedBatch(productBatchId);
    if (!removed) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }
}
