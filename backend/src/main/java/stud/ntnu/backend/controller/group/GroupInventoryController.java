package stud.ntnu.backend.controller.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import stud.ntnu.backend.dto.inventory.AddBatchToGroupRequestDto;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.service.group.GroupInventoryService;
import stud.ntnu.backend.service.group.GroupService;

/**
 * REST controller for managing group inventory operations.
 * <p>
 * This controller handles all operations related to group inventory management, including: -
 * Contributing product batches to group inventories - Viewing and searching contributed product
 * types - Managing contributed batches - Calculating total units contributed
 * <p>
 * All endpoints require user authentication and operate on behalf of the authenticated user's
 * household.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Group Inventory", description = "Operations for managing group inventory, including contributing batches, viewing products, and calculating totals")
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
   * Retrieves a paginated list of all product types that have batches contributed to the specified
   * group.
   *
   * @param groupId   The ID of the group to search within
   * @param pageable  Pagination parameters (page number, size, sorting)
   * @param principal The authenticated user making the request
   * @return ResponseEntity containing a page of ProductTypeDto objects, or 400 Bad Request if
   * groupId is null
   */
  @Operation(summary = "Get contributed product types", description = "Retrieves a paginated list of all product types that have batches contributed to the specified group.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - groupId is null")
  })
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
   * Retrieves all product batches of a specific type that are currently contributed to the
   * specified group.
   *
   * @param groupId       The ID of the group to search within
   * @param productTypeId The ID of the product type to filter by
   * @param pageable      Pagination parameters (page number, size, sorting)
   * @return ResponseEntity containing a page of ProductBatchDto objects, or 400 Bad Request if
   * parameters are invalid
   */
  @Operation(summary = "Get contributed product batches", description = "Retrieves all product batches of a specific type that are currently contributed to the specified group.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved product batches", 
          content = @Content(schema = @Schema(implementation = ProductBatchDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters")
  })
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
   * Removes a contributed product batch from a group. Only allowed if the batch was contributed by
   * the user's household.
   *
   * @param productBatchId The ID of the product batch to remove
   * @param principal      The authenticated user making the request
   * @return ResponseEntity with: - 200 OK if successfully removed - 400 Bad Request if
   * productBatchId is null - 403 Forbidden if user is not authorized - 404 Not Found if batch
   * doesn't exist
   */
  @Operation(summary = "Remove contributed batch", description = "Removes a contributed product batch from a group. Only allowed if the batch was contributed by the user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully removed batch"),
      @ApiResponse(responseCode = "400", description = "Bad request - productBatchId is null", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not authorized", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "404", description = "Batch not found")
  })
  @PatchMapping("/user/groups/inventory/product-batches/{productBatchId}")
  public ResponseEntity<?> removeContributedBatch(
      @PathVariable Integer productBatchId,
      Principal principal) {
    if (Objects.isNull(productBatchId)) {
      return ResponseEntity.badRequest().build();
    }
    try {
      boolean removed = groupInventoryService.removeContributedBatch(productBatchId,
          principal.getName());
      if (!removed) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok().build();
    } catch (SecurityException e) {
      return ResponseEntity.status(403).body("Not authorized to remove this contribution");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Adds a product batch to a group's inventory. Only allowed if the user's household is a member
   * of the group.
   *
   * @param request   The request containing batchId and groupId
   * @param principal The authenticated user making the request
   * @return ResponseEntity with: - 200 OK if successfully added - 403 Forbidden if user is not a
   * group member - 404 Not Found if request parameters are null - 409 Conflict if batch cannot be
   * added
   */
  @Operation(summary = "Add batch to group", description = "Adds a product batch to a group's inventory. Only allowed if the user's household is a member of the group.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully added batch to group"),
      @ApiResponse(responseCode = "403", description = "Forbidden - user is not a group member", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "404", description = "Request parameters are null"),
      @ApiResponse(responseCode = "409", description = "Conflict - batch cannot be added", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
   * Searches for product types that have batches contributed to the specified group by the current
   * user's household.
   *
   * @param groupId   The ID of the group to search within
   * @param search    The search term to filter product types by name
   * @param pageable  Pagination parameters (page number, size, sorting)
   * @param principal The authenticated user making the request
   * @return ResponseEntity containing a page of matching ProductTypeDto objects, or 400 Bad Request
   * if parameters are invalid
   */
  @Operation(summary = "Search contributed product types", description = "Searches for product types that have batches contributed to the specified group by the current user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved matching product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters")
  })
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
   * Checks if a specific product batch is currently contributed to any group by the user's
   * household.
   *
   * @param productBatchId The ID of the product batch to check
   * @param principal      The authenticated user making the request
   * @return ResponseEntity containing: - true if the batch is contributed to a group - false if not
   * contributed or if an error occurs - 400 Bad Request if productBatchId is null - 403 Forbidden
   * if an error occurs during the check
   */
  @Operation(summary = "Check batch contribution status", description = "Checks if a specific product batch is currently contributed to any group by the user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully checked contribution status", 
          content = @Content(schema = @Schema(type = "boolean"))),
      @ApiResponse(responseCode = "400", description = "Bad request - productBatchId is null"),
      @ApiResponse(responseCode = "403", description = "Forbidden - error during check")
  })
  @GetMapping("user/groups/inventory/{productBatchId}/contributed")
  public ResponseEntity<Boolean> isContributedToGroup(@PathVariable Integer productBatchId,
      Principal principal) {
    if (Objects.isNull(productBatchId)) {
      return ResponseEntity.badRequest().build();
    }
    String email = principal.getName();
    try {
      boolean isContributed = groupInventoryService.isContributedToGroup(productBatchId, email);
      return ResponseEntity.ok(isContributed);
    } catch (Exception e) {
      return ResponseEntity.status(403).body(false);
    }
  }

  /**
   * Calculates the total number of units of a specific product type contributed to a group across
   * all households.
   *
   * @param productTypeId The ID of the product type to calculate totals for
   * @param groupId       The ID of the group to calculate totals within
   * @return ResponseEntity containing: - The total number of units if successful - 0 if an error
   * occurs - 400 Bad Request if parameters are invalid
   */
  @Operation(summary = "Get total units for product type", description = "Calculates the total number of units of a specific product type contributed to a group across all households.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully calculated total units", 
          content = @Content(schema = @Schema(type = "integer"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters")
  })
  @GetMapping("user/groups/inventory/product-types/sum")
  public ResponseEntity<Integer> getTotalUnitsForProductType(
      @RequestParam Integer productTypeId,
      @RequestParam Integer groupId) {
    if (Objects.isNull(productTypeId) || Objects.isNull(groupId)) {
      return ResponseEntity.badRequest().build();
    }
    try {
      Integer totalUnits = groupInventoryService.getTotalUnitsForProductType(
          productTypeId,
          groupId);
      return ResponseEntity.ok(totalUnits);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(0);
    }
  }

}
