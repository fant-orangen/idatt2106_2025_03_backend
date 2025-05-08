package stud.ntnu.backend.event;

import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.NotificationPreference;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.inventory.InventoryService;
import stud.ntnu.backend.service.user.NotificationService;

/**
 * Event listener that handles inventory change events and sends notifications to users when
 * their household's supplies are running low.
 * 
 * This listener monitors both water and food supplies, calculating the number of days of supply
 * remaining based on daily requirements. When supplies fall below the warning threshold,
 * notifications are sent to all users in the household who have enabled supply alerts.
 */
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

  /**
   * Service for managing inventory operations and calculations.
   */
  private final InventoryService inventoryService;

  /**
   * Service for handling notification creation and delivery.
   */
  private final NotificationService notificationService;

  /**
   * Repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Repository for accessing notification preferences.
   */
  private final NotificationPreferenceRepository notificationPreferenceRepository;

  /**
   * Source for internationalized messages.
   */
  private final MessageSource messageSource;

  /**
   * The number of days of supply remaining that triggers a warning notification.
   * When supplies fall below this threshold, users will be notified.
   */
  private static final int DAYS_WARNING_THRESHOLD = 7;

  /**
   * Handles inventory change events by checking if supplies are running low and sending
   * notifications to household members if necessary.
   * 
   * This method:
   * 1. Calculates daily water and calorie requirements for the household
   * 2. Gets current inventory levels for water and food
   * 3. Calculates days of supply remaining
   * 4. If supplies are below threshold, sends notifications to household members
   * 
   * @param event The inventory change event containing the household ID
   */
  @Async
  @EventListener
  public void handleInventoryChangeEvent(InventoryChangeEvent event) {
    // Get household requirements
    int requiredWaterPerDay = inventoryService.getHouseholdWaterRequirement(event.getHouseholdId());
    int requiredCaloriesPerDay = inventoryService.getHouseholdCalorieRequirement(
        event.getHouseholdId());

    // Get current inventory levels
    int totalWater = inventoryService.getTotalLitresOfWaterByHousehold(event.getHouseholdId());
    int totalCalories = inventoryService.getTotalCaloriesByHousehold(event.getHouseholdId());

    // Calculate days of supply left
    double waterDaysLeft = (double) totalWater / requiredWaterPerDay;
    double calorieDaysLeft = (double) totalCalories / requiredCaloriesPerDay;

    // Get users in household
    List<User> householdUsers = userRepository.findByHouseholdId(event.getHouseholdId());

    // Check water threshold
    if (waterDaysLeft < DAYS_WARNING_THRESHOLD) {
      String waterMessage = messageSource.getMessage(
          "notification.low.water",
          new Object[]{
              String.format("%.1f", waterDaysLeft),
              totalWater,
              requiredWaterPerDay
          },
          LocaleContextHolder.getLocale()
      );

      for (User user : householdUsers) {
        // Check if user has enabled notifications for remaining supply alerts
        Optional<NotificationPreference> preference = notificationPreferenceRepository
            .findByUserAndPreferenceType(user, Notification.PreferenceType.remaining_supply_alert);
        
        if (preference.isEmpty() || preference.get().isEnabled()) {
          Notification notification = notificationService.createNotification(
              user,
              Notification.PreferenceType.remaining_supply_alert,
              Notification.TargetType.inventory,
              null,
              waterMessage
          );
          notificationService.sendNotification(notification);
        }
      }
    }

    // Check calorie threshold
    if (calorieDaysLeft < DAYS_WARNING_THRESHOLD) {
      String foodMessage = messageSource.getMessage(
          "notification.low.food",
          new Object[]{
              String.format("%.1f", calorieDaysLeft),
              totalCalories,
              requiredCaloriesPerDay
          },
          LocaleContextHolder.getLocale()
      );

      for (User user : householdUsers) {
        // Check if user has enabled notifications for remaining supply alerts
        Optional<NotificationPreference> preference = notificationPreferenceRepository
            .findByUserAndPreferenceType(user, Notification.PreferenceType.remaining_supply_alert);
        
        if (preference.isEmpty() || preference.get().isEnabled()) {
          Notification notification = notificationService.createNotification(
              user,
              Notification.PreferenceType.remaining_supply_alert,
              Notification.TargetType.inventory,
              null,
              foodMessage
          );
          notificationService.sendNotification(notification);
        }
      }
    }
  }
}