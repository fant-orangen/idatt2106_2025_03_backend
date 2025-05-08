package stud.ntnu.backend.service.user;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.repository.user.NotificationRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.util.LocationUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
/**
@SpringBootTest
class NotificationServiceIntegrationTest {

    @InjectMocks
    private NotificationService notificationService;

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

    @Test
    void shouldSendCrisisEventNotificationsSuccessfully() {
        // Arrange
        User user1 = new User();
        user1.setId(1);
        user1.setHomeLatitude(BigDecimal.valueOf(63.4306));
        user1.setHomeLongitude(BigDecimal.valueOf(10.3952));

        User user2 = new User();
        user2.setId(2);
        user2.setHomeLatitude(BigDecimal.valueOf(64.0000));
        user2.setHomeLongitude(BigDecimal.valueOf(11.0000));

        CrisisEvent crisisEvent = new CrisisEvent();
        crisisEvent.setId(123);
        crisisEvent.setEpicenterLatitude(BigDecimal.valueOf(63.4305));
        crisisEvent.setEpicenterLongitude(BigDecimal.valueOf(10.3951));
        crisisEvent.setRadius(BigDecimal.valueOf(1)); // 1 km radius

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        try (MockedStatic<LocationUtil> mockedLocationUtil = mockStatic(LocationUtil.class)) {
            mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(63.4306), eq(10.3952)))
                    .thenReturn(500.0); // Within radius
            mockedLocationUtil.when(() -> LocationUtil.calculateDistance(
                            eq(63.4305), eq(10.3951), eq(64.0000), eq(11.0000)))
                    .thenReturn(20000.0); // Outside radius

            // Act
            notificationService.sendCrisisEventNotifications(crisisEvent);

            // Assert
            verify(userService, times(1)).getAllUsers();
            mockedLocationUtil.verify(() -> LocationUtil.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble()), atLeastOnce());
            verify(notificationService, times(1)).createNotification(
                    eq(user1), eq(Notification.PreferenceType.crisis_alert), eq(Notification.TargetType.event),
                    eq(crisisEvent.getId()), anyString()
            );
            verify(notificationService, never()).createNotification(
                    eq(user2), any(), any(), any(), any()
            );
        }
    }
}
 **/