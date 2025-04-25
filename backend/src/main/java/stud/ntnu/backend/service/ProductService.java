package stud.ntnu.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.inventory.ProductBatchCreateDto;
import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ProductBatchUpdateDto;
import stud.ntnu.backend.dto.inventory.ProductTypeCreateDto;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.repository.HouseholdRepository;
import stud.ntnu.backend.repository.ProductBatchRepository;
import stud.ntnu.backend.repository.ProductTypeRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Service for managing product types and batches.
 */
@Service
public class ProductService {

  private final ProductBatchRepository productBatchRepository;
  private final ProductTypeRepository productTypeRepository;
  private final HouseholdRepository householdRepository;

  public ProductService(ProductBatchRepository productBatchRepository,
      ProductTypeRepository productTypeRepository,
      HouseholdRepository householdRepository) {
    this.productBatchRepository = productBatchRepository;
    this.productTypeRepository = productTypeRepository;
    this.householdRepository = householdRepository;
  }

  /**
   * Get all product types.
   *
   * @param pageable pagination information
   * @return a page of product types
   */
  public Page<ProductTypeDto> getAllProductTypes(Pageable pageable) {
    Page<ProductType> productTypes = productTypeRepository.findAll(pageable);
    return productTypes.map(this::convertToDto);
  }

  /**
   * Get all product types for a specific household.
   *
   * @param householdId the ID of the household
   * @param pageable pagination information
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
   * Create a new product type.
   *
   * @param createDto the DTO containing the product type information
   * @return the created product type
   */
  @Transactional
  public ProductTypeDto createProductType(ProductTypeCreateDto createDto) {
    Household household = householdRepository.findById(createDto.getHouseholdId())
        .orElseThrow(() -> new NoSuchElementException(
            "Household not found with ID: " + createDto.getHouseholdId()));

    ProductType productType = new ProductType(
        household,
        createDto.getName(),
        createDto.getUnit(),
        createDto.getCaloriesPerUnit(),
        createDto.getIsWater()
    );

    ProductType savedProductType = productTypeRepository.save(productType);
    return convertToDto(savedProductType);
  }

  /**
   * Create a new product batch.
   *
   * @param createDto the DTO containing the product batch information
   * @return the created product batch
   */
  @Transactional
  public ProductBatchDto createProductBatch(ProductBatchCreateDto createDto) {
    ProductType productType = productTypeRepository.findById(createDto.getProductTypeId())
        .orElseThrow(() -> new NoSuchElementException(
            "Product type not found with ID: " + createDto.getProductTypeId()));

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
   * Update a product batch by reducing the number of units.
   *
   * @param batchId   the ID of the batch to update
   * @param updateDto the DTO containing the update information
   * @return the updated product batch
   */
  @Transactional
  public ProductBatchDto updateProductBatch(Integer batchId, ProductBatchUpdateDto updateDto) {
    ProductBatch batch = productBatchRepository.findById(batchId)
        .orElseThrow(
            () -> new NoSuchElementException("Product batch not found with ID: " + batchId));

    if (batch.getNumber() < updateDto.getUnitsToRemove()) {
      throw new IllegalArgumentException("Cannot remove more units than available in the batch");
    }

    batch.setNumber(batch.getNumber() - updateDto.getUnitsToRemove());
    ProductBatch updatedBatch = productBatchRepository.save(batch);

    return convertToDto(updatedBatch);
  }

  /**
   * Delete a product batch.
   *
   * @param batchId the ID of the batch to delete
   */
  @Transactional
  public void deleteProductBatch(Integer batchId) {
    if (!productBatchRepository.existsById(batchId)) {
      throw new NoSuchElementException("Product batch not found with ID: " + batchId);
    }
    productBatchRepository.deleteById(batchId);
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
        .isWater(productType.getIsWater())
        .build();
  }
}
