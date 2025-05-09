package stud.ntnu.backend.service.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import stud.ntnu.backend.dto.inventory.ProductBatchDto;
import stud.ntnu.backend.dto.inventory.ProductTypeDto;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.group.GroupInventoryContribution;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.inventory.ProductType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.inventory.ProductTypeRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.SearchUtil;
import stud.ntnu.backend.service.inventory.InventoryService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupInventoryServiceTest {

    @Mock
    private GroupInventoryContributionRepository groupInventoryContributionRepository;
    @Mock
    private ProductTypeRepository productTypeRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ProductBatchRepository productBatchRepository;
    @Mock
    private HouseholdRepository householdRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SearchUtil searchUtil;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private GroupInventoryService groupInventoryService;

    private User testUser;
    private Household testHousehold;
    private Group testGroup;
    private ProductType testProductType;
    private ProductBatch testProductBatch;
    private GroupInventoryContribution testContribution;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");

        // Create test household
        testHousehold = new Household();
        testHousehold.setId(1);
        testHousehold.setName("Test Household");

        // Set user's household
        testUser.setHousehold(testHousehold);

        // Create test group
        testGroup = new Group();
        testGroup.setId(1);
        testGroup.setName("Test Group");

        // Create test product type
        testProductType = new ProductType();
        testProductType.setId(1);
        testProductType.setName("Test Product");
        testProductType.setHousehold(testHousehold);

        // Create test product batch
        testProductBatch = new ProductBatch();
        testProductBatch.setId(1);
        testProductBatch.setProductType(testProductType);
        testProductBatch.setDateAdded(LocalDateTime.now());

        // Create test contribution
        testContribution = new GroupInventoryContribution(testGroup, testHousehold, testProductBatch);
        testContribution.setId(1);
    }

    @Test
    void getContributedProductTypes_WithValidData_ShouldReturnProductTypes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductType> productTypePage = new PageImpl<>(Arrays.asList(testProductType));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupRepository.existsByIdAndMemberHouseholds_Id(anyInt(), anyInt())).thenReturn(true);
        when(productTypeRepository.findContributedProductTypesByGroup(anyInt(), any(Pageable.class)))
            .thenReturn(productTypePage);

        // Act
        Page<ProductTypeDto> result = groupInventoryService.getContributedProductTypes(1, "test@example.com", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductType.getId(), result.getContent().get(0).getId());
        assertEquals(testProductType.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getContributedProductTypes_WithNonMemberUser_ShouldThrowException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            groupInventoryService.getContributedProductTypes(1, "test@example.com", pageable));
    }

    @Test
    void getContributedProductBatchesByType_ShouldReturnBatches() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductBatch> batchPage = new PageImpl<>(Arrays.asList(testProductBatch));
        when(groupInventoryContributionRepository.findContributedProductBatchesByGroupAndProductType(
            anyInt(), anyInt(), any(Pageable.class))).thenReturn(batchPage);

        // Act
        Page<ProductBatchDto> result = groupInventoryService.getContributedProductBatchesByType(1, 1, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductBatch.getId(), result.getContent().get(0).getId());
        assertEquals(testProductType.getName(), result.getContent().get(0).getProductTypeName());
    }

    @Test
    void addBatchToGroup_WithValidData_ShouldCreateContribution() {
        // Arrange
        when(productBatchRepository.findById(anyInt())).thenReturn(Optional.of(testProductBatch));
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupInventoryContributionRepository.findAll()).thenReturn(Arrays.asList());
        when(groupInventoryContributionRepository.save(any(GroupInventoryContribution.class)))
            .thenReturn(testContribution);

        // Act
        boolean result = groupInventoryService.addBatchToGroup(1, 1, "test@example.com");

        // Assert
        assertTrue(result);
        verify(groupInventoryContributionRepository).save(any(GroupInventoryContribution.class));
    }

    @Test
    void addBatchToGroup_WithAlreadyContributedBatch_ShouldReturnFalse() {
        // Arrange
        when(productBatchRepository.findById(anyInt())).thenReturn(Optional.of(testProductBatch));
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup));
        when(groupInventoryContributionRepository.findAll()).thenReturn(Arrays.asList(testContribution));

        // Act
        boolean result = groupInventoryService.addBatchToGroup(1, 1, "test@example.com");

        // Assert
        assertFalse(result);
        verify(groupInventoryContributionRepository, never()).save(any(GroupInventoryContribution.class));
    }

    @Test
    void removeContributedBatch_WithValidData_ShouldRemoveContribution() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupInventoryContributionRepository.findByProductBatchId(anyInt()))
            .thenReturn(Optional.of(testContribution));

        // Act
        boolean result = groupInventoryService.removeContributedBatch(1, "test@example.com");

        // Assert
        assertTrue(result);
        verify(groupInventoryContributionRepository).delete(any(GroupInventoryContribution.class));
    }

    @Test
    void removeContributedBatch_WithUnauthorizedUser_ShouldThrowException() {
        // Arrange
        Household otherHousehold = new Household();
        otherHousehold.setId(2);
        testContribution.setHousehold(otherHousehold);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupInventoryContributionRepository.findByProductBatchId(anyInt()))
            .thenReturn(Optional.of(testContribution));

        // Act & Assert
        assertThrows(SecurityException.class, () ->
            groupInventoryService.removeContributedBatch(1, "test@example.com"));
    }

    @Test
    void getTotalUnitsForProductType_WithValidData_ShouldReturnTotal() {
        // Arrange
        when(productTypeRepository.existsById(anyInt())).thenReturn(true);
        when(groupRepository.existsById(anyInt())).thenReturn(true);
        when(groupInventoryContributionRepository.sumTotalUnitsForProductTypeAndGroup(anyInt(), anyInt()))
            .thenReturn(10);

        // Act
        Integer result = groupInventoryService.getTotalUnitsForProductType(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(10, result);
    }

    @Test
    void getTotalUnitsForProductType_WithInvalidProductType_ShouldThrowException() {
        // Arrange
        when(productTypeRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            groupInventoryService.getTotalUnitsForProductType(1, 1));
    }

    @Test
    void searchContributedProductTypes_WithValidData_ShouldReturnResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductType> searchResults = new PageImpl<>(Arrays.asList(testProductType));
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupRepository.existsByIdAndMemberHouseholds_Id(anyInt(), anyInt())).thenReturn(true);
        when(groupInventoryContributionRepository.findProductTypeIdsContributedToGroup(anyInt()))
            .thenReturn(Arrays.asList(1));
        when(searchUtil.searchByDescription(eq(ProductType.class), eq("name"), anyString(), any(Pageable.class)))
            .thenReturn(searchResults);

        // Act
        Page<ProductTypeDto> result = groupInventoryService.searchContributedProductTypes(1, "test", "test@example.com", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductType.getId(), result.getContent().get(0).getId());
        assertEquals(testProductType.getName(), result.getContent().get(0).getName());
    }
} 