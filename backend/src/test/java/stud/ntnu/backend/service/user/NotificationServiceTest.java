package stud.ntnu.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.mockito.MockedStatic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.test.annotation.DirtiesContext;
import stud.ntnu.backend.dto.user.NotificationDto;
import stud.ntnu.backend.model.household.Household;
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

@DirtiesContext(classMode = AFTER_CLASS)
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

    @Spy
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
    class MarkAllAsReadTests {
        @Nested
        class Positive {
            @Test
            void shouldMarkAllNotificationsAsReadSuccessfully() {
                // Arrange
                String email = "test@example.com";
                User user = new User();
                user.setId(1);
                user.setEmail(email);

                Notification notification1 = new Notification(
                    user, PreferenceType.system, TargetType.event, 123, "Test notification 1", LocalDateTime.now()
                );
                notification1.setId(1);

                Notification notification2 = new Notification(
                    user, PreferenceType.crisis_alert, TargetType.event, 456, "Test notification 2", LocalDateTime.now()
                );
                notification2.setId(2);

                List<Notification> unreadNotifications = Arrays.asList(notification1, notification2);

                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(notificationRepository.findByUserIdAndReadAtIsNull(user.getId()))
                    .thenReturn(unreadNotifications);
                when(notificationRepository.saveAll(unreadNotifications))
                    .thenReturn(unreadNotifications);

                // Act
                int result = notificationService.markAllNotificationsAsRead(email);

                // Assert
                assertEquals(2, result);
                assertNotNull(notification1.getReadAt());
                assertNotNull(notification2.getReadAt());
                verify(userRepository).findByEmail(email);
                verify(notificationRepository).findByUserIdAndReadAtIsNull(user.getId());
                verify(notificationRepository).saveAll(unreadNotifications);
            }

            @Test
            void shouldReturnZeroWhenNoUnreadNotifications() {
                // Arrange
                String email = "test@example.com";
                User user = new User();
                user.setId(1);
                user.setEmail(email);

                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
                when(notificationRepository.findByUserIdAndReadAtIsNull(user.getId()))
                    .thenReturn(Collections.emptyList());

                // Act
                int result = notificationService.markAllNotificationsAsRead(email);

                // Assert
                assertEquals(0, result);
                verify(userRepository).findByEmail(email);
                verify(notificationRepository).findByUserIdAndReadAtIsNull(user.getId());
                verify(notificationRepository, never()).saveAll(anyList());
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenUserNotFound() {
                // Arrange
                String email = "nonexistent@example.com";

                when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> notificationService.markAllNotificationsAsRead(email)
                );

                assertEquals("User not found with email: " + email, exception.getMessage());
                verify(userRepository).findByEmail(email);
                verify(notificationRepository, never()).findByUserIdAndReadAtIsNull(anyInt());
                verify(notificationRepository, never()).saveAll(anyList());
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

    @Nested
    class SendNotificationsToAllUsersTests {
        @Nested
        class Positive {
            @Test
            void shouldSendNotificationsToAllUsersSuccessfully() {
                // Arrange
                User user1 = new User();
                user1.setId(1);
                user1.setEmail("user1@example.com");

                User user2 = new User();
                user2.setId(2);
                user2.setEmail("user2@example.com");

                Notification notification1 = new Notification(
                    user1, PreferenceType.system, TargetType.event, 123, "Test notification 1", LocalDateTime.now()
                );
                notification1.setId(1);

                Notification notification2 = new Notification(
                    user2, PreferenceType.system, TargetType.event, 123, "Test notification 2", LocalDateTime.now()
                );
                notification2.setId(2);

                List<Notification> notifications = Arrays.asList(notification1, notification2);

                // Act
                notificationService.sendNotificationsToAllUsers(notifications);

                // Assert
                assertNotNull(notification1.getSentAt());
                assertNotNull(notification2.getSentAt());
                verify(notificationRepository, times(2)).save(any(Notification.class));
                verify(messagingTemplate).convertAndSend(
                    eq("/topic/notifications/1"),
                    any(NotificationDto.class)
                );
                verify(messagingTemplate).convertAndSend(
                    eq("/topic/notifications/2"),
                    any(NotificationDto.class)
                );
            }
        }
    }

    @Nested
    class SendCrisisEventNotificationsInternalTests {
        @Nested
        class Positive {
            @Test
            void shouldSendNotificationsToUsersWithinRadius() {
                // Arrange
                User user1 = new User();
                user1.setId(1);
                user1.setHomeLatitude(new BigDecimal("63.4306"));
                user1.setHomeLongitude(new BigDecimal("10.3952"));

                User user2 = new User();
                user2.setId(2);
                user2.setHomeLatitude(new BigDecimal("64.0000"));
                user2.setHomeLongitude(new BigDecimal("11.0000"));

                CrisisEvent crisisEvent = new CrisisEvent();
                crisisEvent.setId(123);
                crisisEvent.setName("Test Crisis");
                crisisEvent.setEpicenterLatitude(new BigDecimal("63.4305"));
                crisisEvent.setEpicenterLongitude(new BigDecimal("10.3951"));
                crisisEvent.setRadius(new BigDecimal("1")); // 1 km radius
                crisisEvent.setSeverity(CrisisEvent.Severity.red);
                crisisEvent.setStartTime(LocalDateTime.now());

                String messageTemplate = "🚨 Kriselarsel: 'Test Crisis' (høy alvorlighetsgrad). Du varsles fordi {reason} er innenfor faresonen. Startet %s.";

                when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

                // Mock createNotification to return a valid notification
                Notification mockNotification = new Notification(
                    user1, Notification.PreferenceType.crisis_alert, Notification.TargetType.event, 
                    crisisEvent.getId(), "Test notification", LocalDateTime.now()
                );
                mockNotification.setId(1);
                doReturn(mockNotification).when(notificationService).createNotification(
                    eq(user1),
                    eq(Notification.PreferenceType.crisis_alert),
                    eq(Notification.TargetType.event),
                    eq(crisisEvent.getId()),
                    anyString()
                );

                try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                    // User 1 is within radius
                    mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(63.4306), eq(10.3952)))
                            .thenReturn(500.0); // Within radius
                    // User 2 is outside radius
                    mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(64.0000), eq(11.0000)))
                            .thenReturn(20000.0); // Outside radius

                    // Act
                    notificationService.sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true);

                    // Assert
                    verify(userRepository).findAll();
                    verify(notificationService).createNotification(
                            eq(user1),
                            eq(Notification.PreferenceType.crisis_alert),
                            eq(Notification.TargetType.event),
                            eq(crisisEvent.getId()),
                            anyString()
                    );
                    verify(notificationService).sendNotification(any(Notification.class));
                    verify(notificationService, never()).createNotification(
                            eq(user2),
                            any(),
                            any(),
                            any(),
                            any()
                    );
                }
            }

            @Test
            void shouldSendNotificationsToUsersWithHouseholdWithinRadius() {
                // Arrange
                User user = new User();
                user.setId(1);
                user.setHomeLatitude(new BigDecimal("64.0000"));
                user.setHomeLongitude(new BigDecimal("11.0000"));

                Household household = new Household();
                household.setId(1);
                household.setLatitude(new BigDecimal("63.4306"));
                household.setLongitude(new BigDecimal("10.3952"));
                user.setHousehold(household);

                CrisisEvent crisisEvent = new CrisisEvent();
                crisisEvent.setId(123);
                crisisEvent.setName("Test Crisis");
                crisisEvent.setEpicenterLatitude(new BigDecimal("63.4305"));
                crisisEvent.setEpicenterLongitude(new BigDecimal("10.3951"));
                crisisEvent.setRadius(new BigDecimal("1")); // 1 km radius
                crisisEvent.setSeverity(CrisisEvent.Severity.red);
                crisisEvent.setStartTime(LocalDateTime.now());

                String messageTemplate = "🚨 Kriselarsel: 'Test Crisis' (høy alvorlighetsgrad). Du varsles fordi {reason} er innenfor faresonen. Startet %s.";

                when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

                // Mock createNotification to return a valid notification
                Notification mockNotification = new Notification(
                    user, Notification.PreferenceType.crisis_alert, Notification.TargetType.event, 
                    crisisEvent.getId(), "Test notification", LocalDateTime.now()
                );
                mockNotification.setId(1);
                doReturn(mockNotification).when(notificationService).createNotification(
                    eq(user),
                    eq(Notification.PreferenceType.crisis_alert),
                    eq(Notification.TargetType.event),
                    eq(crisisEvent.getId()),
                    anyString()
                );

                try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                    // User's home is outside radius
                    mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(64.0000), eq(11.0000)))
                            .thenReturn(20000.0); // Outside radius
                    // User's household is within radius
                    mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(63.4306), eq(10.3952)))
                            .thenReturn(500.0); // Within radius

                    // Act
                    notificationService.sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true);

                    // Assert
                    verify(userRepository).findAll();
                    verify(notificationService).createNotification(
                            eq(user),
                            eq(Notification.PreferenceType.crisis_alert),
                            eq(Notification.TargetType.event),
                            eq(crisisEvent.getId()),
                            anyString()
                    );
                    verify(notificationService).sendNotification(any(Notification.class));
                }
            }

            @Test
            void shouldSendNotificationsToUsersWithBothHomeAndHouseholdWithinRadius() {
                // Arrange
                User user = new User();
                user.setId(1);
                user.setHomeLatitude(new BigDecimal("63.4306"));
                user.setHomeLongitude(new BigDecimal("10.3952"));

                Household household = new Household();
                household.setId(1);
                household.setLatitude(new BigDecimal("63.4306"));
                household.setLongitude(new BigDecimal("10.3952"));
                user.setHousehold(household);

                CrisisEvent crisisEvent = new CrisisEvent();
                crisisEvent.setId(123);
                crisisEvent.setName("Test Crisis");
                crisisEvent.setEpicenterLatitude(new BigDecimal("63.4305"));
                crisisEvent.setEpicenterLongitude(new BigDecimal("10.3951"));
                crisisEvent.setRadius(new BigDecimal("1")); // 1 km radius
                crisisEvent.setSeverity(CrisisEvent.Severity.red);
                crisisEvent.setStartTime(LocalDateTime.now());

                String messageTemplate = "🚨 Kriselarsel: 'Test Crisis' (høy alvorlighetsgrad). Du varsles fordi {reason} er innenfor faresonen. Startet %s.";

                when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

                // Mock createNotification to return a valid notification
                Notification mockNotification = new Notification(
                    user, Notification.PreferenceType.crisis_alert, Notification.TargetType.event, 
                    crisisEvent.getId(), "Test notification", LocalDateTime.now()
                );
                mockNotification.setId(1);
                doReturn(mockNotification).when(notificationService).createNotification(
                    eq(user),
                    eq(Notification.PreferenceType.crisis_alert),
                    eq(Notification.TargetType.event),
                    eq(crisisEvent.getId()),
                    anyString()
                );

                try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
                    // Both user's home and household are within radius
                    mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(63.4306), eq(10.3952)))
                            .thenReturn(500.0); // Within radius

                    // Act
                    notificationService.sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true);

                    // Assert
                    verify(userRepository).findAll();
                    verify(notificationService).createNotification(
                            eq(user),
                            eq(Notification.PreferenceType.crisis_alert),
                            eq(Notification.TargetType.event),
                            eq(crisisEvent.getId()),
                            anyString()
                    );
                    verify(notificationService).sendNotification(any(Notification.class));
                }
            }
        }

        @Nested
        class Negative {
            @Test
            void shouldNotSendNotificationsWhenCrisisEventDataIsIncomplete() {
                // Arrange
                CrisisEvent crisisEvent = new CrisisEvent();
                crisisEvent.setId(123);
                // Missing required fields: epicenterLatitude, epicenterLongitude, radius

                String messageTemplate = "Test message template";

                // Act
                notificationService.sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true);

                // Assert
                verify(userRepository, never()).findAll();
                verify(notificationService, never()).createNotification(
                        any(User.class),
                        any(),
                        any(),
                        any(),
                        any()
                );
            }

            @Test
            void shouldNotSendNotificationsWhenNoUsersExist() {
                // Arrange
                CrisisEvent crisisEvent = new CrisisEvent();
                crisisEvent.setId(123);
                crisisEvent.setEpicenterLatitude(new BigDecimal("63.4305"));
                crisisEvent.setEpicenterLongitude(new BigDecimal("10.3951"));
                crisisEvent.setRadius(new BigDecimal("1"));

                String messageTemplate = "Test message template";

                when(userRepository.findAll()).thenReturn(Collections.emptyList());

                // Act
                notificationService.sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true);

                // Assert
                verify(userRepository).findAll();
                verify(notificationService, never()).createNotification(
                        any(User.class),
                        any(),
                        any(),
                        any(),
                        any()
                );
            }

            @Test
            void shouldNotSendNotificationsWhenUserHasNoLocationData() {
                // Arrange
                User user = new User();
                user.setId(1);
                // No home or household location set

                CrisisEvent crisisEvent = new CrisisEvent();
                crisisEvent.setId(123);
                crisisEvent.setEpicenterLatitude(new BigDecimal("63.4305"));
                crisisEvent.setEpicenterLongitude(new BigDecimal("10.3951"));
                crisisEvent.setRadius(new BigDecimal("1"));

                String messageTemplate = "Test message template";

                when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

                // Act
                notificationService.sendCrisisEventNotificationsInternal(crisisEvent, messageTemplate, true);

                // Assert
                verify(userRepository).findAll();
                verify(notificationService, never()).createNotification(
                        any(User.class),
                        any(),
                        any(),
                        any(),
                        any()
                );
            }
        }
    }
}
