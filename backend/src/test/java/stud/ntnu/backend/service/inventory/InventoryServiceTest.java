package stud.ntnu.backend.service.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import stud.ntnu.backend.dto.inventory.*;
import stud.ntnu.backend.event.InventoryChangeEvent;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.Product;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.household.HouseholdMemberRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.inventory.ProductRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.SearchUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductBatchRepository productBatchRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private SearchUtil searchUtil;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private HouseholdMemberRepository householdMemberRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<InventoryChangeEvent> eventCaptor;

    private Household testHousehold;
    private User testUser;
    private ProductType testFoodProductType;
    private ProductType testWaterProductType;
    private ProductBatch testProductBatch;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Set up test household
        testHousehold = new Household();
        testHousehold.setId(1);
        testHousehold.setName("Test Household");

        // Set up test user
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setHousehold(testHousehold);
        testUser.setKcalRequirement(2000);

        // Set up test food product type
        testFoodProductType = new ProductType();
        testFoodProductType.setId(1);
        testFoodProductType.setName("Rice");
        testFoodProductType.setUnit("kg");
        testFoodProductType.setCaloriesPerUnit(350.0);
        testFoodProductType.setCategory("food");
        testFoodProductType.setHousehold(testHousehold);

        // Set up test water product type
        testWaterProductType = new ProductType();
        testWaterProductType.setId(2);
        testWaterProductType.setName("Bottled Water");
        testWaterProductType.setUnit("l");
        testWaterProductType.setCaloriesPerUnit(null);
        testWaterProductType.setCategory("water");
        testWaterProductType.setHousehold(testHousehold);

        // Set up test product batch
        testProductBatch = new ProductBatch();
        testProductBatch.setId(1);
        testProductBatch.setProductType(testFoodProductType);
        testProductBatch.setDateAdded(LocalDateTime.now());
        testProductBatch.setExpirationTime(LocalDateTime.now().plusDays(30));
        testProductBatch.setNumber(10);

        // Set up test product
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setProductType(testFoodProductType);
    }

    @Nested
    class ProductTests {
        @Test
        void getAllProducts_ShouldReturnAllProducts() {
            // Arrange
            List<Product> expectedProducts = Arrays.asList(testProduct);
            when(productRepository.findAll()).thenReturn(expectedProducts);

            // Act
            List<Product> result = inventoryService.getAllProducts();

            // Assert
            assertEquals(expectedProducts.size(), result.size());
            assertEquals(expectedProducts.get(0), result.get(0));
            verify(productRepository).findAll();
        }

        @Test
        void getProductById_WithExistingId_ShouldReturnProduct() {
            // Arrange
            when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

            // Act
            Optional<Product> result = inventoryService.getProductById(1);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testProduct, result.get());
            verify(productRepository).findById(1);
        }

        @Test
        void getProductById_WithNonExistingId_ShouldReturnEmptyOptional() {
            // Arrange
            when(productRepository.findById(999)).thenReturn(Optional.empty());

            // Act
            Optional<Product> result = inventoryService.getProductById(999);

            // Assert
            assertFalse(result.isPresent());
            verify(productRepository).findById(999);
        }

        @Test
        void saveProduct_ShouldSaveAndReturnProduct() {
            // Arrange
            when(productRepository.save(testProduct)).thenReturn(testProduct);

            // Act
            Product result = inventoryService.saveProduct(testProduct);

            // Assert
            assertEquals(testProduct, result);
            verify(productRepository).save(testProduct);
        }

        @Test
        void deleteProduct_ShouldCallRepositoryDeleteById() {
            // Act
            inventoryService.deleteProduct(1);

            // Assert
            verify(productRepository).deleteById(1);
        }
    }

    @Nested
    class ProductTypeTests {
        @Test
        void createProductType_WithValidData_ShouldCreateAndReturnProductType() {
            // Arrange
            FoodProductTypeCreateDto createDto = new FoodProductTypeCreateDto();
            createDto.setHouseholdId(1);
            createDto.setName("Rice");
            createDto.setUnit("kg");
            createDto.setCaloriesPerUnit(350.0);
            createDto.setCategory("food");

            when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));
            when(productTypeRepository.save(any(ProductType.class))).thenReturn(testFoodProductType);

            // Act
            ProductTypeDto result = inventoryService.createProductType(createDto);

            // Assert
            assertNotNull(result);
            assertEquals(testFoodProductType.getId(), result.getId());
            assertEquals(testFoodProductType.getName(), result.getName());
            assertEquals(testFoodProductType.getUnit(), result.getUnit());
            assertEquals(testFoodProductType.getCaloriesPerUnit(), result.getCaloriesPerUnit());
            assertEquals(testFoodProductType.getCategory(), result.getCategory());
            verify(householdRepository).findById(1);
            verify(productTypeRepository).save(any(ProductType.class));
        }

        @Test
        void createProductType_WithNonExistingHousehold_ShouldThrowException() {
            // Arrange
            FoodProductTypeCreateDto createDto = new FoodProductTypeCreateDto();
            createDto.setHouseholdId(999);
            createDto.setName("Rice");
            createDto.setUnit("kg");
            createDto.setCaloriesPerUnit(350.0);
            createDto.setCategory("food");

            when(householdRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.createProductType(createDto);
            });
            verify(householdRepository).findById(999);
            verify(productTypeRepository, never()).save(any(ProductType.class));
        }

        @Test
        void createWaterProductType_WithValidData_ShouldCreateAndReturnProductType() {
            // Arrange
            WaterProductTypeCreateDto createDto = new WaterProductTypeCreateDto();
            createDto.setHouseholdId(1);
            createDto.setName("Bottled Water");
            createDto.setUnit("l");
            createDto.setCategory("water");

            when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));
            when(productTypeRepository.save(any(ProductType.class))).thenReturn(testWaterProductType);

            // Act
            ProductTypeDto result = inventoryService.createWaterProductType(createDto);

            // Assert
            assertNotNull(result);
            assertEquals(testWaterProductType.getId(), result.getId());
            assertEquals(testWaterProductType.getName(), result.getName());
            assertEquals(testWaterProductType.getUnit(), result.getUnit());
            assertNull(result.getCaloriesPerUnit()); // Water product types have null calories
            assertEquals(testWaterProductType.getCategory(), result.getCategory());
            verify(householdRepository).findById(1);
            verify(productTypeRepository).save(any(ProductType.class));
        }

        @Test
        void getProductTypesByHousehold_ShouldReturnProductTypes() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductType> expectedPage = new PageImpl<>(Arrays.asList(testFoodProductType, testWaterProductType));
            when(productTypeRepository.findByHouseholdId(1, pageable)).thenReturn(expectedPage);

            // Act
            Page<ProductTypeDto> result = inventoryService.getProductTypesByHousehold(1, pageable);

            // Assert
            assertEquals(2, result.getTotalElements());
            assertEquals(testFoodProductType.getId(), result.getContent().get(0).getId());
            assertEquals(testWaterProductType.getId(), result.getContent().get(1).getId());
            verify(productTypeRepository).findByHouseholdId(1, pageable);
        }

        @Test
        void getAllFoodProductTypes_ShouldReturnFoodProductTypes() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductType> expectedPage = new PageImpl<>(Arrays.asList(testFoodProductType));
            when(productTypeRepository.findByHouseholdIdAndCategory(1, "food", pageable)).thenReturn(expectedPage);

            // Act
            Page<ProductTypeDto> result = inventoryService.getAllFoodProductTypes(1, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(testFoodProductType.getId(), result.getContent().get(0).getId());
            assertEquals(testFoodProductType.getName(), result.getContent().get(0).getName());
            verify(productTypeRepository).findByHouseholdIdAndCategory(1, "food", pageable);
        }

        @Test
        void deleteProductType_WithValidData_ShouldDeleteProductType() {
            // Arrange
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(testFoodProductType));

            // Act
            inventoryService.deleteProductType(1, 1);

            // Assert
            verify(productTypeRepository).findById(1);
            verify(productTypeRepository).deleteById(1);
            verify(eventPublisher).publishEvent(any(InventoryChangeEvent.class));
        }

        @Test
        void deleteProductType_WithNonExistingProductType_ShouldThrowException() {
            // Arrange
            when(productTypeRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.deleteProductType(999, 1);
            });
            verify(productTypeRepository).findById(999);
            verify(productTypeRepository, never()).deleteById(anyInt());
        }

        @Test
        void deleteProductType_WithDifferentHouseholdId_ShouldThrowException() {
            // Arrange
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(testFoodProductType));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                inventoryService.deleteProductType(1, 999); // Different household ID
            });
            verify(productTypeRepository).findById(1);
            verify(productTypeRepository, never()).deleteById(anyInt());
        }
    }

    @Nested
    class ProductBatchTests {
        @Test
        void createProductBatch_WithValidData_ShouldCreateAndReturnProductBatch() {
            // Arrange
            ProductBatchCreateDto createDto = new ProductBatchCreateDto();
            createDto.setProductTypeId(1);
            createDto.setNumber(10);
            createDto.setExpirationTime(LocalDateTime.now().plusDays(30));

            when(productTypeRepository.findById(1)).thenReturn(Optional.of(testFoodProductType));
            when(productBatchRepository.save(any(ProductBatch.class))).thenReturn(testProductBatch);

            // Act
            ProductBatchDto result = inventoryService.createProductBatch(createDto, 1);

            // Assert
            assertNotNull(result);
            assertEquals(testProductBatch.getId(), result.getId());
            assertEquals(testProductBatch.getProductType().getId(), result.getProductTypeId());
            assertEquals(testProductBatch.getNumber(), result.getNumber());
            verify(productTypeRepository).findById(1);
            verify(productBatchRepository).save(any(ProductBatch.class));
        }

        @Test
        void createProductBatch_WithNonExistingProductType_ShouldThrowException() {
            // Arrange
            ProductBatchCreateDto createDto = new ProductBatchCreateDto();
            createDto.setProductTypeId(999);
            createDto.setNumber(10);
            createDto.setExpirationTime(LocalDateTime.now().plusDays(30));

            when(productTypeRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.createProductBatch(createDto, 1);
            });
            verify(productTypeRepository).findById(999);
            verify(productBatchRepository, never()).save(any(ProductBatch.class));
        }

        @Test
        void createProductBatch_WithDifferentHouseholdId_ShouldThrowException() {
            // Arrange
            ProductBatchCreateDto createDto = new ProductBatchCreateDto();
            createDto.setProductTypeId(1);
            createDto.setNumber(10);
            createDto.setExpirationTime(LocalDateTime.now().plusDays(30));

            when(productTypeRepository.findById(1)).thenReturn(Optional.of(testFoodProductType));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                inventoryService.createProductBatch(createDto, 999); // Different household ID
            });
            verify(productTypeRepository).findById(1);
            verify(productBatchRepository, never()).save(any(ProductBatch.class));
        }

        @Test
        void getProductBatchesByProductType_WithValidData_ShouldReturnProductBatches() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductBatch> expectedPage = new PageImpl<>(Arrays.asList(testProductBatch));
            when(productTypeRepository.existsById(1)).thenReturn(true);
            when(productBatchRepository.findByProductTypeId(1, pageable)).thenReturn(expectedPage);

            // Act
            Page<ProductBatchDto> result = inventoryService.getProductBatchesByProductType(1, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(testProductBatch.getId(), result.getContent().get(0).getId());
            assertEquals(testProductBatch.getProductType().getName(), result.getContent().get(0).getProductTypeName());
            verify(productTypeRepository).existsById(1);
            verify(productBatchRepository).findByProductTypeId(1, pageable);
        }

        @Test
        void getProductBatchesByProductType_WithNonExistingProductType_ShouldThrowException() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(productTypeRepository.existsById(999)).thenReturn(false);

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.getProductBatchesByProductType(999, pageable);
            });
            verify(productTypeRepository).existsById(999);
            verify(productBatchRepository, never()).findByProductTypeId(anyInt(), any(Pageable.class));
        }

        @Test
        void updateProductBatch_WithValidData_ShouldUpdateAndReturnProductBatch() {
            // Arrange
            when(productBatchRepository.findById(1)).thenReturn(Optional.of(testProductBatch));
            when(productBatchRepository.save(any(ProductBatch.class))).thenReturn(testProductBatch);

            // Act
            ProductBatchDto result = inventoryService.updateProductBatch(1, 15);

            // Assert
            assertNotNull(result);
            assertEquals(testProductBatch.getId(), result.getId());
            assertEquals(15, testProductBatch.getNumber()); // Verify number was updated
            verify(productBatchRepository).findById(1);
            verify(productBatchRepository).save(testProductBatch);
            verify(eventPublisher).publishEvent(any(InventoryChangeEvent.class));
        }

        @Test
        void updateProductBatch_WithNegativeNumber_ShouldThrowException() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                inventoryService.updateProductBatch(1, -5);
            });
            verify(productBatchRepository, never()).findById(anyInt());
            verify(productBatchRepository, never()).save(any(ProductBatch.class));
        }

        @Test
        void updateProductBatch_WithNonExistingBatch_ShouldThrowException() {
            // Arrange
            when(productBatchRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.updateProductBatch(999, 15);
            });
            verify(productBatchRepository).findById(999);
            verify(productBatchRepository, never()).save(any(ProductBatch.class));
        }

        @Test
        void deleteProductBatch_WithValidData_ShouldDeleteProductBatch() {
            // Arrange
            when(productBatchRepository.findById(1)).thenReturn(Optional.of(testProductBatch));

            // Act
            inventoryService.deleteProductBatch(1);

            // Assert
            verify(productBatchRepository).findById(1);
            verify(productBatchRepository).deleteById(1);
            verify(eventPublisher).publishEvent(any(InventoryChangeEvent.class));
        }

        @Test
        void deleteProductBatch_WithNonExistingBatch_ShouldThrowException() {
            // Arrange
            when(productBatchRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.deleteProductBatch(999);
            });
            verify(productBatchRepository).findById(999);
            verify(productBatchRepository, never()).deleteById(anyInt());
        }
    }

    @Nested
    class InventoryCalculationTests {
        @Test
        void getTotalUnitsForProductType_WithValidData_ShouldReturnTotal() {
            // Arrange
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(testFoodProductType));
            when(productBatchRepository.sumNumberByProductTypeId(1)).thenReturn(10);

            // Act
            Integer result = inventoryService.getTotalUnitsForProductType(1, 1);

            // Assert
            assertEquals(10, result);
            verify(productTypeRepository).findById(1);
            verify(productBatchRepository).sumNumberByProductTypeId(1);
        }

        @Test
        void getTotalUnitsForProductType_WithNonExistingProductType_ShouldThrowException() {
            // Arrange
            when(productTypeRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.getTotalUnitsForProductType(999, 1);
            });
            verify(productTypeRepository).findById(999);
            verify(productBatchRepository, never()).sumNumberByProductTypeId(anyInt());
        }

        @Test
        void getTotalUnitsForProductType_WithDifferentHouseholdId_ShouldThrowException() {
            // Arrange
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(testFoodProductType));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                inventoryService.getTotalUnitsForProductType(1, 999); // Different household ID
            });
            verify(productTypeRepository).findById(1);
            verify(productBatchRepository, never()).sumNumberByProductTypeId(anyInt());
        }

        @Test
        void getTotalLitresOfWater_ShouldReturnTotal() {
            // Arrange
            when(productBatchRepository.sumTotalLitresOfWater()).thenReturn(20);

            // Act
            Integer result = inventoryService.getTotalLitresOfWater();

            // Assert
            assertEquals(20, result);
            verify(productBatchRepository).sumTotalLitresOfWater();
        }

        @Test
        void getTotalLitresOfWaterByHousehold_ShouldReturnTotal() {
            // Arrange
            when(productBatchRepository.sumTotalLitresOfWaterByHousehold(1)).thenReturn(15);

            // Act
            Integer result = inventoryService.getTotalLitresOfWaterByHousehold(1);

            // Assert
            assertEquals(15, result);
            verify(productBatchRepository).sumTotalLitresOfWaterByHousehold(1);
        }

        @Test
        void getTotalCaloriesByHousehold_ShouldReturnTotal() {
            // Arrange
            when(productBatchRepository.sumTotalCaloriesByHousehold(1)).thenReturn(5000);

            // Act
            Integer result = inventoryService.getTotalCaloriesByHousehold(1);

            // Assert
            assertEquals(5000, result);
            verify(productBatchRepository).sumTotalCaloriesByHousehold(1);
        }

        @Test
        void getHouseholdWaterRequirement_WithValidHousehold_ShouldReturnRequirement() {
            // Arrange
            when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));
            when(userRepository.countByHouseholdId(1)).thenReturn(2);
            when(householdMemberRepository.countByHouseholdIdAndTypeNot(1, "pet")).thenReturn(1);

            // Act
            Integer result = inventoryService.getHouseholdWaterRequirement(1);

            // Assert
            assertEquals(9, result); // (2 users + 1 member) * 3L = 9L
            verify(householdRepository).findById(1);
            verify(userRepository).countByHouseholdId(1);
            verify(householdMemberRepository).countByHouseholdIdAndTypeNot(1, "pet");
        }

        @Test
        void getHouseholdWaterRequirement_WithNonExistingHousehold_ShouldThrowException() {
            // Arrange
            when(householdRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.getHouseholdWaterRequirement(999);
            });
            verify(householdRepository).findById(999);
            verify(userRepository, never()).countByHouseholdId(anyInt());
        }

        @Test
        void getHouseholdCalorieRequirement_ShouldReturnRequirement() {
            // Arrange
            when(userRepository.sumKcalRequirementByHouseholdId(1)).thenReturn(4000);
            when(householdMemberRepository.sumKcalRequirementByHouseholdId(1)).thenReturn(1500);

            // Act
            Integer result = inventoryService.getHouseholdCalorieRequirement(1);

            // Assert
            assertEquals(5500, result); // 4000 + 1500 = 5500
            verify(userRepository).sumKcalRequirementByHouseholdId(1);
            verify(householdMemberRepository).sumKcalRequirementByHouseholdId(1);
        }

        @Test
        void getWaterDaysRemaining_WithNonZeroRequirement_ShouldReturnDays() {
            // Arrange
            when(productBatchRepository.sumTotalLitresOfWaterByHousehold(1)).thenReturn(30);
            // Mock the getHouseholdWaterRequirement method to return 6L per day
            when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));
            when(userRepository.countByHouseholdId(1)).thenReturn(1);
            when(householdMemberRepository.countByHouseholdIdAndTypeNot(1, "pet")).thenReturn(1);

            // Act
            Double result = inventoryService.getWaterDaysRemaining(1);

            // Assert
            assertEquals(5.0, result); // 30L / 6L per day = 5 days
            verify(productBatchRepository).sumTotalLitresOfWaterByHousehold(1);
            verify(householdRepository).findById(1);
            verify(userRepository).countByHouseholdId(1);
            verify(householdMemberRepository).countByHouseholdIdAndTypeNot(1, "pet");
        }

        @Test
        void getWaterDaysRemaining_WithZeroRequirement_ShouldReturnZero() {
            // Arrange
            when(productBatchRepository.sumTotalLitresOfWaterByHousehold(1)).thenReturn(30);
            // Mock the getHouseholdWaterRequirement method to return 0L per day
            when(householdRepository.findById(1)).thenReturn(Optional.of(testHousehold));
            when(userRepository.countByHouseholdId(1)).thenReturn(0);
            when(householdMemberRepository.countByHouseholdIdAndTypeNot(1, "pet")).thenReturn(0);

            // Act
            Double result = inventoryService.getWaterDaysRemaining(1);

            // Assert
            assertEquals(0.0, result);
            verify(productBatchRepository).sumTotalLitresOfWaterByHousehold(1);
            verify(householdRepository).findById(1);
            verify(userRepository).countByHouseholdId(1);
            verify(householdMemberRepository).countByHouseholdIdAndTypeNot(1, "pet");
        }

        @Test
        void getFoodDaysRemaining_WithNonZeroRequirement_ShouldReturnDays() {
            // Arrange
            when(productBatchRepository.sumTotalCaloriesByHousehold(1)).thenReturn(10000);
            when(userRepository.sumKcalRequirementByHouseholdId(1)).thenReturn(2000);
            when(householdMemberRepository.sumKcalRequirementByHouseholdId(1)).thenReturn(0);

            // Act
            Double result = inventoryService.getFoodDaysRemaining(1);

            // Assert
            assertEquals(5.0, result); // 10000 kcal / 2000 kcal per day = 5 days
            verify(productBatchRepository).sumTotalCaloriesByHousehold(1);
            verify(userRepository).sumKcalRequirementByHouseholdId(1);
            verify(householdMemberRepository).sumKcalRequirementByHouseholdId(1);
        }

        @Test
        void getFoodDaysRemaining_WithZeroRequirement_ShouldReturnZero() {
            // Arrange
            when(productBatchRepository.sumTotalCaloriesByHousehold(1)).thenReturn(10000);
            when(userRepository.sumKcalRequirementByHouseholdId(1)).thenReturn(0);
            when(householdMemberRepository.sumKcalRequirementByHouseholdId(1)).thenReturn(0);

            // Act
            Double result = inventoryService.getFoodDaysRemaining(1);

            // Assert
            assertEquals(0.0, result);
            verify(productBatchRepository).sumTotalCaloriesByHousehold(1);
            verify(userRepository).sumKcalRequirementByHouseholdId(1);
            verify(householdMemberRepository).sumKcalRequirementByHouseholdId(1);
        }
    }

    @Nested
    class SearchAndExpiringProductTests {
        @Test
        void searchProductTypesByNameAndCategoryAndHousehold_ShouldReturnFilteredResults() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductType> searchResults = new PageImpl<>(Arrays.asList(testFoodProductType, testWaterProductType));
            when(searchUtil.searchByDescription(eq(ProductType.class), eq("name"), anyString(), any(Pageable.class)))
                .thenReturn(searchResults);

            // Act
            Page<ProductTypeDto> result = inventoryService.searchProductTypesByNameAndCategoryAndHousehold(
                1, "food", "Rice", pageable);

            // Assert
            assertEquals(1, result.getTotalElements()); // Only the food product type should be returned
            assertEquals(testFoodProductType.getId(), result.getContent().get(0).getId());
            verify(searchUtil).searchByDescription(eq(ProductType.class), eq("name"), eq("Rice"), eq(pageable));
        }

        @Test
        void getExpiringProductTypes_ShouldReturnProductTypesWithExpiringBatches() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductType> productTypes = new PageImpl<>(Arrays.asList(testFoodProductType));
            Page<ProductBatch> productBatches = new PageImpl<>(Arrays.asList(testProductBatch));

            when(productTypeRepository.findByHouseholdIdAndCategory(eq(1), eq("food"), any(Pageable.class)))
                .thenReturn(productTypes);
            when(productBatchRepository.findByProductTypeId(eq(1), any(Pageable.class)))
                .thenReturn(productBatches);

            // Set the expiration time to be within the next 7 days
            testProductBatch.setExpirationTime(LocalDateTime.now().plusDays(3));

            // Act
            Page<ProductTypeDto> result = inventoryService.getExpiringProductTypes(
                1, "food", 7, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(testFoodProductType.getId(), result.getContent().get(0).getId());
            verify(productTypeRepository).findByHouseholdIdAndCategory(eq(1), eq("food"), any(Pageable.class));
            verify(productBatchRepository).findByProductTypeId(eq(1), any(Pageable.class));
        }

        @Test
        void getExpiringProductTypes_WithNoExpiringBatches_ShouldReturnEmptyPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductType> productTypes = new PageImpl<>(Arrays.asList(testFoodProductType));
            Page<ProductBatch> productBatches = new PageImpl<>(Arrays.asList(testProductBatch));

            when(productTypeRepository.findByHouseholdIdAndCategory(eq(1), eq("food"), any(Pageable.class)))
                .thenReturn(productTypes);
            when(productBatchRepository.findByProductTypeId(eq(1), any(Pageable.class)))
                .thenReturn(productBatches);

            // Set the expiration time to be far in the future (not expiring soon)
            testProductBatch.setExpirationTime(LocalDateTime.now().plusDays(30));

            // Act
            Page<ProductTypeDto> result = inventoryService.getExpiringProductTypes(
                1, "food", 7, pageable);

            // Assert
            assertEquals(0, result.getTotalElements());
            verify(productTypeRepository).findByHouseholdIdAndCategory(eq(1), eq("food"), any(Pageable.class));
            verify(productBatchRepository).findByProductTypeId(eq(1), any(Pageable.class));
        }

        @Test
        void getExpiringProductBatchesByProductType_ShouldReturnExpiringBatches() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoffDate = now.plusDays(7);
            Page<ProductBatch> productBatches = new PageImpl<>(Arrays.asList(testProductBatch));

            when(productTypeRepository.existsById(1)).thenReturn(true);
            when(productBatchRepository.findByProductTypeIdAndExpirationTimeBetween(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(productBatches);

            // Act
            Page<ProductBatchDto> result = inventoryService.getExpiringProductBatchesByProductType(1, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(testProductBatch.getId(), result.getContent().get(0).getId());
            verify(productTypeRepository).existsById(1);
            verify(productBatchRepository).findByProductTypeIdAndExpirationTimeBetween(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
        }

        @Test
        void getExpiringProductBatchesByProductType_WithNonExistingProductType_ShouldThrowException() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(productTypeRepository.existsById(999)).thenReturn(false);

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                inventoryService.getExpiringProductBatchesByProductType(999, pageable);
            });
            verify(productTypeRepository).existsById(999);
            verify(productBatchRepository, never()).findByProductTypeIdAndExpirationTimeBetween(
                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        }
    }
}
