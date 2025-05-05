package stud.ntnu.backend.service.inventory;

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
