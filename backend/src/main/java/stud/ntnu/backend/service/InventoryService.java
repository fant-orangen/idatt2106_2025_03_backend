package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.HouseholdInventoryRepository;
import stud.ntnu.backend.repository.ProductRepository;
import stud.ntnu.backend.model.HouseholdInventory;
import stud.ntnu.backend.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing household inventory and products. Handles creation, retrieval, updating, and
 * deletion of inventory items and products.
 */
@Service
public class InventoryService {

  private final HouseholdInventoryRepository householdInventoryRepository;
  private final ProductRepository productRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param householdInventoryRepository repository for household inventory operations
   * @param productRepository            repository for product operations
   */
  public InventoryService(HouseholdInventoryRepository householdInventoryRepository,
      ProductRepository productRepository) {
    this.householdInventoryRepository = householdInventoryRepository;
    this.productRepository = productRepository;
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
}