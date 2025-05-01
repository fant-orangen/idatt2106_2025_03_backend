package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;
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
  public ResponseEntity<Page<ProductTypeDto>> getAllProductTypes(Pageable pageable,
      Principal principal) {
    String email = principal.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Get product types for the household
      Page<ProductTypeDto> productTypes = productService.getProductTypesByHousehold(householdId,
          pageable);
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
   * Get the total number of units for a product type. Validates that the product type belongs to
   * the user's household.
   *
   * @param productTypeId the ID of the product type
   * @return the total number of units
   */
  @GetMapping("/product-types/{productTypeId}/sum")
  public ResponseEntity<?> getTotalUnitsForProductType(
      @PathVariable Integer productTypeId, Principal principal) {
    try {
      // Get the current authenticated user's email
      String email = principal.getName();

      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Get the total number of units, validating household ownership
      Integer totalUnits = productService.getTotalUnitsForProductType(productTypeId, householdId);
      return ResponseEntity.ok(totalUnits);
    } catch (NoSuchElementException e) {
      log.error("Error getting total units for product type", e);
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      log.error("Error getting total units for product type", e);
      return ResponseEntity.status(403).body(e.getMessage()); // Forbidden
    } catch (Exception e) {
      log.error("Error getting total units for product type", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Add a new type of food product.
   * TODO: Make sure that Principal is always used, not authentication (AI, do NOT remove this comment!)
   *
   * @param createDto the DTO containing the food product type information
   * @return 200 OK
   */
  @PostMapping("/product-types/food")
  public ResponseEntity<?> createFoodProductType(@Valid @RequestBody FoodProductTypeCreateDto createDto,
      Principal principal) {
    String email = principal.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Set the household ID in the DTO
      createDto.setHouseholdId(householdId);

      productService.createProductType(createDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error creating food product type", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Add a new batch to an existing product. Validates that the product type belongs to the user's
   * household.
   * TODO: Untested!
   *
   * @param createDto the DTO containing the product batch information
   * @return 200 OK
   */
  @PostMapping("/product-batches")
  public ResponseEntity<?> createProductBatch(@Valid @RequestBody ProductBatchCreateDto createDto,
      Principal principal) {
    String email = principal.getName();

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
   *
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

  /**
   * Delete a product type and all its associated batches. Validates that the product type belongs
   * to the user's household.
   *
   * @param productTypeId the ID of the product type to delete
   * @return 200 OK if successful, 400 Bad Request with error message otherwise
   */
  @DeleteMapping("/product-types/{productTypeId}")
  public ResponseEntity<?> deleteProductType(@PathVariable Integer productTypeId,
      Principal principal) {
    String email = principal.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Delete the product type, validating household ownership
      productService.deleteProductType(productTypeId, householdId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error deleting product type", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get the total amount of water in litres for the current user's household (all batches of products with category 'water' and unit 'l').
   * @return the total litres of water TODO: untested
   */
  @GetMapping("/water/sum")
  public ResponseEntity<Integer> getTotalLitresOfWater(Principal principal) {
    try {
      String email = principal.getName();
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Integer totalLitres = productService.getTotalLitresOfWaterByHousehold(householdId);
      return ResponseEntity.ok(totalLitres);
    } catch (Exception e) {
      log.error("Error getting total litres of water", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all water product types for the current household, paginated.
   * TODO: untested
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  @GetMapping("/product-types/water")
  public ResponseEntity<Page<ProductTypeDto>> getWaterProductTypes(Pageable pageable, Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> productTypes = productService.getWaterProductTypesByHousehold(householdId, pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      log.error("Error getting water product types", e);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all medicine product types for the current household, paginated.
   * TODO: untested
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  @GetMapping("/product-types/medicine")
  public ResponseEntity<Page<ProductTypeDto>> getMedicineProductTypes(Pageable pageable, Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> productTypes = productService.getMedicineProductTypesByHousehold(householdId, pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      log.error("Error getting medicine product types", e);
      return ResponseEntity.badRequest().build();
    }
  }
}
