package stud.ntnu.backend.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.dto.user.SystemNotificationCreateDto;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.user.NotificationService;
import stud.ntnu.backend.service.user.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private Principal principal;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;
    private User adminUser;
    private Notification testNotification;
    private List<Notification> notificationList;
    private Page<Notification> notificationPage;
    private List<NotificationPreference> preferenceList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        // Create regular user
        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName("USER");

        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("user@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(userRole);

        // Create admin user
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ADMIN");

        adminUser = new User();
        adminUser.setId(2);
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(adminRole);

        // Create test notification
        testNotification = new Notification();
        testNotification.setId(1);
        testNotification.setUser(testUser);
        testNotification.setPreferenceType(Notification.PreferenceType.crisis_alert);
        testNotification.setTargetType(Notification.TargetType.event);
        testNotification.setTargetId(1);
        testNotification.setDescription("Test notification");
        testNotification.setCreatedAt(LocalDateTime.now());
        testNotification.setNotifyAt(LocalDateTime.now());

        // Create notification list and page
        notificationList = new ArrayList<>();
        notificationList.add(testNotification);
        notificationPage = new PageImpl<>(notificationList, PageRequest.of(0, 10), 1);

        // Create notification preferences
        NotificationPreference preference1 = new NotificationPreference();
        preference1.setId(1);
        preference1.setUser(testUser);
        preference1.setPreferenceType(Notification.PreferenceType.crisis_alert);
        preference1.setEnabled(true);

        NotificationPreference preference2 = new NotificationPreference();
        preference2.setId(2);
        preference2.setUser(testUser);
        preference2.setPreferenceType(Notification.PreferenceType.system);
        preference2.setEnabled(true);

        preferenceList = Arrays.asList(preference1, preference2);

        // Mock principal
        when(principal.getName()).thenReturn("user@example.com");
    }

    @Test
    @DisplayName("Should return paginated notifications for user")
    void shouldReturnPaginatedNotificationsForUser() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(notificationService.getNotificationsForUser(testUser.getId(), pageable)).thenReturn(notificationPage);

        // Act
        ResponseEntity<?> response = notificationController.getNotifications(principal, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Page);
        @SuppressWarnings("unchecked")
        Page<NotificationDto> resultPage = (Page<NotificationDto>) response.getBody();
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(testNotification.getId(), resultPage.getContent().get(0).getId());

        verify(userService).getUserByEmail("user@example.com");
        verify(notificationService).getNotificationsForUser(testUser.getId(), pageable);
    }

    @Test
    @DisplayName("Should return bad request when user not found")
    void shouldReturnBadRequestWhenUserNotFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = notificationController.getNotifications(principal, pageable);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());

        verify(userService).getUserByEmail("user@example.com");
        verify(notificationService, never()).getNotificationsForUser(anyInt(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should mark notification as read")
    void shouldMarkNotificationAsRead() {
        // Arrange
        Integer notificationId = 1;
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(notificationService.markAsRead(notificationId)).thenReturn(testNotification);

        // Act
        ResponseEntity<?> response = notificationController.markAsRead(notificationId, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof NotificationDto);
        NotificationDto resultDto = (NotificationDto) response.getBody();
        assertEquals(testNotification.getId(), resultDto.getId());

        verify(userService).getUserByEmail("user@example.com");
        verify(notificationService).markAsRead(notificationId);
    }

    @Test
    @DisplayName("Should return forbidden when notification belongs to different user")
    void shouldReturnForbiddenWhenNotificationBelongsToDifferentUser() {
        // Arrange
        Integer notificationId = 1;
        User otherUser = new User();
        otherUser.setId(2);

        Notification otherUserNotification = new Notification();
        otherUserNotification.setId(1);
        otherUserNotification.setUser(otherUser);

        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(notificationService.markAsRead(notificationId)).thenReturn(otherUserNotification);

        // Act
        ResponseEntity<?> response = notificationController.markAsRead(notificationId, principal);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You don't have permission to mark this notification as read", response.getBody());

        verify(userService).getUserByEmail("user@example.com");
        verify(notificationService).markAsRead(notificationId);
    }

    @Test
    @DisplayName("Should return true when user has unread notifications")
    void shouldReturnTrueWhenUserHasUnreadNotifications() {
        // Arrange
        when(notificationService.hasUnreadNotifications("user@example.com")).thenReturn(true);

        // Act
        ResponseEntity<?> response = notificationController.anyUnread(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());

        verify(notificationService).hasUnreadNotifications("user@example.com");
    }

    @Test
    @DisplayName("Should return false when user has no unread notifications")
    void shouldReturnFalseWhenUserHasNoUnreadNotifications() {
        // Arrange
        when(notificationService.hasUnreadNotifications("user@example.com")).thenReturn(false);

        // Act
        ResponseEntity<?> response = notificationController.anyUnread(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());

        verify(notificationService).hasUnreadNotifications("user@example.com");
    }

    @Test
    @DisplayName("Should return user notification preferences")
    void shouldReturnUserNotificationPreferences() {
        // Arrange
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(notificationService.getUserNotificationPreferences(testUser)).thenReturn(preferenceList);

        // Act
        ResponseEntity<?> response = notificationController.getNotificationPreferences(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<NotificationPreference> resultList = (List<NotificationPreference>) response.getBody();
        assertEquals(2, resultList.size());
        assertEquals(Notification.PreferenceType.crisis_alert, resultList.get(0).getPreferenceType());
        assertEquals(Notification.PreferenceType.system, resultList.get(1).getPreferenceType());

        verify(userService).getUserByEmail("user@example.com");
        verify(notificationService).getUserNotificationPreferences(testUser);
    }

    @Test
    @DisplayName("Should change notification preference")
    void shouldChangeNotificationPreference() {
        // Arrange
        String preferenceType = "crisis_alert";
        boolean enable = true;
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(notificationService).changeNotificationPreference(testUser, preferenceType, enable);

        // Act
        ResponseEntity<?> response = notificationController.changeNotificationPreference(principal, preferenceType, enable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).getUserByEmail("user@example.com");
        verify(notificationService).changeNotificationPreference(testUser, preferenceType, enable);
    }

    @Test
    @DisplayName("Should handle invalid preference type")
    void shouldHandleInvalidPreferenceType() {
        // Arrange
        String preferenceType = "invalid_type";
        boolean enable = true;
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        doThrow(new IllegalArgumentException("Invalid preference type")).when(notificationService)
            .changeNotificationPreference(testUser, preferenceType, enable);

        // Act
        ResponseEntity<?> response = notificationController.changeNotificationPreference(principal, preferenceType, enable);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid preference type", response.getBody());
    }

    @Test
    @DisplayName("Admin should be able to create system notification")
    void adminShouldBeAbleToCreateSystemNotification() {
        // Arrange
        SystemNotificationCreateDto createDto = new SystemNotificationCreateDto("System-wide test notification");
        when(principal.getName()).thenReturn("admin@example.com");
        when(userService.getUserByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Mock AdminChecker
        try (var adminCheckerMock = mockStatic(AdminChecker.class)) {
            adminCheckerMock.when(() -> AdminChecker.isCurrentUserAdmin(principal, userService)).thenReturn(true);

            List<Notification> createdNotifications = Arrays.asList(testNotification);
            when(notificationService.createSystemNotificationForAllUsers(createDto.getDescription(), adminUser))
                .thenReturn(createdNotifications);

            // Act
            ResponseEntity<?> response = notificationController.createSystemNotification(createDto, principal);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(notificationService).createSystemNotificationForAllUsers(createDto.getDescription(), adminUser);
            verify(notificationService).sendNotificationsToAllUsers(createdNotifications);
        }
    }
}
