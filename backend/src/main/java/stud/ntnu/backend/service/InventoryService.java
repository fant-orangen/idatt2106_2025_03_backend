package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.inventory.InventoryItemCreateDto;
import stud.ntnu.backend.dto.inventory.InventoryItemDto;
import stud.ntnu.backend.dto.inventory.InventoryItemUpdateDto;
import stud.ntnu.backend.dto.inventory.InventoryStatusDto;
import stud.ntnu.backend.repository.household.HouseholdInventoryRepository;
import stud.ntnu.backend.repository.inventory.ProductRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.HouseholdInventory;
import stud.ntnu.backend.model.inventory.Product;
import stud.ntnu.backend.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  /**
   * Constructor for dependency injection.
   *
   * @param householdInventoryRepository repository for household inventory operations
   * @param productRepository            repository for product operations
   * @param userRepository               repository for user operations
   */
  public InventoryService(HouseholdInventoryRepository householdInventoryRepository,
      ProductRepository productRepository, UserRepository userRepository) {
    this.householdInventoryRepository = householdInventoryRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
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
   * Gets the inventory items for a user's household.
   *
   * @param email the email of the user
   * @return a list of inventory items
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public List<InventoryItemDto> getHouseholdInventory(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Get inventory items for the household
    List<HouseholdInventory> inventoryItems = householdInventoryRepository.findByHouseholdId(
        household.getId());

    // Convert to DTOs
    return inventoryItems.stream()
        .map(this::convertToInventoryItemDto)
        .collect(Collectors.toList());
  }

  /**
   * Creates a new inventory item for a user's household.
   *
   * @param email     the email of the user
   * @param createDto the inventory item creation DTO
   * @return the created inventory item
   * @throws IllegalStateException if the user is not found, doesn't have a household, or the
   *                               product is not found
   */
  public InventoryItemDto createInventoryItem(String email, InventoryItemCreateDto createDto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    HouseholdInventory inventoryItem;

    // Check if this is a custom item or a product from the catalog
    if (createDto.getProductId() != null) {
      // Get the product
      Product product = productRepository.findById(createDto.getProductId())
          .orElseThrow(() -> new IllegalStateException("Product not found"));

      // Create the inventory item
      inventoryItem = new HouseholdInventory(household, product, createDto.getQuantity());
    } else if (createDto.getCustomName() != null && !createDto.getCustomName().trim().isEmpty()) {
      // Create a custom inventory item
      inventoryItem = new HouseholdInventory(household, createDto.getCustomName(),
          createDto.getQuantity());
    } else {
      throw new IllegalStateException("Either productId or customName must be provided");
    }

    // Set the expiration date if provided
    inventoryItem.setExpirationDate(createDto.getExpirationDate());

    // Save the inventory item
    inventoryItem = householdInventoryRepository.save(inventoryItem);

    // Convert to DTO
    return convertToInventoryItemDto(inventoryItem);
  }

  /**
   * Updates an inventory item for a user's household.
   *
   * @param email     the email of the user
   * @param id        the ID of the inventory item
   * @param updateDto the inventory item update DTO
   * @return the updated inventory item
   * @throws IllegalStateException if the user is not found, doesn't have a household, the inventory
   *                               item is not found, the inventory item doesn't belong to the
   *                               user's household, or the product is not found
   */
  public InventoryItemDto updateInventoryItem(String email, Integer id,
      InventoryItemUpdateDto updateDto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Get the inventory item
    HouseholdInventory inventoryItem = householdInventoryRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Inventory item not found"));

    // Check if the inventory item belongs to the user's household
    if (!inventoryItem.getHousehold().getId().equals(household.getId())) {
      throw new IllegalStateException("Inventory item doesn't belong to the user's household");
    }

    // Update the inventory item
    if (updateDto.getProductId() != null) {
      // Get the product
      Product product = productRepository.findById(updateDto.getProductId())
          .orElseThrow(() -> new IllegalStateException("Product not found"));

      // Update the product
      inventoryItem.setProduct(product);
      inventoryItem.setCustomName(null);
    } else if (updateDto.getCustomName() != null && !updateDto.getCustomName().trim().isEmpty()) {
      // Update the custom name
      inventoryItem.setCustomName(updateDto.getCustomName());
      inventoryItem.setProduct(null);
    }

    // Update the quantity if provided
    if (updateDto.getQuantity() != null) {
      inventoryItem.setQuantity(updateDto.getQuantity());
    }

    // Update the expiration date if provided
    if (updateDto.getExpirationDate() != null) {
      inventoryItem.setExpirationDate(updateDto.getExpirationDate());
    }

    // Save the inventory item
    inventoryItem = householdInventoryRepository.save(inventoryItem);

    // Convert to DTO
    return convertToInventoryItemDto(inventoryItem);
  }

  /**
   * Deletes an inventory item for a user's household.
   *
   * @param email the email of the user
   * @param id    the ID of the inventory item
   * @throws IllegalStateException if the user is not found, doesn't have a household, the inventory
   *                               item is not found, or the inventory item doesn't belong to the
   *                               user's household
   */
  public void deleteInventoryItem(String email, Integer id) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Get the inventory item
    HouseholdInventory inventoryItem = householdInventoryRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Inventory item not found"));

    // Check if the inventory item belongs to the user's household
    if (!inventoryItem.getHousehold().getId().equals(household.getId())) {
      throw new IllegalStateException("Inventory item doesn't belong to the user's household");
    }

    // Delete the inventory item
    householdInventoryRepository.deleteById(id);
  }

  /**
   * Gets the preparedness status of a user's household.
   *
   * @param email the email of the user
   * @return the preparedness status
   * @throws IllegalStateException if the user is not found or doesn't have a household
   */
  public InventoryStatusDto getHouseholdInventoryStatus(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    Household household = user.getHousehold();
    if (household == null) {
      throw new IllegalStateException("User doesn't have a household");
    }

    // Get inventory items for the household
    List<HouseholdInventory> inventoryItems = householdInventoryRepository.findByHouseholdId(
        household.getId());

    // Calculate product type counts
    Map<String, Integer> productTypeCounts = new HashMap<>();
    for (HouseholdInventory item : inventoryItems) {
      if (item.getProduct() != null && item.getProduct().getProductType() != null) {
        String productTypeName = item.getProduct().getProductType().getName();
        Integer count = productTypeCounts.getOrDefault(productTypeName, 0);
        count += item.getQuantity().intValue();
        productTypeCounts.put(productTypeName, count);
      }
    }

    // Calculate product type status
    List<InventoryStatusDto.ProductTypeStatusDto> productTypeStatus = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : productTypeCounts.entrySet()) {
      String productTypeName = entry.getKey();
      Integer count = entry.getValue();

      // TODO: Calculate recommended count based on household size and product type
      Integer recommendedCount = 10 * household.getPopulationCount();

      // Check if the count is sufficient
      Boolean sufficient = count >= recommendedCount;

      productTypeStatus.add(new InventoryStatusDto.ProductTypeStatusDto(
          productTypeName, count, recommendedCount, sufficient
      ));
    }

    // Calculate survival days
    // TODO: Calculate survival days based on inventory items and household size
    Integer survivalDays = 7;

    // Create the status DTO
    return new InventoryStatusDto(
        household.getId(),
        household.getName(),
        household.getPopulationCount(),
        survivalDays,
        productTypeCounts,
        productTypeStatus
    );
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
}
