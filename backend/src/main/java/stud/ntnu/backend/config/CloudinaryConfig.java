package stud.ntnu.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h2>CloudinaryConfig</h2>
 * <p>Configuration class for Cloudinary integration with the marketplace application.</p>
 * <p>This class sets up the Cloudinary client with credentials from application properties,
 * making it available as a Spring bean for dependency injection in service classes.</p>
 *
 * <h3>Usage Context</h3>
 * <p>The Cloudinary service is primarily used for:</p>
 * <ul>
 *   <li>Storing item images in the cloud</li>
 *   <li>Generating public URLs for uploaded images</li>
 *   <li>Managing media assets for the marketplace items</li>
 * </ul>
 *
 */
@Configuration
public class CloudinaryConfig {

  /**
   * <h3>Cloudinary Cloud Name</h3>
   * <p>The unique cloud name for the Cloudinary account.</p>
   * <p>Injected from {@code cloudinary.cloud-name} property.</p>
   */
  @Value("${cloudinary.cloud-name}")
  private String cloudName;

  /**
   * <h3>Cloudinary API Key</h3>
   * <p>Authentication key for the Cloudinary API.</p>
   * <p>Injected from {@code cloudinary.api-key} property.</p>
   */
  @Value("${cloudinary.api-key}")
  private String apiKey;

  /**
   * <h3>Cloudinary API Secret</h3>
   * <p>Secret key for Cloudinary API authentication.</p>
   * <p>Injected from {@code cloudinary.api-secret} property.</p>
   */
  @Value("${cloudinary.api-secret}")
  private String apiSecret;


  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
  }
}