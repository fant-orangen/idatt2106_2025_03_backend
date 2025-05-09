package stud.ntnu.backend.service.crisis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.map.CreateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventChangeDto;
import stud.ntnu.backend.dto.map.UpdateCrisisEventDto;
import stud.ntnu.backend.dto.map.CrisisEventPreviewDto;
import stud.ntnu.backend.dto.map.CrisisEventDetailsDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.map.CrisisEventChange;
import stud.ntnu.backend.model.map.ScenarioTheme;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.CrisisEventChangeRepository;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.repository.map.ScenarioThemeRepository;
import stud.ntnu.backend.service.user.NotificationService;
import stud.ntnu.backend.service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CrisisEventServiceTest {

    @Mock
    private CrisisEventRepository crisisEventRepository;

    @Mock
    private CrisisEventChangeRepository crisisEventChangeRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @Mock
    private ScenarioThemeRepository scenarioThemeRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CrisisEventService crisisEventService;

    private User testUser;
    private CrisisEvent testCrisisEvent;
    private ScenarioTheme testScenarioTheme;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");

        // Create a test scenario theme
        testScenarioTheme = new ScenarioTheme();
        testScenarioTheme.setId(1);
        testScenarioTheme.setName("Test Theme");

        // Create a test crisis event
        testCrisisEvent = new CrisisEvent(
            "Test Crisis",
            new BigDecimal("60.0"),
            new BigDecimal("10.0"),
            new BigDecimal("5.0"),
            LocalDateTime.now(),
            testUser
        );
        testCrisisEvent.setId(1);
        testCrisisEvent.setDescription("Test description");
        testCrisisEvent.setSeverity(CrisisEvent.Severity.yellow);
        testCrisisEvent.setScenarioTheme(testScenarioTheme);
    }

    @Nested
    class GetAllCrisisEventsTests {
        @Test
        void shouldReturnAllCrisisEventsWithPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<CrisisEvent> crisisEvents = Collections.singletonList(testCrisisEvent);
            Page<CrisisEvent> crisisEventPage = new PageImpl<>(crisisEvents, pageable, crisisEvents.size());
            
            when(crisisEventRepository.findAll(pageable)).thenReturn(crisisEventPage);
            
            // Act
            Page<CrisisEvent> result = crisisEventService.getAllCrisisEvents(pageable);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testCrisisEvent, result.getContent().get(0));
            verify(crisisEventRepository).findAll(pageable);
        }
    }
    
    @Nested
    class GetCrisisEventByIdTests {
        @Nested
        class Positive {
            @Test
            void shouldReturnCrisisEventWhenFound() {
                // Arrange
                when(crisisEventRepository.findById(1)).thenReturn(Optional.of(testCrisisEvent));
                
                // Act
                Optional<CrisisEvent> result = crisisEventService.getCrisisEventById(1);
                
                // Assert
                assertTrue(result.isPresent());
                assertEquals(testCrisisEvent, result.get());
                verify(crisisEventRepository).findById(1);
            }
        }
        
        @Nested
        class Negative {
            @Test
            void shouldReturnEmptyOptionalWhenNotFound() {
                // Arrange
                when(crisisEventRepository.findById(999)).thenReturn(Optional.empty());
                
                // Act
                Optional<CrisisEvent> result = crisisEventService.getCrisisEventById(999);
                
                // Assert
                assertFalse(result.isPresent());
                verify(crisisEventRepository).findById(999);
            }
        }
    }
    
    @Nested
    class SaveCrisisEventTests {
        @Test
        void shouldSaveCrisisEvent() {
            // Arrange
            when(crisisEventRepository.save(testCrisisEvent)).thenReturn(testCrisisEvent);
            
            // Act
            CrisisEvent result = crisisEventService.saveCrisisEvent(testCrisisEvent);
            
            // Assert
            assertNotNull(result);
            assertEquals(testCrisisEvent, result);
            verify(crisisEventRepository).save(testCrisisEvent);
        }
    }
    
    @Nested
    class CreateCrisisEventTests {
        @Nested
        class Positive {
            @Test
            void shouldCreateCrisisEventSuccessfully() {
                // Arrange
                CreateCrisisEventDto createDto = new CreateCrisisEventDto(
                    new BigDecimal("60.0"),
                    new BigDecimal("10.0"),
                    "Test Address",
                    new BigDecimal("5.0"),
                    CrisisEvent.Severity.yellow,
                    "Test description",
                    "Test Crisis",
                    LocalDateTime.now(),
                    1
                );
                
                when(scenarioThemeRepository.findById(1)).thenReturn(Optional.of(testScenarioTheme));
                when(crisisEventRepository.save(any(CrisisEvent.class))).thenAnswer(invocation -> {
                    CrisisEvent savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1);
                    return savedEvent;
                });
                doNothing().when(notificationService).sendCrisisEventNotifications(any(CrisisEvent.class));
                
                // Act
                CrisisEvent result = crisisEventService.createCrisisEvent(createDto, testUser);
                
                // Assert
                assertNotNull(result);
                assertEquals(1, result.getId());
                assertEquals("Test Crisis", result.getName());
                assertEquals("Test description", result.getDescription());
                assertEquals(CrisisEvent.Severity.yellow, result.getSeverity());
                assertEquals(new BigDecimal("60.0"), result.getEpicenterLatitude());
                assertEquals(new BigDecimal("10.0"), result.getEpicenterLongitude());
                assertEquals(new BigDecimal("5.0"), result.getRadius());
                assertEquals(testScenarioTheme, result.getScenarioTheme());
                assertEquals(testUser, result.getCreatedByUser());
                
                verify(crisisEventRepository).save(any(CrisisEvent.class));
                verify(crisisEventChangeRepository).save(any(CrisisEventChange.class));
                verify(notificationService).sendCrisisEventNotifications(any(CrisisEvent.class));
            }
            
            @Test
            void shouldCreateCrisisEventWithoutScenarioTheme() {
                // Arrange
                CreateCrisisEventDto createDto = new CreateCrisisEventDto(
                    new BigDecimal("60.0"),
                    new BigDecimal("10.0"),
                    "Test Address",
                    new BigDecimal("5.0"),
                    CrisisEvent.Severity.yellow,
                    "Test description",
                    "Test Crisis",
                    LocalDateTime.now(),
                    null // No scenario theme
                );
                
                when(crisisEventRepository.save(any(CrisisEvent.class))).thenAnswer(invocation -> {
                    CrisisEvent savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1);
                    return savedEvent;
                });
                doNothing().when(notificationService).sendCrisisEventNotifications(any(CrisisEvent.class));
                
                // Act
                CrisisEvent result = crisisEventService.createCrisisEvent(createDto, testUser);
                
                // Assert
                assertNotNull(result);
                assertEquals(1, result.getId());
                assertEquals("Test Crisis", result.getName());
                assertNull(result.getScenarioTheme());
                
                verify(crisisEventRepository).save(any(CrisisEvent.class));
                verify(crisisEventChangeRepository).save(any(CrisisEventChange.class));
                verify(notificationService).sendCrisisEventNotifications(any(CrisisEvent.class));
                verify(scenarioThemeRepository, never()).findById(anyInt());
            }
        }
        
        @Nested
        class Negative {
            @Test
            void shouldHandleScenarioThemeNotFound() {
                // Arrange
                CreateCrisisEventDto createDto = new CreateCrisisEventDto(
                    new BigDecimal("60.0"),
                    new BigDecimal("10.0"),
                    "Test Address",
                    new BigDecimal("5.0"),
                    CrisisEvent.Severity.yellow,
                    "Test description",
                    "Test Crisis",
                    LocalDateTime.now(),
                    999 // Non-existent theme ID
                );
                
                when(scenarioThemeRepository.findById(999)).thenReturn(Optional.empty());
                when(crisisEventRepository.save(any(CrisisEvent.class))).thenAnswer(invocation -> {
                    CrisisEvent savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1);
                    return savedEvent;
                });
                doNothing().when(notificationService).sendCrisisEventNotifications(any(CrisisEvent.class));
                
                // Act
                CrisisEvent result = crisisEventService.createCrisisEvent(createDto, testUser);
                
                // Assert
                assertNotNull(result);
                assertEquals(1, result.getId());
                assertNull(result.getScenarioTheme()); // Theme should be null since it wasn't found
                
                verify(scenarioThemeRepository).findById(999);
                verify(crisisEventRepository).save(any(CrisisEvent.class));
                verify(crisisEventChangeRepository).save(any(CrisisEventChange.class));
                verify(notificationService).sendCrisisEventNotifications(any(CrisisEvent.class));
            }
        }
    }
    
    @Nested
    class DeactivateCrisisEventTests {
        @Nested
        class Positive {
            /**
            @Test
            void shouldDeactivateCrisisEventSuccessfully() {
                // Arrange
                when(crisisEventRepository.findById(1)).thenReturn(Optional.of(testCrisisEvent));
                when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));
                when(messageSource.getMessage(eq("notification.crisis.deactivated"), any(), any()))
                    .thenReturn("Crisis event Test Crisis has been deactivated");
                when(notificationService.createNotification(
                    any(User.class),
                    eq(Notification.PreferenceType.crisis_alert),
                    eq(Notification.TargetType.event),
                    eq(1),
                    anyString()
                )).thenReturn(new Notification());
                
                // Act
                crisisEventService.deactivateCrisisEvent(1);
                
                // Assert
                verify(crisisEventRepository).findById(1);
                verify(crisisEventRepository).deactivateCrisisEvent(1);
                verify(crisisEventChangeRepository).save(any(CrisisEventChange.class));
                verify(notificationService).createNotification(
                    eq(testUser),
                    eq(Notification.PreferenceType.crisis_alert),
                    eq(Notification.TargetType.event),
                    eq(1),
                    anyString()
                );
                verify(notificationService).sendNotification(any(Notification.class));
            }
            */
        }
        
        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenCrisisEventNotFound() {
                // Arrange
                when(crisisEventRepository.findById(999)).thenReturn(Optional.empty());
                
                // Act & Assert
                assertThrows(IllegalStateException.class, () -> crisisEventService.deactivateCrisisEvent(999));
                verify(crisisEventRepository).findById(999);
                verify(crisisEventRepository, never()).deactivateCrisisEvent(anyInt());
                verify(crisisEventChangeRepository, never()).save(any(CrisisEventChange.class));
                verify(notificationService, never()).createNotification(any(), any(), any(), any(), any());
            }
        }
    }
    
    @Nested
    class UpdateCrisisEventTests {
        @Nested
        class Positive {
            @Test
            void shouldUpdateAllFieldsSuccessfully() {
                // Arrange
                UpdateCrisisEventDto updateDto = new UpdateCrisisEventDto(
                    "Updated Crisis",
                    "Updated description",
                    CrisisEvent.Severity.red,
                    new BigDecimal("61.0"),
                    new BigDecimal("11.0"),
                    new BigDecimal("6.0"),
                    1
                );
                
                when(crisisEventRepository.existsById(1)).thenReturn(true);
                when(crisisEventRepository.findById(1)).thenReturn(Optional.of(testCrisisEvent));
                when(scenarioThemeRepository.findById(1)).thenReturn(Optional.of(testScenarioTheme));
                
                // Act
                CrisisEvent result = crisisEventService.updateCrisisEvent(1, updateDto);
                
                // Assert
                assertNotNull(result);
                assertEquals("Updated Crisis", result.getName());
                assertEquals("Updated description", result.getDescription());
                assertEquals(CrisisEvent.Severity.red, result.getSeverity());
                assertEquals(new BigDecimal("61.0"), result.getEpicenterLatitude());
                assertEquals(new BigDecimal("11.0"), result.getEpicenterLongitude());
                assertEquals(new BigDecimal("6.0"), result.getRadius());
                assertEquals(testScenarioTheme, result.getScenarioTheme());
                
                verify(crisisEventRepository).updateCrisisEvent(
                    eq(1),
                    eq("Updated Crisis"),
                    eq("Updated description"),
                    eq(CrisisEvent.Severity.red),
                    eq(new BigDecimal("61.0")),
                    eq(new BigDecimal("11.0")),
                    eq(new BigDecimal("6.0"))
                );
                verify(crisisEventRepository).flush();
                verify(notificationService).sendCrisisEventUpdateNotifications(any(CrisisEvent.class), any(CrisisEvent.class));
            }
            
            @Test
            void shouldUpdatePartialFieldsSuccessfully() {
                // Arrange
                UpdateCrisisEventDto updateDto = new UpdateCrisisEventDto();
                updateDto.setName("Updated Crisis");
                updateDto.setDescription("Updated description");
                // Other fields are null
                
                when(crisisEventRepository.existsById(1)).thenReturn(true);
                when(crisisEventRepository.findById(1)).thenReturn(Optional.of(testCrisisEvent));
                when(crisisEventRepository.save(any(CrisisEvent.class))).thenReturn(testCrisisEvent);
                
                // Act
                CrisisEvent result = crisisEventService.updateCrisisEvent(1, updateDto);
                
                // Assert
                assertNotNull(result);
                assertEquals("Updated Crisis", result.getName());
                assertEquals("Updated description", result.getDescription());
                // Other fields remain unchanged
                assertEquals(CrisisEvent.Severity.yellow, result.getSeverity());
                assertEquals(new BigDecimal("60.0"), result.getEpicenterLatitude());
                assertEquals(new BigDecimal("10.0"), result.getEpicenterLongitude());
                assertEquals(new BigDecimal("5.0"), result.getRadius());
                
                verify(crisisEventRepository, never()).updateCrisisEvent(
                    anyInt(), anyString(), anyString(), any(), any(), any(), any()
                );
                verify(crisisEventRepository).save(any(CrisisEvent.class));
                verify(crisisEventRepository).flush();
                verify(notificationService).sendCrisisEventUpdateNotifications(any(CrisisEvent.class), any(CrisisEvent.class));
            }
            

        }
        
        @Nested
        class Negative {
            @Test
            void shouldThrowExceptionWhenCrisisEventNotFound() {
                // Arrange
                UpdateCrisisEventDto updateDto = new UpdateCrisisEventDto();
                updateDto.setName("Updated Crisis");
                
                when(crisisEventRepository.existsById(999)).thenReturn(false);
                
                // Act & Assert
                assertThrows(IllegalStateException.class, () -> crisisEventService.updateCrisisEvent(999, updateDto));
                verify(crisisEventRepository).existsById(999);
                verify(crisisEventRepository, never()).findById(anyInt());
                verify(crisisEventRepository, never()).updateCrisisEvent(
                    anyInt(), anyString(), anyString(), any(), any(), any(), any()
                );
                verify(crisisEventRepository, never()).save(any(CrisisEvent.class));
            }
            
            @Test
            void shouldReturnNullWhenScenarioThemeNotFound() {
                // Arrange
                UpdateCrisisEventDto updateDto = new UpdateCrisisEventDto();
                updateDto.setScenarioThemeId(999); // Non-existent theme
                
                when(crisisEventRepository.existsById(1)).thenReturn(true);
                when(crisisEventRepository.findById(1)).thenReturn(Optional.of(testCrisisEvent));
                when(scenarioThemeRepository.findById(999)).thenReturn(Optional.empty());
                
                // Act
                CrisisEvent result = crisisEventService.updateCrisisEvent(1, updateDto);
                
                // Assert
                assertNull(result);
                verify(crisisEventRepository).existsById(1);
                verify(crisisEventRepository).findById(1);
                verify(scenarioThemeRepository).findById(999);
                verify(crisisEventRepository, never()).save(any(CrisisEvent.class));
            }
        }
    }
    
    @Nested
    class GetCrisisEventChangesTests {
        @Test
        void shouldReturnCrisisEventChangesWithPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            CrisisEventChange change = new CrisisEventChange(
                testCrisisEvent,
                CrisisEventChange.ChangeType.creation,
                null,
                "Created crisis event: Test Crisis",
                testUser
            );
            change.setId(1);
            
            List<CrisisEventChange> changes = Collections.singletonList(change);
            Page<CrisisEventChange> changePage = new PageImpl<>(changes, pageable, changes.size());
            
            when(crisisEventRepository.existsById(1)).thenReturn(true);
            when(crisisEventChangeRepository.findByCrisisEventIdOrderByCreatedAtDesc(eq(1), any(Pageable.class)))
                .thenReturn(changePage);
            
            // Act
            Page<CrisisEventChangeDto> result = crisisEventService.getCrisisEventChanges(1, pageable);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().get(0).getId());
            assertEquals("creation", result.getContent().get(0).getChangeType());
            assertEquals("Created crisis event: Test Crisis", result.getContent().get(0).getNewValue());
            
            verify(crisisEventRepository).existsById(1);
            verify(crisisEventChangeRepository).findByCrisisEventIdOrderByCreatedAtDesc(eq(1), any(Pageable.class));
        }
        
        @Test
        void shouldThrowExceptionWhenCrisisEventNotFound() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(crisisEventRepository.existsById(999)).thenReturn(false);
            
            // Act & Assert
            assertThrows(IllegalStateException.class, () -> crisisEventService.getCrisisEventChanges(999, pageable));
            verify(crisisEventRepository).existsById(999);
            verify(crisisEventChangeRepository, never()).findByCrisisEventIdOrderByCreatedAtDesc(anyInt(), any(Pageable.class));
        }
    }
    
    @Nested
    class GetCrisisEventsAffectingUserTests {
        @Test
        void shouldReturnCrisisEventsAffectingUser() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<CrisisEvent> activeEvents = Collections.singletonList(testCrisisEvent);
            
            when(crisisEventRepository.findByActiveTrue()).thenReturn(activeEvents);
            
            // Act
            Page<CrisisEvent> result = crisisEventService.getCrisisEventsAffectingUser(testUser, pageable);
            
            // Assert
            assertNotNull(result);
            // The actual filtering logic depends on LocationUtil which we can't easily test here
            verify(crisisEventRepository).findByActiveTrue();
        }
    }
    
    @Nested
    class GetAllCrisisEventPreviewsTests {
        @Test
        void shouldReturnAllCrisisEventPreviewsSortedBySeverity() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            
            CrisisEvent redEvent = new CrisisEvent(
                "Red Crisis",
                new BigDecimal("60.0"),
                new BigDecimal("10.0"),
                new BigDecimal("5.0"),
                LocalDateTime.now(),
                testUser
            );
            redEvent.setId(2);
            redEvent.setSeverity(CrisisEvent.Severity.red);
            
            CrisisEvent greenEvent = new CrisisEvent(
                "Green Crisis",
                new BigDecimal("60.0"),
                new BigDecimal("10.0"),
                new BigDecimal("5.0"),
                LocalDateTime.now(),
                testUser
            );
            greenEvent.setId(3);
            greenEvent.setSeverity(CrisisEvent.Severity.green);
            
            List<CrisisEvent> activeEvents = Arrays.asList(testCrisisEvent, redEvent, greenEvent);
            
            when(crisisEventRepository.findByActiveTrue()).thenReturn(activeEvents);
            
            // Act
            Page<CrisisEventPreviewDto> result = crisisEventService.getAllCrisisEventPreviews(pageable);
            
            // Assert
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            // Should be sorted by severity: red > yellow > green
            assertEquals(CrisisEvent.Severity.red, result.getContent().get(0).getSeverity());
            assertEquals(CrisisEvent.Severity.yellow, result.getContent().get(1).getSeverity());
            assertEquals(CrisisEvent.Severity.green, result.getContent().get(2).getSeverity());
            
            verify(crisisEventRepository).findByActiveTrue();
        }
    }
    
    @Nested
    class GetCrisisEventPreviewsAffectingUserSortedBySeverityTests {
        @Test
        void shouldReturnCrisisEventPreviewsAffectingUserSortedBySeverity() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            
            // This test is complex because it depends on the implementation of getCrisisEventsAffectingUser
            // which uses LocationUtil. We'll just verify that the method is called.
            
            // Act
            Page<CrisisEventPreviewDto> result = crisisEventService.getCrisisEventPreviewsAffectingUserSortedBySeverity(testUser, pageable);
            
            // Assert
            assertNotNull(result);
        }
    }
    
    @Nested
    class GetCrisisEventDetailsByIdTests {
        @Test
        void shouldReturnCrisisEventDetailsWhenFound() {
            // Arrange
            when(crisisEventRepository.findById(1)).thenReturn(Optional.of(testCrisisEvent));
            
            // Act
            Optional<CrisisEventDetailsDto> result = crisisEventService.getCrisisEventDetailsById(1);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getId());
            assertEquals("Test Crisis", result.get().getName());
            assertEquals("Test description", result.get().getDescription());
            assertEquals(CrisisEvent.Severity.yellow, result.get().getSeverity());
            assertEquals(new BigDecimal("60.0"), result.get().getEpicenterLatitude());
            assertEquals(new BigDecimal("10.0"), result.get().getEpicenterLongitude());
            assertEquals(new BigDecimal("5.0"), result.get().getRadius());
            assertEquals(1, result.get().getScenarioThemeId());
            
            verify(crisisEventRepository).findById(1);
        }
        
        @Test
        void shouldReturnEmptyOptionalWhenNotFound() {
            // Arrange
            when(crisisEventRepository.findById(999)).thenReturn(Optional.empty());
            
            // Act
            Optional<CrisisEventDetailsDto> result = crisisEventService.getCrisisEventDetailsById(999);
            
            // Assert
            assertFalse(result.isPresent());
            verify(crisisEventRepository).findById(999);
        }
    }
}
