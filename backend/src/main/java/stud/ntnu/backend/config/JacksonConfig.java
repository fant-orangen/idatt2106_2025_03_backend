package stud.ntnu.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for Jackson JSON serialization. Configures the ObjectMapper to handle
 * circular references and other serialization settings.
 */
@Configuration
public class JacksonConfig {

  /**
   * Creates and configures the primary ObjectMapper bean used for JSON serialization.
   *
   * @return configured ObjectMapper instance
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    // Handle circular references
    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    // Configure to prevent infinite recursion for bidirectional relationships
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    // Register module to properly serialize Java 8 date/time types
    objectMapper.registerModule(new JavaTimeModule());

    return objectMapper;
  }
}
