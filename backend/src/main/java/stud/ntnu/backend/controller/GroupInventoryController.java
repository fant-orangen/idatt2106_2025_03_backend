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
import stud.ntnu.backend.service.GroupService;
import java.util.Objects;

@RestController
@RequestMapping("/api/groups/inventory")
public class GroupInventoryController {

  private final GroupService groupService;

  @Autowired
  public GroupInventoryController(GroupService groupService) {
    this.groupService = groupService;
  }

  /**
   * Get a paginated list of all product types with batches contributed to the given group and category.
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
    Page<ProductTypeDto> page = groupService.getContributedProductTypes(request.getGroupId(),
        request.getCategory(), pageable);
    return ResponseEntity.ok(page);
  }
}
