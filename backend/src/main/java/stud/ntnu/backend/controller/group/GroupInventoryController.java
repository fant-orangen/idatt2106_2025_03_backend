package stud.ntnu.backend.controller.group;

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
import stud.ntnu.backend.service.group.GroupService;
import stud.ntnu.backend.service.group.GroupInventoryService;
import java.util.Objects;
import java.security.Principal;
import stud.ntnu.backend.dto.inventory.AddBatchToGroupRequestDto;

@RestController
@RequestMapping("/api")
public class GroupInventoryController {

  private final GroupService groupService;
  private final GroupInventoryService groupInventoryService;

  @Autowired
  public GroupInventoryController(GroupService groupService,
      GroupInventoryService groupInventoryService) {
    this.groupService = groupService;
    this.groupInventoryService = groupInventoryService;
  }

  /**
   * Get a paginated list of all product types with batches contributed to the given group.
   *
   * @param groupId The ID of the group
   * @param pageable pagination information
   * @param principal the authenticated user
   * @return a page of ProductTypeDto
   */
  @GetMapping("/user/groups/inventory/product-types")
  public ResponseEntity<Page<ProductTypeDto>> getContributedProductTypes(
      @RequestParam Integer groupId,
      Pageable pageable,
      Principal principal) {
    if (Objects.isNull(groupId)) {
      return ResponseEntity.badRequest().build();
    }
    Page<ProductTypeDto> page = groupInventoryService.getContributedProductTypes(
        groupId,
        principal.getName(),
        pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * Get all product batches for a given product type that are currently contributed to the given
   * group.
   *
   * @param groupId The ID of the group
   * @param productTypeId The ID of the product type
   * @param pageable pagination information
   * @return a paginated list of product batches
   */
  @GetMapping("/user/groups/inventory/product-types/batches")
  public ResponseEntity<Page<ProductBatchDto>> getContributedProductBatchesByType(
      @RequestParam Integer groupId,
      @RequestParam Integer productTypeId,
      Pageable pageable) {
    if (Objects.isNull(groupId) || Objects.isNull(productTypeId)) {
      return ResponseEntity.badRequest().build();
    }
    Page<ProductBatchDto> page = groupInventoryService.getContributedProductBatchesByType(
        groupId, productTypeId, pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * Remove a contributed product batch from a group by ProductBatch id.
   * <p>
   * If the batch is contributed to more than one group, return an error and do not remove it.
   *
   * @param productBatchId the id of the product batch
   * @return 200 OK if removed, 409 Conflict if batch is contributed to more than one group, 404 if
   * not found
   */
  @PatchMapping("/user/groups/inventory/product-batches/{productBatchId}")
  public ResponseEntity<?> removeContributedBatch(@PathVariable Integer productBatchId) {
    int count = groupInventoryService.countGroupContributionsForBatch(productBatchId);
    if (count == 0) {
      return ResponseEntity.notFound().build();
    } else if (count > 1) {
      return ResponseEntity.status(409)
          .body("This product batch is being contributed to more than one group.");
    }
    boolean removed = groupInventoryService.removeContributedBatch(productBatchId);
    if (!removed) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }

  /**
   * Add a product batch to a group. Only allowed if the current user is in a household that is a
   * member of the group.
   *
   * @param request   DTO with batchId and groupId
   * @param principal the authenticated user
   * @return 200 OK if added, 403 if not authorized
   */
  @PostMapping("/user/groups/inventory")
  public ResponseEntity<?> addBatchToGroup(@RequestBody AddBatchToGroupRequestDto request,
      Principal principal) {
    if (request.getBatchId() == null || request.getGroupId() == null) {
      return ResponseEntity.notFound().build();
    }
    String email = principal.getName();
    boolean isMember = groupService.isUserMemberOfGroup(request.getGroupId(), email);
    if (!isMember) {
      return ResponseEntity.status(403).body("You are not a member of this group.");
    }
    boolean added = groupInventoryService.addBatchToGroup(request.getBatchId(),
        request.getGroupId(), email);
    if (!added) {
      return ResponseEntity.status(409).body("Batch could not be added to group.");
    }
    return ResponseEntity.ok().build();
  }

  /**
   * Search for product types that have at least one batch contributed to the specified group by the current user's household.
   *
   * @param groupId The ID of the group to search within
   * @param search The search term to filter product types by name
   * @param pageable pagination information
   * @param principal the authenticated user
   * @return a page of ProductTypeDto matching the search criteria
   */
  @GetMapping("/user/groups/inventory/product-types/search")
  public ResponseEntity<Page<ProductTypeDto>> searchContributedProductTypes(
      @RequestParam Integer groupId,
      @RequestParam String search,
      Pageable pageable,
      Principal principal) {
    if (Objects.isNull(groupId) || Objects.isNull(search)) {
      return ResponseEntity.badRequest().build();
    }
    String email = principal.getName();
    Page<ProductTypeDto> page = groupInventoryService.searchContributedProductTypes(
        groupId,
        search,
        email,
        pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * Check if a product batch is contributed to a group by the current user's household.
   *
   * @param productBatchId the id of the product batch
   * @param principal the authenticated user
   * @return true if the product batch is contributed to a group, false otherwise
   */
  @GetMapping("user/groups/inventory/{productBatchId}/contributed")
  public ResponseEntity<Boolean> isContributedToGroup(@PathVariable Integer productBatchId, Principal principal) {
    if (Objects.isNull(productBatchId)) {
      return ResponseEntity.badRequest().build();
    }
    String email = principal.getName();
    try {
      boolean isContributed = groupInventoryService.isContributedToGroup(productBatchId, email);
      return ResponseEntity.ok(isContributed);
    } catch (Exception e) {
      return ResponseEntity.status(403).body(false); // TODO: Change this status code?
    }
  }
}
