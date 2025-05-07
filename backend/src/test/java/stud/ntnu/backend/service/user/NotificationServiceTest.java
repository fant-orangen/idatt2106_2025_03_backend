package stud.ntnu.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.Notification.PreferenceType;
import stud.ntnu.backend.model.user.Notification.TargetType;
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.repository.user.NotificationRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.LocationUtil;

public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateNotificationTests {
        @Nested
        class Positive {
            @Test
            void shouldCreateNotificationSuccessfully() {
                // Arrange
                User user = new User();
                user.setId(1);
                user.setEmail("test@example.com");

                PreferenceType preferenceType = PreferenceType.system;
                TargetType targetType = TargetType.event;
                Integer targetId = 123;
                String description = "Test notification";
                LocalDateTime now = LocalDateTime.now();

                Notification expectedNotification = new Notification(
                    user, preferenceType, targetType, targetId, description, now
                );

                when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(invocation -> {
                        Notification savedNotification = invocation.getArgument(0);
                        savedNotification.setId(1);
                        return savedNotification;
                    });

                // Act
                Notification result = notificationService.createNotification(
                    user, preferenceType, targetType, targetId, description
                );

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getId());
                assertEquals(user, result.getUser());
                assertEquals(preferenceType, result.getPreferenceType());
                assertEquals(targetType, result.getTargetType());
                assertEquals(targetId, result.getTargetId());
                assertEquals(description, result.getDescription());
                assertNotNull(result.getNotifyAt());
                verify(notificationRepository).save(any(Notification.class));
            }
        }
    }

    @Nested
    class SendNotificationTests {
        @Nested
        class Positive {
            @Test
            void shouldSendNotificationSuccessfully() {
                // Arrange
                User user = new User();
                user.setId(1);
                user.setEmail("test@example.com");

                Notification notification = new Notification(
                    user, PreferenceType.system, TargetType.event, 123, "Test notification", LocalDateTime.now()
                );
                notification.setId(1);

                // Act
                notificationService.sendNotification(notification);

                // Assert
                assertNotNull(notification.getSentAt());
                verify(notificationRepository).save(notification);
                verify(messagingTemplate).convertAndSend(
                    eq("/topic/notifications/1"),
                    any(NotificationDto.class)
                );
            }
        }
    }

    @Nested
    class GetNotificationsForUserTests {
        @Nested
        class Positive {
            @Test
            void shouldGetNotificationsForUserSuccessfully() {
                // Arrange
                Integer userId = 1;
                User user = new User();
                user.setId(userId);

                Notification notification1 = new Notification(
                    user, PreferenceType.system, TargetType.event, 123, "Test notification 1", LocalDateTime.now()
                );
                notification1.setId(1);

                Notification notification2 = new Notification(
                    user, PreferenceType.crisis_alert, TargetType.event, 456, "Test notification 2", LocalDateTime.now()
                );
                notification2.setId(2);

                List<Notification> expectedNotifications = Arrays.asList(notification1, notification2);

                when(notificationRepository.findByUserIdOrderByNotifyAtDesc(userId))
                    .thenReturn(expectedNotifications);

                // Act
                List<Notification> result = notificationService.getNotificationsForUser(userId);

                // Assert
                assertEquals(2, result.size());
                assertEquals(expectedNotifications, result);
                verify(notificationRepository).findByUserIdOrderByNotifyAtDesc(userId);
            }

            @Test
            void shouldGetPaginatedNotificationsForUserSuccessfully() {
                // Arrange
                Integer userId = 1;
                User user = new User();
                user.setId(userId);

                Notification notification1 = new Notification(
                    user, PreferenceType.system, TargetType.event, 123, "Test notification 1", LocalDateTime.now()
                );
                notification1.setId(1);

                Notification notification2 = new Notification(
                    user, PreferenceType.crisis_alert, TargetType.event, 456, "Test notification 2", LocalDateTime.now()
                );
                notification2.setId(2);

                List<Notification> notificationList = Arrays.asList(notification1, notification2);
                Page<Notification> expectedPage = new PageImpl<>(notificationList);
                Pageable pageable = PageRequest.of(0, 10);

                when(notificationRepository.findByUserId(userId, pageable))
                    .thenReturn(expectedPage);

                // Act
                Page<Notification> result = notificationService.getNotificationsForUser(userId, pageable);

                // Assert
                assertEquals(2, result.getContent().size());
                assertEquals(expectedPage, result);
                verify(notificationRepository).findByUserId(userId, pageable);
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldReturnEmptyListWhenNoNotificationsExist() {
                // Arrange
                Integer userId = 1;

                when(notificationRepository.findByUserIdOrderByNotifyAtDesc(userId))
                    .thenReturn(Collections.emptyList());

                // Act
                List<Notification> result = notificationService.getNotificationsForUser(userId);

                // Assert
                assertTrue(result.isEmpty());
                verify(notificationRepository).findByUserIdOrderByNotifyAtDesc(userId);
            }
        }
    }

    @Nested
    class MarkAsReadTests {
        @Nested
        class Positive {
            @Test
            void shouldMarkNotificationAsReadSuccessfully() {
                // Arrange
                Integer notificationId = 1;
                User user = new User();
                user.setId(1);

                Notification notification = new Notification(
                    user, PreferenceType.system, TargetType.event, 123, "Test notification", LocalDateTime.now()
                );
                notification.setId(notificationId);

                when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.of(notification));
                when(notificationRepository.save(any(Notification.class)))
                    .thenReturn(notification);

                // Act
                Notification result = notificationService.markAsRead(notificationId);

                // Assert
                assertNotNull(result);
                assertNotNull(result.getReadAt());
                verify(notificationRepository).findById(notificationId);
                verify(notificationRepository).save(notification);
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenNotificationNotFound() {
                // Arrange
                Integer notificationId = 999;

                when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> notificationService.markAsRead(notificationId)
                );

                assertEquals("Notification not found with ID: " + notificationId, exception.getMessage());
                verify(notificationRepository).findById(notificationId);
                verify(notificationRepository, never()).save(any(Notification.class));
            }
        }
    }

    @Nested
    class CreateSystemNotificationForAllUsersTests {
        @Nested
        class Positive {
            @Test
            void shouldCreateSystemNotificationForAllUsersSuccessfully() {
                // Arrange
                String description = "System-wide notification";

                User adminUser = new User();
                adminUser.setId(1);
                adminUser.setEmail("admin@example.com");

                User user1 = new User();
                user1.setId(2);
                user1.setEmail("user1@example.com");

                User user2 = new User();
                user2.setId(3);
                user2.setEmail("user2@example.com");

                List<User> allUsers = Arrays.asList(adminUser, user1, user2);

                when(userRepository.findAll()).thenReturn(allUsers);
                when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(invocation -> {
                        Notification savedNotification = invocation.getArgument(0);
                        savedNotification.setId(savedNotification.getUser().getId()); // Just for testing
                        return savedNotification;
                    });

                // Act
                List<Notification> result = notificationService.createSystemNotificationForAllUsers(description, adminUser);

                // Assert
                assertEquals(3, result.size());
                for (int i = 0; i < result.size(); i++) {
                    Notification notification = result.get(i);
                    User user = allUsers.get(i);

                    assertEquals(user.getId(), notification.getId());
                    assertEquals(user, notification.getUser());
                    assertEquals(PreferenceType.system, notification.getPreferenceType());
                    assertNull(notification.getTargetType());
                    assertNull(notification.getTargetId());
                    assertEquals(description, notification.getDescription());
                    assertNotNull(notification.getNotifyAt());
                }

                verify(userRepository).findAll();
                verify(notificationRepository, times(3)).save(any(Notification.class));
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldReturnEmptyListWhenNoUsersExist() {
                // Arrange
                String description = "System-wide notification";
                User adminUser = new User();
                adminUser.setId(1);

                when(userRepository.findAll()).thenReturn(Collections.emptyList());

                // Act
                List<Notification> result = notificationService.createSystemNotificationForAllUsers(description, adminUser);

                // Assert
                assertTrue(result.isEmpty());
                verify(userRepository).findAll();
                verify(notificationRepository, never()).save(any(Notification.class));
            }
        }
    }

    @Nested
    class ChangeNotificationPreferenceTests {
        @Nested
        class Positive {
            @Test
            void shouldUpdateExistingPreferenceSuccessfully() {
                // Arrange
                User user = new User();
                user.setId(1);
                user.setEmail("test@example.com");

                String preferenceType = "crisis_alert";
                boolean enable = false;

                NotificationPreference existingPreference = new NotificationPreference();
                existingPreference.setId(1);
                existingPreference.setUser(user);
                existingPreference.setPreferenceType(PreferenceType.crisis_alert);
                existingPreference.setEnabled(true);
                existingPreference.setCreatedAt(LocalDateTime.now().minusDays(1));
                existingPreference.setUpdatedAt(LocalDateTime.now().minusDays(1));

                when(notificationPreferenceRepository.findByUserAndPreferenceType(
                    user, PreferenceType.crisis_alert))
                    .thenReturn(Optional.of(existingPreference));

                when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                notificationService.changeNotificationPreference(user, preferenceType, enable);

                // Assert
                assertFalse(existingPreference.isEnabled());
                assertNotEquals(existingPreference.getCreatedAt(), existingPreference.getUpdatedAt());
                verify(notificationPreferenceRepository).findByUserAndPreferenceType(user, PreferenceType.crisis_alert);
                verify(notificationPreferenceRepository).save(existingPreference);
            }

            @Test
            void shouldCreateNewPreferenceWhenNotExists() {
                // Arrange
                User user = new User();
                user.setId(1);
                user.setEmail("test@example.com");

                String preferenceType = "crisis_alert";
                boolean enable = true;

                when(notificationPreferenceRepository.findByUserAndPreferenceType(
                    user, PreferenceType.crisis_alert))
                    .thenReturn(Optional.empty());

                when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                    .thenAnswer(invocation -> {
                        NotificationPreference savedPreference = invocation.getArgument(0);
                        savedPreference.setId(1);
                        return savedPreference;
                    });

                // Act
                notificationService.changeNotificationPreference(user, preferenceType, enable);

                // Assert
                verify(notificationPreferenceRepository).findByUserAndPreferenceType(user, PreferenceType.crisis_alert);
                verify(notificationPreferenceRepository).save(any(NotificationPreference.class));
            }
        }
    }
}
