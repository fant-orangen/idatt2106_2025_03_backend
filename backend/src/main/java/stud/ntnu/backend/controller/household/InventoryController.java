package stud.ntnu.backend.controller.household;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;
import stud.ntnu.backend.dto.inventory.*;
import stud.ntnu.backend.service.inventory.InventoryService;


@RestController
@RequestMapping("/api/user/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
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
    Page<ProductBatchDto> productBatches = inventoryService.getProductBatchesByProductType(
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
      Integer totalUnits = inventoryService.getTotalUnitsForProductType(productTypeId, householdId);
      return ResponseEntity.ok(totalUnits);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(403).body(e.getMessage()); // Forbidden
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get all food product types for the current household.
   *
   * @param pageable pagination information
   * @return a paginated list of food product types
   */
  @GetMapping("/product-types/food")
  public ResponseEntity<Page<ProductTypeDto>> getAllFoodProductTypes(Pageable pageable,
      Principal principal) {
    try {
      String email = principal.getName();
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> productTypes = inventoryService.getAllFoodProductTypes(householdId,
          pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
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
  public ResponseEntity<?> createFoodProductType(
      @Valid @RequestBody FoodProductTypeCreateDto createDto,
      Principal principal) {
    String email = principal.getName();

    try {
      // Get the user's household ID
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);

      // Set the household ID in the DTO
      createDto.setHouseholdId(householdId);

      inventoryService.createProductType(createDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Add a new batch to an existing product. Validates that the product type belongs to the user's
   * household.
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
      inventoryService.createProductBatch(createDto, householdId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Update the number of units in an existing batch.
   *
   * @param batchId the ID of the batch
   * @param newNumberOfUnits the new number of units to set for the batch
   * @return 200 OK if successful, 400 Bad Request with error message otherwise
   */
  @PutMapping("/product-batches/{batchId}")
  public ResponseEntity<?> updateProductBatch(
      @PathVariable Integer batchId,
      @RequestBody @Valid @Positive Integer newNumberOfUnits) {
    try {
      inventoryService.updateProductBatch(batchId, newNumberOfUnits);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
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
      inventoryService.deleteProductBatch(batchId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
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
      inventoryService.deleteProductType(productTypeId, householdId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Get the total amount of water in litres for the current user's household (all batches of
   * products with category 'water' and unit 'l').
   *
   * @return the total litres of water
   */
  @GetMapping("/water/sum")
  public ResponseEntity<Integer> getTotalLitresOfWater(Principal principal) {
    try {
      String email = principal.getName();
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Integer totalLitres = inventoryService.getTotalLitresOfWaterByHousehold(householdId);
      return ResponseEntity.ok(totalLitres);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // TODO: add endpoint to get the number of days left of water in the household

  /**
   * Get the number of days of water remaining in the household based on the recommended daily
   * water consumption per person.
   *
   * @param principal the Principal representing the current user
   * @return the number of days of water remaining
   */
  @GetMapping("/water/days-remaining")
  public ResponseEntity<Double> getWaterDaysRemaining(Principal principal) {
    try {
      String email = principal.getName();
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Double daysRemaining = inventoryService.getWaterDaysRemaining(householdId);
      return ResponseEntity.ok(daysRemaining);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // TODO: call getWaterDaysRemaining endpoint and getFoodDaysRemaining endpoint on frontend
  /**
   * Get the number of days of food remaining in the household based on the recommended daily
   * food consumption per person.
   *
   * @param principal the Principal representing the current user
   * @return the number of days of food remaining
   */
  @GetMapping("/food/days-remaining")
  public ResponseEntity<Double> getFoodDaysRemaining(Principal principal) {
    try {
      String email = principal.getName();
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Double daysRemaining = inventoryService.getFoodDaysRemaining(householdId);
      return ResponseEntity.ok(daysRemaining);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // TODO: add endpoint to get the number of days left of food in the household

  /**
   * Get all water product types for the current household, paginated.
   *
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  @GetMapping("/product-types/water")
  public ResponseEntity<Page<ProductTypeDto>> getWaterProductTypes(Pageable pageable,
      Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> productTypes = inventoryService.getWaterProductTypesByHousehold(
          householdId, pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Get all medicine product types for the current household, paginated.
   *
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  @GetMapping("/product-types/medicine")
  public ResponseEntity<Page<ProductTypeDto>> getMedicineProductTypes(Pageable pageable,
      Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> productTypes = inventoryService.getMedicineProductTypesByHousehold(
          householdId, pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
  // TODO: test this endpoint
  /**
   * Get all expiring product types for the current household, filtered by category and expiration time.
   * 
   * @param category            the category to filter by (food, water, medicine)
   * @param expirationTimeInDays the expiration time in days
   * @param pageable            pagination information
   * @param principal           the authenticated user
   * @return a page of ProductTypeDto
   */
  @GetMapping("/product-types/expiring")
  public ResponseEntity<Page<ProductTypeDto>> getExpiringProductTypes(
      @RequestParam @Valid String category,
      @RequestParam @Valid @Positive Integer expirationTimeInDays,
      Pageable pageable,
      Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> productTypes = inventoryService.getExpiringProductTypes(
          householdId, category, expirationTimeInDays, pageable);
      return ResponseEntity.ok(productTypes);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Add a new type of water product.
   *
   * @param createDto the DTO containing the water product type information
   * @return 200 OK
   */
  @PostMapping("/product-types/water")
  public ResponseEntity<?> createWaterProductType(
      @Valid @RequestBody WaterProductTypeCreateDto createDto,
      Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      createDto.setHouseholdId(householdId);
      inventoryService.createWaterProductType(createDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Add a new type of medicine product.
   *
   * @param createDto the DTO containing the medicine product type information
   * @return 200 OK
   */
  @PostMapping("/product-types/medicine")
  public ResponseEntity<?> createMedicineProductType(
      @Valid @RequestBody MedicineProductTypeCreateDto createDto,
      Principal principal) {
    String email = principal.getName();
    try {
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      createDto.setHouseholdId(householdId);
      inventoryService.createMedicineProductType(createDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Search for product types by name, category, and household.
   * TODO: untested
   *
   * @param search    the search string
   * @param category  the category to filter by (food, water, medicine)
   * @param pageable  pagination information
   * @param principal the authenticated user
   * @return a page of matching ProductTypeDto
   */
  @GetMapping("/product-types/search")
  public ResponseEntity<Page<ProductTypeDto>> searchProductTypes(
      @RequestParam String search,
      @RequestParam String category,
      Pageable pageable,
      Principal principal) {
    try {
      String email = principal.getName();
      Integer householdId = inventoryService.getHouseholdIdByUserEmail(email);
      Page<ProductTypeDto> result = inventoryService.searchProductTypesByNameAndCategoryAndHousehold(
          householdId, category, search, pageable);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }


}
