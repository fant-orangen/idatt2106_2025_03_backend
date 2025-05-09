package stud.ntnu.backend.controller.household;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;
import stud.ntnu.backend.dto.inventory.*;
import stud.ntnu.backend.service.inventory.InventoryService;

@RestController
@RequestMapping("/api/user/inventory")
@Tag(name = "Household Inventory", description = "Operations for managing household inventory, including food, water, and medicine products")
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
  @Operation(summary = "Get product batches", description = "Get all product batches for a given product type.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved product batches", 
          content = @Content(schema = @Schema(implementation = ProductBatchDto.class)))
  })
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
  @Operation(summary = "Get total units", description = "Get the total number of units for a product type. Validates that the product type belongs to the user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved total units", 
          content = @Content(schema = @Schema(type = "integer"))),
      @ApiResponse(responseCode = "403", description = "Forbidden - product type does not belong to user's household", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "404", description = "Product type not found")
  })
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
  @Operation(summary = "Get food product types", description = "Get all food product types for the current household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved food product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
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
   *
   * @param createDto the DTO containing the food product type information
   * @return 200 OK
   */
  @Operation(summary = "Create food product type", description = "Add a new type of food product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created food product type"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Create product batch", description = "Add a new batch to an existing product. Validates that the product type belongs to the user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created product batch"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
   * @param batchId          the ID of the batch
   * @param newNumberOfUnits the new number of units to set for the batch
   * @return 200 OK if successful, 400 Bad Request with error message otherwise
   */
  @Operation(summary = "Update product batch", description = "Update the number of units in an existing batch.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated product batch"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Delete product batch", description = "Delete an existing batch.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted product batch"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid batch ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Delete product type", description = "Delete a product type and all its associated batches. Validates that the product type belongs to the user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted product type"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid product type ID", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Get total water", description = "Get the total amount of water in litres for the current user's household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved total water", 
          content = @Content(schema = @Schema(type = "integer"))),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
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


  /**
   * Get the number of days of water remaining in the household based on the recommended daily water
   * consumption per person.
   *
   * @param principal the Principal representing the current user
   * @return the number of days of water remaining
   */
  @Operation(summary = "Get water days remaining", description = "Get the number of days of water remaining in the household based on the recommended daily water consumption per person.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved water days remaining", 
          content = @Content(schema = @Schema(type = "number", format = "double"))),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
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


  /**
   * Get the number of days of food remaining in the household based on the recommended daily food
   * consumption per person.
   *
   * @param principal the Principal representing the current user
   * @return the number of days of food remaining
   */
  @Operation(summary = "Get food days remaining", description = "Get the number of days of food remaining in the household based on the recommended daily food consumption per person.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved food days remaining", 
          content = @Content(schema = @Schema(type = "number", format = "double"))),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
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


  /**
   * Get all water product types for the current household, paginated.
   *
   * @param pageable pagination information
   * @return a page of ProductTypeDto
   */
  @Operation(summary = "Get water product types", description = "Get all water product types for the current household, paginated.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved water product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
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
  @Operation(summary = "Get medicine product types", description = "Get all medicine product types for the current household, paginated.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved medicine product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
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

  /**
   * Get all expiring product types for the current household, filtered by category and expiration
   * time.
   *
   * @param category             the category to filter by (food, water, medicine)
   * @param expirationTimeInDays the expiration time in days
   * @param pageable             pagination information
   * @param principal            the authenticated user
   * @return a page of ProductTypeDto
   */
  @Operation(summary = "Get expiring product types", description = "Get all expiring product types for the current household, filtered by category and expiration time.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved expiring product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters")
  })
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
  @Operation(summary = "Create water product type", description = "Add a new type of water product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created water product type"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Create medicine product type", description = "Add a new type of medicine product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created medicine product type"),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
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
  @Operation(summary = "Search product types", description = "Search for product types by name, category, and household.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved matching product types", 
          content = @Content(schema = @Schema(implementation = ProductTypeDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid search parameters")
  })
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
