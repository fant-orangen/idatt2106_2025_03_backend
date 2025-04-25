package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.inventory.*;
import stud.ntnu.backend.service.InventoryService;
import stud.ntnu.backend.service.ProductService;

import java.util.List;

/**
 * Handles inventory management at the household level. Includes listing, adding, editing, or
 * removing stock items, tracking expiration dates, and computing preparedness grade.
 * <p>
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

  private final InventoryService inventoryService;
  private final ProductService productService;
  private final Logger log = LoggerFactory.getLogger(InventoryController.class);

  public InventoryController(InventoryService inventoryService, ProductService productService) {
    this.inventoryService = inventoryService;
    this.productService = productService;
  }

  /**
   * Get all product types for the current household.
   *
   * @param pageable pagination information
   * @return a paginated list of product types
   */
  @GetMapping("/product-types")
  public ResponseEntity<Page<ProductTypeDto>> getAllProductTypes(Pageable pageable) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Get product types for the household
      Page<ProductTypeDto> productTypes = productService.getProductTypesByHousehold(householdId, pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      log.error("Error getting product types", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all product batches for a given product type.
   *
   * @param productTypeId the ID of the product type
   * @param pageable      pagination information
   * @return a paginated list of product batches
   */
  @GetMapping("/product-types/{productTypeId}/batches")
  public ResponseEntity<Page<ProductBatchDto>> getProductBatchesByProductType(
      @PathVariable Integer productTypeId,
      Pageable pageable) {
    Page<ProductBatchDto> productBatches = productService.getProductBatchesByProductType(
        productTypeId, pageable);
    return ResponseEntity.ok(productBatches);
  }

  /**
   * Add a new type of product.
   *
   * @param createDto the DTO containing the product type information
   * @return 200 OK
   */
  @PostMapping("/product-types")
  public ResponseEntity<?> createProductType(@Valid @RequestBody ProductTypeCreateDto createDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Set the household ID in the DTO
      createDto.setHouseholdId(householdId);

      productService.createProductType(createDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error creating product type", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Add a new batch to an existing product.
   * Validates that the product type belongs to the user's household.
   * TODO: Untested!
   * @param createDto the DTO containing the product batch information
   * @return 200 OK
   */
  @PostMapping("/product-batches")
  public ResponseEntity<?> createProductBatch(@Valid @RequestBody ProductBatchCreateDto createDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Validate that the product type belongs to the user's household
      productService.createProductBatch(createDto, householdId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error creating product batch", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Reduce the number of units in an existing batch.
   *
   * @param batchId   the ID of the batch
   * @param updateDto the DTO containing the update information
   * @return 200 OK
   */
  @PutMapping("/product-batches/{batchId}")
  public ResponseEntity<?> updateProductBatch(
      @PathVariable Integer batchId,
      @Valid @RequestBody ProductBatchUpdateDto updateDto) {
    try {
      productService.updateProductBatch(batchId, updateDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error updating product batch", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Delete an existing batch.
   * TODO: UNTESTED!
   * @param batchId the ID of the batch
   * @return 200 OK
   */
  @DeleteMapping("/product-batches/{batchId}")
  public ResponseEntity<?> deleteProductBatch(@PathVariable Integer batchId) {
    try {
      productService.deleteProductBatch(batchId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error deleting product batch", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
