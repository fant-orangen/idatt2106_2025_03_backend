package stud.ntnu.backend.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.model.inventory.ProductBatch;
import stud.ntnu.backend.model.user.Notification;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.inventory.ProductBatchRepository;
import stud.ntnu.backend.repository.user.NotificationPreferenceRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.user.NotificationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Scheduled service that checks for expiring and expired products
 * and sends notifications to users in the corresponding households.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpirationNotificationScheduler {

    // Set this to true to enable test mode with notifications every 15 seconds
    private static final boolean TEST_MODE = true;

    private final ProductBatchRepository productBatchRepository;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationService notificationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Scheduled task that runs daily at 1 AM to check for expiring and expired products.
     * Sends notifications to users based on their notification preferences.
     * This method will only run if test mode is disabled.
     */
    @Scheduled(cron = "0 0 1 * * ?")  // Run at 1:00 AM every day
    public void checkExpiringProducts() {
        if (TEST_MODE) {
            // Skip execution if in test mode
            return;
        }
        
        log.info("Running daily expiration check");
        performExpirationCheck();
    }
    
    /**
     * Scheduled task that runs every 15 seconds for testing purposes.
     * This method will only run if test mode is enabled.
     */
    @Scheduled(fixedRate = 15000)  // Run every 15 seconds (15000 ms)
    public void testModeCheckExpiringProducts() {
        if (!TEST_MODE) {
            // Skip execution if not in test mode
            return;
        }
        
        log.info("Running test mode expiration check (15-second interval)");
        performExpirationCheck();
    }
    
    /**
     * Core method containing the expiration check logic.
     * Extracted to avoid duplication between the daily and test mode schedulers.
     */
    private void performExpirationCheck() {
        try {
            // Calculate relevant dates
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneWeekFromNow = now.plusDays(7);
            
            // Get products expiring in the next week
            List<ProductBatch> expiringBatches = productBatchRepository.findExpiringBatches(now, oneWeekFromNow);
            processExpiringBatches(expiringBatches, false);
            
            // Get expired products
            List<ProductBatch> expiredBatches = productBatchRepository.findExpiredBatches(now);
            processExpiringBatches(expiredBatches, true);
            
            log.info("Expiration check completed - processed {} expiring and {} expired batches", 
                    expiringBatches.size(), expiredBatches.size());
            
        } catch (Exception e) {
            log.error("Error during expiration check", e);
        }
    }
    
    /**
     * Process expiring or expired batches and send notifications
     * 
     * @param batches The list of product batches
     * @param isExpired Whether these are expired batches (true) or expiring batches (false)
     */
    @Transactional
    private void processExpiringBatches(List<ProductBatch> batches, boolean isExpired) {
        for (ProductBatch batch : batches) {
            try {
                // Get the household ID and find all users in that household
                Integer householdId = batch.getProductType().getHousehold().getId();
                List<User> users = userRepository.findByHouseholdId(householdId);
                
                if (users.isEmpty()) {
                    continue;
                }
                
                // Create the notification message
                String productName = batch.getProductType().getName();
                int quantity = batch.getNumber();
                String unit = batch.getProductType().getUnit();
                String date = batch.getExpirationTime().format(DATE_FORMATTER);
                
                String message;
                if (isExpired) {
                    message = String.format(
                        "⚠️ Produktet '%s' (%d %s) har utløpt (%s).",
                        productName, quantity, unit, date);
                } else {
                    message = String.format(
                        "⚠️ Produktet '%s' (%d %s) utløper om mindre enn 7 dager (%s).",
                        productName, quantity, unit, date);
                }
                
                // Send notification to each user in the household
                for (User user : users) {
                    Notification notification = notificationService.createNotification(
                        user,
                        Notification.PreferenceType.expiration_reminder,
                        Notification.TargetType.inventory,
                        batch.getId(),
                        message);
                    
                    notificationService.sendNotification(notification);
                    log.debug("Sent expiration notification to user {}: {}", user.getId(), message);
                }
            } catch (Exception e) {
                log.error("Error processing batch {}", batch.getId(), e);
            }
        }
    }
}
