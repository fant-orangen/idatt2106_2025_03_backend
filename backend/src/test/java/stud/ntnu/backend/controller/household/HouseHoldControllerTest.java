package stud.ntnu.backend.controller.household;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import stud.ntnu.backend.dto.household.*;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.Invitation;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.household.HouseholdService;
import stud.ntnu.backend.service.user.InvitationService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HouseHoldControllerTest {

    @Mock
    private HouseholdService householdService;

    @Mock
    private InvitationService invitationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private HouseholdController householdController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(principal.getName()).thenReturn("test@example.com");
    }

    // Positive test cases

    @Test
    void createHousehold_Success() {
        // Arrange
        HouseholdCreateRequestDto requestDto = new HouseholdCreateRequestDto();
        requestDto.setName("Test Household");
        requestDto.setAddress("Test Address");
        requestDto.setPopulationCount(2);

        Household expectedHousehold = new Household("Test Household", "Test Address", 2);
        when(householdService.createHousehold(any())).thenReturn(expectedHousehold);

        // Act
        ResponseEntity<?> response = householdController.createHousehold(requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedHousehold, response.getBody());
    }

    @Test
    void inviteToHousehold_Success() {
        // Arrange
        HouseholdInviteRequestDto requestDto = new HouseholdInviteRequestDto();
        requestDto.setEmail("invitee@example.com");

        HouseholdInviteResponseDto expectedResponse = new HouseholdInviteResponseDto(
            "invite-token",
            "invitee@example.com",
            1,
            "Test Household",
            "2024-12-31T23:59:59"
        );
        when(householdService.inviteToHousehold(anyString(), anyString())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = householdController.inviteToHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void joinHousehold_Success() {
        // Arrange
        HouseholdJoinRequestDto requestDto = new HouseholdJoinRequestDto();
        requestDto.setToken("valid-token");

        Household expectedHousehold = new Household("Test Household", "Test Address", 2);
        when(householdService.joinHousehold(anyString(), anyString())).thenReturn(expectedHousehold);

        // Act
        ResponseEntity<?> response = householdController.joinHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedHousehold, response.getBody());
    }

    @Test
    void getCurrentUserHousehold_Success() {
        // Arrange
        HouseholdDto expectedDto = new HouseholdDto(1, "Test Household", "Test Address", 2, null, null);
        when(householdService.getCurrentUserHousehold(anyString())).thenReturn(expectedDto);

        // Act
        ResponseEntity<?> response = householdController.getCurrentUserHousehold(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getHouseholdMembers_Success() {
        // Arrange
        List<HouseholdMemberDto> expectedMembers = Arrays.asList(
            new HouseholdMemberDto(1, "user1@example.com", "John", "Doe", true),
            new HouseholdMemberDto(2, "user2@example.com", "Jane", "Smith", false)
        );
        when(householdService.getHouseholdMembers(anyString())).thenReturn(expectedMembers);

        // Act
        ResponseEntity<?> response = householdController.getHouseholdMembers(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMembers, response.getBody());
    }

    @Test
    void updateHousehold_Success() {
        // Arrange
        HouseholdUpdateRequestDto requestDto = new HouseholdUpdateRequestDto();
        requestDto.setName("Updated Household");
        requestDto.setAddress("Updated Address");

        Household updatedHousehold = new Household("Updated Household", "Updated Address", 2);
        when(householdService.updateHousehold(anyString(), anyString(), anyString()))
            .thenReturn(updatedHousehold);

        // Act
        ResponseEntity<?> response = householdController.updateHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // Negative test cases

    @Test
    void createHousehold_UserAlreadyHasHousehold() {
        // Arrange
        HouseholdCreateRequestDto requestDto = new HouseholdCreateRequestDto();
        when(householdService.createHousehold(any()))
            .thenThrow(new IllegalStateException("User already has a household"));

        // Act
        ResponseEntity<?> response = householdController.createHousehold(requestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already has a household", response.getBody());
    }

    @Test
    void inviteToHousehold_UserNotFound() {
        // Arrange
        HouseholdInviteRequestDto requestDto = new HouseholdInviteRequestDto();
        requestDto.setEmail("nonexistent@example.com");

        // Mock the service to throw the exception
        when(householdService.inviteToHousehold(anyString(), anyString()))
            .thenThrow(new IllegalStateException("User not found"));

        // Act
        ResponseEntity<?> response = householdController.inviteToHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(householdService).inviteToHousehold(principal.getName(), requestDto.getEmail());
    }

    @Test
    void joinHousehold_InvalidToken() {
        // Arrange
        HouseholdJoinRequestDto requestDto = new HouseholdJoinRequestDto();
        requestDto.setToken("invalid-token");

        // Mock the service to throw the exception
        when(householdService.joinHousehold(anyString(), anyString()))
            .thenThrow(new IllegalStateException("Invalid or expired token"));

        // Act
        ResponseEntity<?> response = householdController.joinHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid or expired token", response.getBody());
        verify(householdService).joinHousehold(principal.getName(), requestDto.getToken());
    }

    @Test
    void getCurrentUserHousehold_NoHousehold() {
        // Arrange
        when(householdService.getCurrentUserHousehold(anyString()))
            .thenThrow(new IllegalStateException("User doesn't have a household"));

        // Act
        ResponseEntity<?> response = householdController.getCurrentUserHousehold(principal);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateHousehold_NotAdmin() {
        // Arrange
        HouseholdUpdateRequestDto requestDto = new HouseholdUpdateRequestDto();
        requestDto.setName("Updated Household");
        requestDto.setAddress("Updated Address");

        // Mock the service to throw the exception
        when(householdService.updateHousehold(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalStateException("User is not a household admin"));

        // Act
        ResponseEntity<?> response = householdController.updateHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not a household admin", response.getBody());
        verify(householdService).updateHousehold(principal.getName(), requestDto.getName(), requestDto.getAddress());
    }

    @Test
    void deleteHousehold_Success() {
        // Arrange
        doNothing().when(householdService).deleteCurrentHousehold(anyString());

        // Act
        ResponseEntity<?> response = householdController.deleteHousehold(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully deleted household", response.getBody());
    }

    @Test
    void deleteHousehold_NotAdmin() {
        // Arrange
        doThrow(new IllegalStateException("User is not a household admin"))
            .when(householdService).deleteCurrentHousehold(anyString());

        // Act
        ResponseEntity<?> response = householdController.deleteHousehold(principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not a household admin", response.getBody());
    }

    @Test
    void isCurrentUserHouseholdAdmin_Success() {
        // Arrange
        User mockUser = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(mockUser));
        when(householdService.isUserHouseholdAdmin(any())).thenReturn(true);

        // Act
        ResponseEntity<?> response = householdController.isCurrentUserHouseholdAdmin(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<String, Boolean>) response.getBody()).get("isAdmin"));
    }

    @Test
    void isCurrentUserHouseholdAdmin_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString()))
            .thenThrow(new IllegalStateException("User not found"));

        // Act
        ResponseEntity<?> response = householdController.isCurrentUserHouseholdAdmin(principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }
/**
    @Test
    void createHousehold_InvalidRequest() {
        // Arrange
        HouseholdCreateRequestDto requestDto = new HouseholdCreateRequestDto();
        requestDto.setName("");  // Empty name to trigger @NotBlank validation
        requestDto.setAddress("");  // Empty address to trigger @NotBlank validation
        requestDto.setPopulationCount(0);  // Invalid population count to trigger @Min validation

        // Act
        ResponseEntity<?> response = householdController.createHousehold(requestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void inviteToHousehold_InvalidEmail() {
        // Arrange
        HouseholdInviteRequestDto requestDto = new HouseholdInviteRequestDto();
        requestDto.setEmail("invalid-email");

        // Act
        ResponseEntity<?> response = householdController.inviteToHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void joinHousehold_EmptyToken() {
        // Arrange
        HouseholdJoinRequestDto requestDto = new HouseholdJoinRequestDto();
        requestDto.setToken("");

        // Act
        ResponseEntity<?> response = householdController.joinHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
*/

    @Test
    void updateHousehold_InvalidRequest() {
        // Arrange
        HouseholdUpdateRequestDto requestDto = new HouseholdUpdateRequestDto();
        requestDto.setName("");  // Empty name to trigger @NotBlank validation
        requestDto.setAddress("");  // Empty address to trigger @NotBlank validation

        // Act
        ResponseEntity<?> response = householdController.updateHousehold(requestDto, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getHouseholdMembers_NoHousehold() {
        // Arrange
        when(householdService.getHouseholdMembers(anyString()))
            .thenThrow(new IllegalStateException("User doesn't have a household"));

        // Act
        ResponseEntity<?> response = householdController.getHouseholdMembers(principal);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void leaveHousehold_Success() {
        // Arrange
        doNothing().when(householdService).leaveHousehold(anyString());

        // Act
        ResponseEntity<?> response = householdController.leaveHousehold(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully left the household", response.getBody());
        verify(householdService).leaveHousehold(principal.getName());
    }

    @Test
    void leaveHousehold_NotAllowed() {
        // Arrange
        doThrow(new IllegalStateException("The last household admin cannot leave the household"))
            .when(householdService).leaveHousehold(anyString());

        // Act
        ResponseEntity<?> response = householdController.leaveHousehold(principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The last household admin cannot leave the household", response.getBody());
    }

    @Test
    void promoteToAdmin_Success() {
        // Arrange
        doNothing().when(householdService).promoteToAdmin(anyString(), anyString());

        // Act
        ResponseEntity<?> response = householdController.promoteToAdmin("user@example.com", principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully promoted user to admin", response.getBody());
        verify(householdService).promoteToAdmin(principal.getName(), "user@example.com");
    }

    @Test
    void promoteToAdmin_NotAdmin() {
        // Arrange
        doThrow(new IllegalStateException("Only household admins can promote users"))
            .when(householdService).promoteToAdmin(anyString(), anyString());

        // Act
        ResponseEntity<?> response = householdController.promoteToAdmin("user@example.com", principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only household admins can promote users", response.getBody());
    }

    @Test
    void removeMemberFromHousehold_Success() {
        // Arrange
        doNothing().when(householdService).removeMemberFromHousehold(anyString(), anyInt());

        // Act
        ResponseEntity<?> response = householdController.removeMemberFromHousehold(1, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully removed member from household", response.getBody());
        verify(householdService).removeMemberFromHousehold(principal.getName(), 1);
    }

    @Test
    void removeMemberFromHousehold_NotAdmin() {
        // Arrange
        doThrow(new IllegalStateException("Only household admins can remove members"))
            .when(householdService).removeMemberFromHousehold(anyString(), anyInt());

        // Act
        ResponseEntity<?> response = householdController.removeMemberFromHousehold(1, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only household admins can remove members", response.getBody());
    }
}
