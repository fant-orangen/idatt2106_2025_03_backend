package stud.ntnu.backend.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.inventory.ProductService;
import stud.ntnu.backend.service.user.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

  private final ProductService productService;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  private static final int DAYS_WARNING_THRESHOLD = 7;

  @Async
  @EventListener
  public void handleInventoryChangeEvent(InventoryChangeEvent event) {
    log.debug("Processing inventory change for household: {}", event.getHouseholdId());

    // Get household requirements
    int requiredWaterPerDay = productService.getHouseholdWaterRequirement(event.getHouseholdId());
    int requiredCaloriesPerDay = productService.getHouseholdCalorieRequirement(
        event.getHouseholdId());

    // Get current inventory levels
    int totalWater = productService.getTotalLitresOfWaterByHousehold(event.getHouseholdId());
    int totalCalories = productService.getTotalCaloriesByHousehold(event.getHouseholdId());

    // Calculate days of supply left
    double waterDaysLeft = (double) totalWater / requiredWaterPerDay;
    double calorieDaysLeft = (double) totalCalories / requiredCaloriesPerDay;

    // Get users in household
    List<User> householdUsers = userRepository.findByHouseholdId(event.getHouseholdId());

    // Check water threshold
    if (waterDaysLeft < DAYS_WARNING_THRESHOLD) {
      String waterMessage = String.format(
          "⚠️ Lav vannbeholdning: Du har kun %.1f dager igjen med vann basert på nåværende forbruk (%d liter tilgjengelig, %d liter per dag nødvendig).",
          waterDaysLeft, totalWater, requiredWaterPerDay
      );

      for (User user : householdUsers) {
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

    // Check calorie threshold
    if (calorieDaysLeft < DAYS_WARNING_THRESHOLD) {
      String foodMessage = String.format(
          "⚠️ Lav matbeholdning: Du har kun %.1f dager igjen med mat basert på nåværende forbruk (%d kcal tilgjengelig, %d kcal per dag nødvendig).",
          calorieDaysLeft, totalCalories, requiredCaloriesPerDay
      );

      for (User user : householdUsers) {
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