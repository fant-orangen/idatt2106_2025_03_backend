package stud.ntnu.backend.controller.household;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import stud.ntnu.backend.dto.inventory.*;
import stud.ntnu.backend.service.inventory.InventoryService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private Principal principal;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(principal.getName()).thenReturn("test@example.com");
    }

    // Test cases for getProductBatchesByProductType
    @Test
    void getProductBatchesByProductType_Success() {
        // Arrange
        Integer productTypeId = 1;
        Pageable pageable = mock(Pageable.class);
        List<ProductBatchDto> batches = Arrays.asList(
            ProductBatchDto.builder()
                .id(1)
                .productTypeId(1)
                .productTypeName("Rice")
                .dateAdded(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusDays(30))
                .number(10)
                .build(),
            ProductBatchDto.builder()
                .id(2)
                .productTypeId(1)
                .productTypeName("Rice")
                .dateAdded(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusDays(30))
                .number(5)
                .build()
        );
        Page<ProductBatchDto> expectedPage = new PageImpl<>(batches);
        when(inventoryService.getProductBatchesByProductType(productTypeId, pageable))
            .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ProductBatchDto>> response = 
            inventoryController.getProductBatchesByProductType(productTypeId, pageable);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(inventoryService).getProductBatchesByProductType(productTypeId, pageable);
    }

    @Test
    void getProductBatchesByProductType_EmptyResult() {
        // Arrange
        Integer productTypeId = 1;
        Pageable pageable = mock(Pageable.class);
        Page<ProductBatchDto> emptyPage = new PageImpl<>(List.of());
        when(inventoryService.getProductBatchesByProductType(productTypeId, pageable))
            .thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<ProductBatchDto>> response = 
            inventoryController.getProductBatchesByProductType(productTypeId, pageable);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    // Test cases for getTotalUnitsForProductType
    @Test
    void getTotalUnitsForProductType_Success() {
        // Arrange
        Integer productTypeId = 1;
        Integer householdId = 1;
        Integer expectedTotal = 15;
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        when(inventoryService.getTotalUnitsForProductType(productTypeId, householdId))
            .thenReturn(expectedTotal);

        // Act
        ResponseEntity<?> response = inventoryController.getTotalUnitsForProductType(productTypeId, principal);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedTotal, response.getBody());
        verify(inventoryService).getHouseholdIdByUserEmail(principal.getName());
        verify(inventoryService).getTotalUnitsForProductType(productTypeId, householdId);
    }

    @Test
    void getTotalUnitsForProductType_ProductTypeNotFound() {
        // Arrange
        Integer productTypeId = 1;
        Integer householdId = 1;
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        when(inventoryService.getTotalUnitsForProductType(productTypeId, householdId))
            .thenThrow(new NoSuchElementException("Product type not found"));

        // Act
        ResponseEntity<?> response = inventoryController.getTotalUnitsForProductType(productTypeId, principal);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getTotalUnitsForProductType_HouseholdMismatch() {
        // Arrange
        Integer productTypeId = 1;
        Integer householdId = 1;
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        when(inventoryService.getTotalUnitsForProductType(productTypeId, householdId))
            .thenThrow(new IllegalArgumentException("Product type does not belong to household"));

        // Act
        ResponseEntity<?> response = inventoryController.getTotalUnitsForProductType(productTypeId, principal);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Product type does not belong to household", response.getBody());
    }

    // Test cases for getAllFoodProductTypes
    @Test
    void getAllFoodProductTypes_Success() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        Integer householdId = 1;
        List<ProductTypeDto> productTypes = Arrays.asList(
            ProductTypeDto.builder()
                .id(1)
                .householdId(1)
                .name("Rice")
                .unit("kg")
                .caloriesPerUnit(350.0)
                .category("food")
                .build(),
            ProductTypeDto.builder()
                .id(2)
                .householdId(1)
                .name("Pasta")
                .unit("kg")
                .caloriesPerUnit(350.0)
                .category("food")
                .build()
        );
        Page<ProductTypeDto> expectedPage = new PageImpl<>(productTypes);
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        when(inventoryService.getAllFoodProductTypes(householdId, pageable))
            .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ProductTypeDto>> response = 
            inventoryController.getAllFoodProductTypes(pageable, principal);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(inventoryService).getAllFoodProductTypes(householdId, pageable);
    }

    @Test
    void getAllFoodProductTypes_UserNotInHousehold() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        when(inventoryService.getHouseholdIdByUserEmail(anyString()))
            .thenThrow(new NoSuchElementException("User not in household"));

        // Act
        ResponseEntity<Page<ProductTypeDto>> response = 
            inventoryController.getAllFoodProductTypes(pageable, principal);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    // Test cases for createFoodProductType
    @Test
    void createFoodProductType_Success() {
        // Arrange
        FoodProductTypeCreateDto createDto = FoodProductTypeCreateDto.builder()
            .name("Rice")
            .unit("kg")
            .caloriesPerUnit(350.0)
            .build();
        Integer householdId = 1;
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        when(inventoryService.createProductType(any())).thenReturn(ProductTypeDto.builder()
            .id(1)
            .householdId(householdId)
            .name("Rice")
            .unit("kg")
            .caloriesPerUnit(350.0)
            .category("food")
            .build());

        // Act
        ResponseEntity<?> response = inventoryController.createFoodProductType(createDto, principal);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(inventoryService).createProductType(any());
    }

    @Test
    void createFoodProductType_UserNotInHousehold() {
        // Arrange
        FoodProductTypeCreateDto createDto = FoodProductTypeCreateDto.builder()
            .name("Rice")
            .unit("kg")
            .caloriesPerUnit(350.0)
            .build();
        when(inventoryService.getHouseholdIdByUserEmail(anyString()))
            .thenThrow(new NoSuchElementException("User not in household"));

        // Act
        ResponseEntity<?> response = inventoryController.createFoodProductType(createDto, principal);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    // Test cases for createProductBatch
    @Test
    void createProductBatch_Success() {
        // Arrange
        ProductBatchCreateDto createDto = ProductBatchCreateDto.builder()
            .productTypeId(1)
            .number(10)
            .expirationTime(LocalDateTime.now().plusDays(30))
            .build();
        Integer householdId = 1;
        ProductBatchDto expectedResponse = ProductBatchDto.builder()
            .id(1)
            .productTypeId(1)
            .productTypeName("Rice")
            .dateAdded(LocalDateTime.now())
            .expirationTime(LocalDateTime.now().plusDays(30))
            .number(10)
            .build();
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        when(inventoryService.createProductBatch(any(), any())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = inventoryController.createProductBatch(createDto, principal);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(inventoryService).createProductBatch(any(), any());
    }

    @Test
    void createProductBatch_UserNotInHousehold() {
        // Arrange
        ProductBatchCreateDto createDto = ProductBatchCreateDto.builder()
            .productTypeId(1)
            .number(10)
            .expirationTime(LocalDateTime.now().plusDays(30))
            .build();
        when(inventoryService.getHouseholdIdByUserEmail(anyString()))
            .thenThrow(new NoSuchElementException("User not in household"));

        // Act
        ResponseEntity<?> response = inventoryController.createProductBatch(createDto, principal);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    // Test cases for updateProductBatch
    @Test
    void updateProductBatch_Success() {
        // Arrange
        Integer batchId = 1;
        Integer newNumberOfUnits = 15;
        ProductBatchDto expectedResponse = ProductBatchDto.builder()
            .id(batchId)
            .productTypeId(1)
            .productTypeName("Rice")
            .dateAdded(LocalDateTime.now())
            .expirationTime(LocalDateTime.now().plusDays(30))
            .number(newNumberOfUnits)
            .build();
        when(inventoryService.updateProductBatch(batchId, newNumberOfUnits)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = inventoryController.updateProductBatch(batchId, newNumberOfUnits);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(inventoryService).updateProductBatch(batchId, newNumberOfUnits);
    }

    @Test
    void updateProductBatch_BatchNotFound() {
        // Arrange
        Integer batchId = 1;
        Integer newNumberOfUnits = 15;
        when(inventoryService.updateProductBatch(batchId, newNumberOfUnits))
            .thenThrow(new NoSuchElementException("Batch not found"));

        // Act
        ResponseEntity<?> response = inventoryController.updateProductBatch(batchId, newNumberOfUnits);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Batch not found", response.getBody());
    }

    // Test cases for deleteProductBatch
    @Test
    void deleteProductBatch_Success() {
        // Arrange
        Integer batchId = 1;
        doNothing().when(inventoryService).deleteProductBatch(batchId);

        // Act
        ResponseEntity<?> response = inventoryController.deleteProductBatch(batchId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(inventoryService).deleteProductBatch(batchId);
    }

    @Test
    void deleteProductBatch_BatchNotFound() {
        // Arrange
        Integer batchId = 1;
        doThrow(new NoSuchElementException("Batch not found"))
            .when(inventoryService).deleteProductBatch(batchId);

        // Act
        ResponseEntity<?> response = inventoryController.deleteProductBatch(batchId);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Batch not found", response.getBody());
    }

    // Test cases for deleteProductType
    @Test
    void deleteProductType_Success() {
        // Arrange
        Integer productTypeId = 1;
        Integer householdId = 1;
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(householdId);
        doNothing().when(inventoryService).deleteProductType(productTypeId, householdId);

        // Act
        ResponseEntity<?> response = inventoryController.deleteProductType(productTypeId, principal);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(inventoryService).deleteProductType(productTypeId, householdId);
    }

    @Test
    void deleteProductType_UserNotInHousehold() {
        // Arrange
        Integer productTypeId = 1;
        when(inventoryService.getHouseholdIdByUserEmail(anyString()))
            .thenThrow(new NoSuchElementException("User not in household"));

        // Act
        ResponseEntity<?> response = inventoryController.deleteProductType(productTypeId, principal);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }
} 