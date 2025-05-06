package stud.ntnu.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable scheduling capabilities in the application.
 * This allows the use of @Scheduled annotations for recurring tasks.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // No additional configuration needed - the annotation handles enabling the scheduling subsystem
} 