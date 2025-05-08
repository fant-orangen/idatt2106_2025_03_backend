package stud.ntnu.backend.service.inventory;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import stud.ntnu.backend.dto.inventory.*;
import stud.ntnu.backend.repository.household.HouseholdInventoryRepository;
import stud.ntnu.backend.repository.inventory.ProductRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.household.HouseholdMemberRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.HouseholdInventory;
import stud.ntnu.backend.model.inventory.Product;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.event.InventoryChangeEvent;
import stud.ntnu.backend.util.SearchUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing household inventory and products. Handles creation, retrieval, updating, and
 * deletion of inventory items and products.
 */
@Service
public class InventoryService {

  private final HouseholdInventoryRepository householdInventoryRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final ProductBatchRepository productBatchRepository;
  private final ProductTypeRepository productTypeRepository;
  private final HouseholdRepository householdRepository;
  private final SearchUtil searchUtil;
  private final ApplicationEventPublisher eventPublisher;
  private final HouseholdMemberRepository householdMemberRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param householdInventoryRepository repository for household inventory operations
   * @param productRepository            repository for product operations
   * @param userRepository               repository for user operations
   * @param productBatchRepository       repository for product batch operations
   * @param productTypeRepository        repository for product type operations
   * @param householdRepository          repository for household operations
   * @param searchUtil                 utility for search operations
   * @param eventPublisher             publisher for inventory change events
   * @param householdMemberRepository  repository for household member operations
   */
  public InventoryService(
      HouseholdInventoryRepository householdInventoryRepository,
      ProductRepository productRepository,
      UserRepository userRepository,
      ProductBatchRepository productBatchRepository,
      ProductTypeRepository productTypeRepository,
      HouseholdRepository householdRepository,
      SearchUtil searchUtil,
      ApplicationEventPublisher eventPublisher,
      HouseholdMemberRepository householdMemberRepository) {
    this.householdInventoryRepository = householdInventoryRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.productBatchRepository = productBatchRepository;
    this.productTypeRepository = productTypeRepository;
    this.householdRepository = householdRepository;
    this.searchUtil = searchUtil;
    this.eventPublisher = eventPublisher;
    this.householdMemberRepository = householdMemberRepository;
  }

  /**
   * Retrieves all household inventory items.
   *
   * @return list of all household inventory items
   */
  public List<HouseholdInventory> getAllInventoryItems() {
    return householdInventoryRepository.findAll();
  }

  /**
   * Retrieves a household inventory item by its ID.
   *
   * @param id the ID of the household inventory item
   * @return an Optional containing the household inventory item if found
   */
  public Optional<HouseholdInventory> getInventoryItemById(Integer id) {
    return householdInventoryRepository.findById(id);
  }

  /**
   * Saves a household inventory item.
   *
   * @param inventoryItem the household inventory item to save
   * @return the saved household inventory item
   */
  public HouseholdInventory saveInventoryItem(HouseholdInventory inventoryItem) {
    return householdInventoryRepository.save(inventoryItem);
  }

  /**
   * Deletes a household inventory item by its ID.
   *
   * @param id the ID of the household inventory item to delete
   */
  public void deleteInventoryItem(Integer id) {
    householdInventoryRepository.deleteById(id);
  }

  /**
   * Retrieves all products.
   *
   * @return list of all products
   */
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  /**
   * Retrieves a product by its ID.
   *
   * @param id the ID of the product
   * @return an Optional containing the product if found
   */
  public Optional<Product> getProductById(Integer id) {
    return productRepository.findById(id);
  }

  /**
   * Saves a product.
   *
   * @param product the product to save
   * @return the saved product
   */
  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }

  /**
   * Deletes a product by its ID.
   *
   * @param id the ID of the product to delete
   */
  public void deleteProduct(Integer id) {
    productRepository.deleteById(id);
  }

  /**
   * Gets the household ID for a user by email.
   *
   * @param email the email of the user
   * @return the household ID
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public Integer getHouseholdIdByUserEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    return household.getId();
  }

  /**
   * Converts a HouseholdInventory entity to an InventoryItemDto.
   *
   * @param inventoryItem the inventory item entity
   * @return the inventory item DTO
   */
  private InventoryItemDto convertToInventoryItemDto(HouseholdInventory inventoryItem) {
    Integer productId = null;
    String productName = null;
    String productTypeName = null;

    if (inventoryItem.getProduct() != null) {
      productId = inventoryItem.getProduct().getId();
      productName = inventoryItem.getProduct().getName();

      if (inventoryItem.getProduct().getProductType() != null) {
        productTypeName = inventoryItem.getProduct().getProductType().getName();
      }
    }

    return new InventoryItemDto(
        inventoryItem.getId(),
        productId,
        productName,
        productTypeName,
        inventoryItem.getCustomName(),
        inventoryItem.getQuantity(),
        inventoryItem.getExpirationDate()
    );
  }

  /**
   * Get all food product types for a specific household.
   *
   * @param householdId the ID of the household
   * @param pageable    pagination information
   * @return a page of food product types
   */
  public Page<ProductTypeDto> getAllFoodProductTypes(Integer householdId, Pageable pageable) {
    Page<ProductType> productTypes = productTypeRepository.findByHouseholdIdAndCategory(householdId,
        "food", pageable);
    return productTypes.map(this::convertToDto);
  }

  /**
   * Get all product types for a specific household.
   *
   * @param householdId the ID of the household
   * @param pageable    pagination information
   * @return a page of product types
   */
  public Page<ProductTypeDto> getProductTypesByHousehold(Integer householdId, Pageable pageable) {
    Page<ProductType> productTypes = productTypeRepository.findByHouseholdId(householdId, pageable);
    return productTypes.map(this::convertToDto);
  }

  /**
   * Get all product batches for a given product type.
   *
   * @param productTypeId the ID of the product type
   * @param pageable      pagination information
   * @return a page of product batches
   */
  public Page<ProductBatchDto> getProductBatchesByProductType(Integer productTypeId,
      Pageable pageable) {
    if (!productTypeRepository.existsById(productTypeId)) {
      throw new NoSuchElementException("Product type not found with ID: " + productTypeId);
    }

    Page<ProductBatch> productBatches = productBatchRepository.findByProductTypeId(productTypeId,
        pageable);
    return productBatches.map(this::convertToDto);
  }

  /**
   * Create a new food product type.
   *
   * @param createDto the DTO containing the food product type information
   * @return the created product type
   */
  @Transactional
  public ProductTypeDto createProductType(FoodProductTypeCreateDto createDto) {
    Household household = householdRepository.findById(createDto.getHouseholdId())
        .orElseThrow(() -> new NoSuchElementException(
            "Household not found with ID: " + createDto.getHouseholdId()));

    ProductType productType = new ProductType(
        household,
        createDto.getName(),
        createDto.getUnit(),
        createDto.getCaloriesPerUnit(),
        createDto.getCategory()
    );

    ProductType savedProductType = productTypeRepository.save(productType);
    return convertToDto(savedProductType);
  }

  /**
   * Create a new product batch with household validation.
   *
   * @param createDto   the DTO containing the product batch information
   * @param householdId the ID of the household to validate against
   * @return the created product batch
   * @throws IllegalArgumentException if the product type doesn't belong to the specified household
   */
  @Transactional
  public ProductBatchDto createProductBatch(ProductBatchCreateDto createDto, Integer householdId) {
    ProductType productType = productTypeRepository.findById(createDto.getProductTypeId())
        .orElseThrow(() -> new NoSuchElementException(
            "Product type not found with ID: " + createDto.getProductTypeId()));

    // Validate that the product type belongs to the user's household
    if (!productType.getHousehold().getId().equals(householdId)) {
      throw new IllegalArgumentException(
          "Product type with ID " + createDto.getProductTypeId()
              + " does not belong to the user's household");
    }

    ProductBatch productBatch = new ProductBatch(
        productType,
        LocalDateTime.now(),
        createDto.getExpirationTime(),
        createDto.getNumber()
    );

    ProductBatch savedProductBatch = productBatchRepository.save(productBatch);
    return convertToDto(savedProductBatch);
  }

  /**
   * Update a product batch by setting its number of units to a specific value.
   *
   * @param batchId the ID of the batch to update
   * @param newNumberOfUnits the new number of units to set for the batch
   * @return the updated product batch
   * @throws NoSuchElementException if the batch is not found
   * @throws IllegalArgumentException if newNumberOfUnits is negative
   */
  @Transactional
  public ProductBatchDto updateProductBatch(Integer batchId, Integer newNumberOfUnits) {
    if (newNumberOfUnits < 0) {
      throw new IllegalArgumentException("Number of units cannot be negative");
    }

    ProductBatch batch = productBatchRepository.findById(batchId)
        .orElseThrow(
            () -> new NoSuchElementException("Product batch not found with ID: " + batchId));

    Integer householdId = batch.getProductType().getHousehold().getId();
    
    batch.setNumber(newNumberOfUnits);
    ProductBatch updatedBatch = productBatchRepository.save(batch);

    // Publish event after update
    eventPublisher.publishEvent(new InventoryChangeEvent(householdId, "UPDATE"));

    return convertToDto(updatedBatch);
  }

  /**
   * Delete a product batch.
   *
   * @param batchId the ID of the batch to delete
   */
  @Transactional
  public void deleteProductBatch(Integer batchId) {
    ProductBatch batch = productBatchRepository.findById(batchId)
        .orElseThrow(
            () -> new NoSuchElementException("Product batch not found with ID: " + batchId));

    Integer householdId = batch.getProductType().getHousehold().getId();

    productBatchRepository.deleteById(batchId);

    // Publish event after deletion
    eventPublisher.publishEvent(new InventoryChangeEvent(householdId, "DELETE"));
  }

  /**
   * Delete a product type and all its associated batches.
   *
   * @param productTypeId the ID of the product type to delete
   * @param householdId   the ID of the household to validate against
   * @throws NoSuchElementException   if the product type doesn't exist
   * @throws IllegalArgumentException if the product type doesn't belong to the specified household
   */
  @Transactional
  public void deleteProductType(Integer productTypeId, Integer householdId) {
    ProductType productType = productTypeRepository.findById(productTypeId)
        .orElseThrow(() -> new NoSuchElementException(
            "Product type not found with ID: " + productTypeId));

    // Validate that the product type belongs to the user's household
    if (!productType.getHousehold().getId().equals(householdId)) {
      throw new IllegalArgumentException(
          "Product type with ID " + productTypeId
              + " does not belong to the user's household");
    }

    // Delete the product type - associated product batches will be deleted automatically via ON DELETE CASCADE
    productTypeRepository.deleteById(productTypeId);

    eventPublisher.publishEvent(new InventoryChangeEvent(householdId, "DELETE"));
  }

  /**
   * Get the total number of units for a product type. Validates that the product type belongs to
   * the user's household.
   *
   * @param productTypeId the ID of the product type
   * @param householdId   the ID of the household to validate against
   * @return the total number of units
   * @throws NoSuchElementException   if the product type doesn't exist
   * @throws IllegalArgumentException if the product type doesn't belong to the specified household
   */
  public Integer getTotalUnitsForProductType(Integer productTypeId, Integer householdId) {
    // Check if the product type exists and belongs to the user's household
    ProductType productType = productTypeRepository.findById(productTypeId)
        .orElseThrow(
            () -> new NoSuchElementException("Product type not found with ID: " + productTypeId));

    // Validate that the product type belongs to the user's household
    if (!productType.getHousehold().getId().equals(householdId)) {
      throw new IllegalArgumentException(
          "Product type with ID " + productTypeId + " does not belong to the user's household");
    }

    // Get the total number of units
    return productBatchRepository.sumNumberByProductTypeId(productTypeId);
  }

  /**
   * Get the total amount of water in litres (all batches of products with category 'water' and unit
   * 'l').
   *
   * @return the total litres of water
   */
  public Integer getTotalLitresOfWater() {
    return productBatchRepository.sumTotalLitresOfWater();
  }

  /**
   * Get the total amount of water in litres for a specific household (all batches of products with
   * category 'water' and unit 'l').
   *
   * @param householdId the ID of the household
   * @return the total litres of water
   */
  public Integer getTotalLitresOfWaterByHousehold(Integer householdId) {
    return productBatchRepository.sumTotalLitresOfWaterByHousehold(householdId);
  }

  /**
   * Get all water product types for a specific household, paginated.
   *
   * @param householdId the ID of the household
   * @param pageable    pagination information
   * @return a page of ProductTypeDto
   */
  public Page<ProductTypeDto> getWaterProductTypesByHousehold(Integer householdId,
      Pageable pageable) {
    Page<ProductType> productTypes = productTypeRepository.findByHouseholdIdAndCategory(householdId,
        "water", pageable);
    return productTypes.map(this::convertToDto);
  }

  /**
   * Get all medicine product types for a specific household, paginated.
   *
   * @param householdId the ID of the household
   * @param pageable    pagination information
   * @return a page of ProductTypeDto
   */
  public Page<ProductTypeDto> getMedicineProductTypesByHousehold(Integer householdId,
      Pageable pageable) {
    Page<ProductType> productTypes = productTypeRepository.findByHouseholdIdAndCategory(householdId,
        "medicine", pageable);
    return productTypes.map(this::convertToDto);
  }

  /**
   * Create a new water product type.
   *
   * @param createDto the DTO containing the water product type information
   * @return the created product type
   */
  @Transactional
  public ProductTypeDto createWaterProductType(WaterProductTypeCreateDto createDto) {
    Household household = householdRepository.findById(createDto.getHouseholdId())
        .orElseThrow(() -> new NoSuchElementException(
            "Household not found with ID: " + createDto.getHouseholdId()));

    ProductType productType = new ProductType(
        household,
        createDto.getName(),
        createDto.getUnit(),
        null, // caloriesPerUnit is always null for water
        createDto.getCategory()
    );

    ProductType savedProductType = productTypeRepository.save(productType);
    return convertToDto(savedProductType);
  }

  /**
   * Create a new medicine product type.
   *
   * @param createDto the DTO containing the medicine product type information
   * @return the created product type
   */
  @Transactional
  public ProductTypeDto createMedicineProductType(MedicineProductTypeCreateDto createDto) {
    Household household = householdRepository.findById(createDto.getHouseholdId())
        .orElseThrow(() -> new NoSuchElementException(
            "Household not found with ID: " + createDto.getHouseholdId()));

    ProductType productType = new ProductType(
        household,
        createDto.getName(),
        createDto.getUnit(),
        null, // caloriesPerUnit is always null for medicine
        createDto.getCategory()
    );

    ProductType savedProductType = productTypeRepository.save(productType);
    return convertToDto(savedProductType);
  }

  /**
   * Convert a ProductBatch entity to a ProductBatchDto.
   *
   * @param batch the ProductBatch entity
   * @return the ProductBatchDto
   */
  private ProductBatchDto convertToDto(ProductBatch batch) {
    return ProductBatchDto.builder()
        .id(batch.getId())
        .productTypeId(batch.getProductType().getId())
        .productTypeName(batch.getProductType().getName())
        .dateAdded(batch.getDateAdded())
        .expirationTime(batch.getExpirationTime())
        .number(batch.getNumber())
        .build();
  }

  /**
   * Convert a ProductType entity to a ProductTypeDto.
   *
   * @param productType the ProductType entity
   * @return the ProductTypeDto
   */
  private ProductTypeDto convertToDto(ProductType productType) {
    return ProductTypeDto.builder()
        .id(productType.getId())
        .householdId(productType.getHousehold().getId())
        .name(productType.getName())
        .unit(productType.getUnit())
        .caloriesPerUnit(productType.getCaloriesPerUnit())
        .category(productType.getCategory())
        .build();
  }

  /**
   * Search for product types by name, category, and household.
   *
   * @param householdId the ID of the household
   * @param category    the category to filter by
   * @param search      the search string for the name
   * @param pageable    pagination information
   * @return a page of matching ProductTypeDto
   */
  public Page<ProductTypeDto> searchProductTypesByNameAndCategoryAndHousehold(Integer householdId,
      String category, String search, Pageable pageable) {
    // Use SearchUtil to search by name, then filter by household and category
    Page<ProductType> page = searchUtil.searchByDescription(ProductType.class, "name", search,
        pageable);
    // Filter by household and category
    List<ProductType> filteredList = page.getContent().stream()
        .filter(pt -> pt.getHousehold() != null && pt.getHousehold().getId().equals(householdId)
            && pt.getCategory().equalsIgnoreCase(category))
        .toList();
    return new PageImpl<>(filteredList, pageable, filteredList.size()).map(this::convertToDto);
  }

  /**
   * Calculate total calories available in a household's inventory.
   *
   * @param householdId the ID of the household
   * @return total calories available
   */
  public Integer getTotalCaloriesByHousehold(Integer householdId) {
    return productBatchRepository.sumTotalCaloriesByHousehold(householdId);
  }

  /**
   * Calculate required water per day for a household (3L per person).
   *
   * @param householdId the ID of the household
   * @return required water in litres per day
   */
  public Integer getHouseholdWaterRequirement(Integer householdId) {
    Household household = householdRepository.findById(householdId)
        .orElseThrow(
            () -> new NoSuchElementException("Household not found with ID: " + householdId));

    // Count users in household
    int userCount = userRepository.countByHouseholdId(householdId);

    // Count non-user household members (excluding pets)
    int memberCount = householdMemberRepository.countByHouseholdIdAndTypeNot(householdId, "pet");

    // Calculate total water requirement (3L per person per day)
    return (userCount + memberCount) * 3;
  }

  /**
   * Calculate required calories per day for a household.
   *
   * @param householdId the ID of the household
   * @return required calories per day
   */
  public Integer getHouseholdCalorieRequirement(Integer householdId) {
    // Sum kcal requirements for users
    Integer userCalories = userRepository.sumKcalRequirementByHouseholdId(householdId);

    // Sum kcal requirements for household members
    Integer memberCalories = householdMemberRepository.sumKcalRequirementByHouseholdId(householdId);

    return userCalories + memberCalories;
  }

  /**
   * Calculate the number of days of water remaining in the household based on
   * the recommended daily water consumption per person.
   *
   * @param householdId the ID of the household
   * @return the number of days of water remaining
   */
  public Double getWaterDaysRemaining(Integer householdId) {
    // Get total water in the household
    Integer totalLitres = getTotalLitresOfWaterByHousehold(householdId);
    
    // Get the daily water requirement for the household
    Integer dailyRequirement = getHouseholdWaterRequirement(householdId);
    
    // Avoid division by zero
    if (dailyRequirement == 0) {
      return 0.0;
    }
    
    // Calculate days remaining
    return totalLitres.doubleValue() / dailyRequirement.doubleValue();
  }

  /**
   * Calculate the number of days of food remaining in the household based on
   * the recommended daily food consumption per person.
   *
   * @param householdId the ID of the household
   * @return the number of days of food remaining
   */
  public Double getFoodDaysRemaining(Integer householdId) {
    // Get total calories available in the household
    Integer totalCalories = getTotalCaloriesByHousehold(householdId);
    
    // Get the daily calorie requirement for the household
    Integer dailyRequirement = getHouseholdCalorieRequirement(householdId);
    
    // Avoid division by zero
    if (dailyRequirement == 0) {
      return 0.0;
    }
    
    // Calculate days remaining
    return totalCalories.doubleValue() / dailyRequirement.doubleValue();
  }

  /**
   * Get all expiring product types for a specific household, filtered by category and expiration time.
   *
   * @param householdId the ID of the household
   * @param category the category to filter by (food, water, medicine)
   * @param expirationTimeInDays the number of days within which products should expire
   * @param pageable pagination information
   * @return a page of product types that have batches expiring within the specified time
   */
  public Page<ProductTypeDto> getExpiringProductTypes(
      Integer householdId,
      String category,
      Integer expirationTimeInDays,
      Pageable pageable) {
    
    // Calculate the expiration cutoff date
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffDate = now.plusDays(expirationTimeInDays);

    // Get product types with expiring batches
    Page<ProductType> productTypes = productTypeRepository.findByHouseholdIdAndCategory(
        householdId, category, pageable);

    // Filter product types to only include those with batches expiring within the time period
    List<ProductType> filteredTypes = productTypes.getContent().stream()
        .filter(type -> {
            List<ProductBatch> batches = productBatchRepository.findByProductTypeId(type.getId(), Pageable.unpaged()).getContent();
            return batches.stream()
                .anyMatch(batch -> {
                    LocalDateTime expirationTime = batch.getExpirationTime();
                    return expirationTime != null && 
                           expirationTime.isAfter(now) && 
                           expirationTime.isBefore(cutoffDate);
                });
        })
        .toList();

    // Create a new page with the filtered results
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), filteredTypes.size());
    
    return new PageImpl<>(
        filteredTypes.subList(start, end),
        pageable,
        filteredTypes.size()
    ).map(this::convertToDto);
  }

  /**
   * Get all expiring product batches for a given product type.
   *
   * @param productTypeId the ID of the product type
   * @param pageable pagination information
   * @return a page of expiring product batches
   */
  public Page<ProductBatchDto> getExpiringProductBatchesByProductType(
      Integer productTypeId,
      Pageable pageable) {
    
    if (!productTypeRepository.existsById(productTypeId)) {
      throw new NoSuchElementException("Product type not found with ID: " + productTypeId);
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffDate = now.plusDays(7); // Default to 7 days for expiring products

    Page<ProductBatch> productBatches = productBatchRepository.findByProductTypeIdAndExpirationTimeBetween(
        productTypeId, now, cutoffDate, pageable);
        
    return productBatches.map(this::convertToDto);
  }
}
