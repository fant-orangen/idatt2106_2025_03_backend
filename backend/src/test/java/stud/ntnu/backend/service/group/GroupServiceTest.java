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

import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.dto.household.HouseholdDto;
import stud.ntnu.backend.model.group.Group;
import stud.ntnu.backend.model.group.GroupInvitation;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.group.GroupInvitationRepository;
import stud.ntnu.backend.repository.group.GroupMembershipRepository;
import stud.ntnu.backend.repository.group.GroupRepository;
import stud.ntnu.backend.repository.household.HouseholdAdminRepository;
import stud.ntnu.backend.repository.household.HouseholdRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.inventory.InventoryService;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMembershipRepository groupMembershipRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private HouseholdAdminRepository householdAdminRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HouseholdRepository householdRepository;
    @Mock
    private GroupInvitationRepository groupInvitationRepository;
    @Mock
    private GroupInventoryContributionRepository groupInventoryContributionRepository;

    @InjectMocks
    private GroupService groupService;

    private User testUser;
    private Household testHousehold;
    private Group testGroup;
    private GroupMembership testMembership;
    private GroupInvitation testInvitation;

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

        // Create test group
        testGroup = new Group();
        testGroup.setId(1);
        testGroup.setName("Test Group");
        testGroup.setCreatedAt(LocalDateTime.now());

        // Create test membership
        testMembership = new GroupMembership(testGroup, testHousehold, testUser);
        testMembership.setJoinedAt(LocalDateTime.now());

        // Create test invitation
        testInvitation = new GroupInvitation(testGroup, "inviter@example.com", testHousehold, 
            LocalDateTime.now().plusDays(30));
        testInvitation.setId(1);
    }

    @Test
    void createGroup_WithValidData_ShouldCreateGroup() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(householdAdminRepository.existsByUser(any(User.class))).thenReturn(true);
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(1);
        when(householdRepository.findById(anyInt())).thenReturn(Optional.of(testHousehold));
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);
        when(groupMembershipRepository.save(any(GroupMembership.class))).thenReturn(testMembership);

        // Act
        boolean result = groupService.createGroup("Test Group", "test@example.com");

        // Assert
        assertTrue(result);
        verify(groupRepository).save(any(Group.class));
        verify(groupMembershipRepository).save(any(GroupMembership.class));
    }

    @Test
    void createGroup_WithNonAdminUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(householdAdminRepository.existsByUser(any(User.class))).thenReturn(false);

        // Act
        boolean result = groupService.createGroup("Test Group", "test@example.com");

        // Assert
        assertFalse(result);
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void getCurrentUserGroup_WithValidMembership_ShouldReturnGroupSummary() {
        // Arrange
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(1);
        when(groupMembershipRepository.findCurrentByHouseholdId(anyInt(), any(LocalDateTime.class)))
            .thenReturn(Optional.of(testMembership));

        // Act
        GroupSummaryDto result = groupService.getCurrentUserGroup("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testGroup.getId(), result.getId());
        assertEquals(testGroup.getName(), result.getName());
        assertEquals(testGroup.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void getCurrentUserGroup_WithNoMembership_ShouldReturnNull() {
        // Arrange
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(1);
        when(groupMembershipRepository.findCurrentByHouseholdId(anyInt(), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());

        // Act
        GroupSummaryDto result = groupService.getCurrentUserGroup("test@example.com");

        // Assert
        assertNull(result);
    }

    @Test
    void inviteHouseholdToGroup_WithValidData_ShouldCreateInvitation() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup));
        when(householdRepository.findByName(anyString())).thenReturn(Optional.of(testHousehold));
        when(groupMembershipRepository.findCurrentByHouseholdIdAndGroupId(anyInt(), anyInt(), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());
        when(groupInvitationRepository.existsPendingInvitation(anyInt(), anyInt(), any(LocalDateTime.class)))
            .thenReturn(false);
        when(groupInvitationRepository.save(any(GroupInvitation.class))).thenReturn(testInvitation);

        // Act
        boolean result = groupService.inviteHouseholdToGroup("Test Household", 1, "test@example.com");

        // Assert
        assertTrue(result);
        verify(groupInvitationRepository).save(any(GroupInvitation.class));
    }

    @Test
    void inviteHouseholdToGroup_WithExistingMembership_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup));
        when(householdRepository.findByName(anyString())).thenReturn(Optional.of(testHousehold));
        when(groupMembershipRepository.findCurrentByHouseholdIdAndGroupId(anyInt(), anyInt(), any(LocalDateTime.class)))
            .thenReturn(Optional.of(testMembership));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            groupService.inviteHouseholdToGroup("Test Household", 1, "test@example.com"));
    }

    @Test
    void acceptInvitation_WithValidData_ShouldCreateMembership() {
        // Arrange
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(1);
        when(groupInvitationRepository.findById(anyInt())).thenReturn(Optional.of(testInvitation));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.save(any(GroupMembership.class))).thenReturn(testMembership);

        // Act
        boolean result = groupService.acceptInvitation(1, "test@example.com");

        // Assert
        assertTrue(result);
        verify(groupInvitationRepository).save(any(GroupInvitation.class));
        verify(groupMembershipRepository).save(any(GroupMembership.class));
    }

    @Test
    void acceptInvitation_WithInvalidInvitation_ShouldThrowException() {
        // Arrange
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(1);
        when(groupInvitationRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            groupService.acceptInvitation(1, "test@example.com"));
    }

    @Test
    void getCurrentHouseholdsInGroup_ShouldReturnHouseholdList() {
        // Arrange
        List<GroupMembership> memberships = Arrays.asList(testMembership);
        when(groupMembershipRepository.findAllCurrentByGroupId(anyInt(), any(LocalDateTime.class)))
            .thenReturn(memberships);

        // Act
        List<HouseholdDto> result = groupService.getCurrentHouseholdsInGroup(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHousehold.getId(), result.get(0).getId());
        assertEquals(testHousehold.getName(), result.get(0).getName());
    }

    @Test
    void removeHouseholdFromGroup_WithValidData_ShouldRemoveMembership() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(householdAdminRepository.existsByUser(any(User.class))).thenReturn(true);
        when(inventoryService.getHouseholdIdByUserEmail(anyString())).thenReturn(1);
        when(groupMembershipRepository.findCurrentByHouseholdIdAndGroupId(anyInt(), anyInt(), any(LocalDateTime.class)))
            .thenReturn(Optional.of(testMembership));
        when(groupMembershipRepository.findAllCurrentByGroupId(anyInt(), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testMembership));
        doNothing().when(groupInventoryContributionRepository).deleteByGroupIdAndHouseholdId(anyInt(), anyInt());

        // Act
        boolean result = groupService.removeHouseholdFromGroup("test@example.com", 1);

        // Assert
        assertTrue(result);
        verify(groupMembershipRepository).save(any(GroupMembership.class));
        verify(groupInventoryContributionRepository).deleteByGroupIdAndHouseholdId(anyInt(), anyInt());
    }

    @Test
    void removeHouseholdFromGroup_WithNonAdminUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(householdAdminRepository.existsByUser(any(User.class))).thenReturn(false);

        // Act
        boolean result = groupService.removeHouseholdFromGroup("test@example.com", 1);

        // Assert
        assertFalse(result);
        verify(groupMembershipRepository, never()).save(any(GroupMembership.class));
    }
} 