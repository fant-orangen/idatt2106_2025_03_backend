package stud.ntnu.backend.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.NotificationService;

/**
 * Scheduled service that checks for expiring and expired products and sends notifications to users
 * in the corresponding households. This service runs daily at 1 AM to check for products that are
 * either expired or will expire within the next week, and sends appropriate notifications to users
 * in the affected households.
 */
@Component
@RequiredArgsConstructor
public class ExpirationNotificationScheduler {

  /**
   * Flag to enable test mode, which runs checks every 15 seconds instead of daily. Should be set to
   * false in production.
   */
  private static final boolean TEST_MODE = false;

  /**
   * Repository for accessing product batch data.
   */
  private final ProductBatchRepository productBatchRepository;

  /**
   * Repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Repository for accessing notification preferences.
   */
  private final NotificationPreferenceRepository notificationPreferenceRepository;

  /**
   * Service for handling notification operations.
   */
  private final NotificationService notificationService;

  /**
   * Source for internationalized messages.
   */
  private final MessageSource messageSource;

  /**
   * Formatter for dates in the Norwegian format (dd.MM.yyyy).
   */
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  /**
   * Norwegian locale for message formatting.
   */
  private static final Locale LOCALE = Locale.forLanguageTag("nb-NO");

  /**
   * Scheduled task that runs daily at 1 AM to check for expiring and expired products. Sends
   * notifications to users based on their notification preferences. This method will only run if
   * test mode is disabled.
   */
  @Scheduled(cron = "0 0 1 * * ?")
  public void checkExpiringProducts() {
    if (TEST_MODE) {
      return;
    }
    performExpirationCheck();
  }

  /**
   * Scheduled task that runs every 15 seconds for testing purposes. This method will only run if
   * test mode is enabled.
   */
  @Scheduled(fixedRate = 15000)
  public void testModeCheckExpiringProducts() {
    if (!TEST_MODE) {
      return;
    }
    performExpirationCheck();
  }

  /**
   * Core method containing the expiration check logic. Extracted to avoid duplication between the
   * daily and test mode schedulers. Checks for both expired products and products expiring within
   * the next week.
   */
  private void performExpirationCheck() {
    try {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime oneWeekFromNow = now.plusDays(7);

      List<ProductBatch> expiringBatches = productBatchRepository.findExpiringBatches(now,
          oneWeekFromNow);
      processExpiringBatches(expiringBatches, false);

      List<ProductBatch> expiredBatches = productBatchRepository.findExpiredBatches(now);
      processExpiringBatches(expiredBatches, true);
    } catch (Exception e) {
      // Exception handling is intentionally empty as this is a background process
    }
  }

  /**
   * Processes expiring or expired batches and sends notifications to users in the affected
   * households.
   *
   * @param batches   The list of product batches to process
   * @param isExpired Whether these are expired batches (true) or expiring batches (false)
   */
  @Transactional
  private void processExpiringBatches(List<ProductBatch> batches, boolean isExpired) {
    for (ProductBatch batch : batches) {
      try {
        Integer householdId = batch.getProductType().getHousehold().getId();
        List<User> users = userRepository.findByHouseholdId(householdId);

        if (users.isEmpty()) {
          continue;
        }

        String message = createNotificationMessage(batch, batches.size(), isExpired);

        for (User user : users) {
          Notification notification = notificationService.createNotification(
              user,
              Notification.PreferenceType.expiration_reminder,
              Notification.TargetType.inventory,
              batch.getId(),
              message);

          notificationService.sendNotification(notification);
        }
      } catch (Exception e) {
        // Exception handling is intentionally empty as this is a background process
      }
    }
  }

  /**
   * Creates the appropriate notification message based on the batch status and size.
   *
   * @param batch     The product batch to create a message for
   * @param batchSize The total number of batches being processed
   * @param isExpired Whether the batch is expired (true) or expiring (false)
   * @return The formatted notification message
   */
  private String createNotificationMessage(ProductBatch batch, int batchSize, boolean isExpired) {
    if (batchSize == 1) {
      String productName = batch.getProductType().getName();
      int quantity = batch.getNumber();
      String unit = batch.getProductType().getUnit();
      String date = batch.getExpirationTime().format(DATE_FORMATTER);

      String messageKey =
          isExpired ? "notification.expired.single" : "notification.expiring.single";
      return getMessage(messageKey, productName, quantity, unit, date);
    } else {
      String messageKey =
          isExpired ? "notification.expired.multiple" : "notification.expiring.multiple";
      return getMessage(messageKey, batchSize);
    }
  }

  /**
   * Retrieves and formats a message from the message source.
   *
   * @param code The message code to retrieve
   * @param args Arguments to be formatted into the message
   * @return The formatted message string
   */
  private String getMessage(String code, Object... args) {
    return messageSource.getMessage(code, args, LOCALE);
  }
}
