package stud.ntnu.backend.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * <h2>RecaptchaService</h2>
 *
 * <p>Service for verifying Google reCAPTCHA tokens.</p>
 */
@Service
public class RecaptchaService {

  @Value("${recaptcha.secret}")
  private String recaptchaSecret;

  private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  /**
   * Verifies the reCAPTCHA token with Google's API.
   *
   * @param recaptchaToken The reCAPTCHA token to verify.
   * @return true if the token is valid and the score is above the threshold, false otherwise.
   */
  public boolean verifyRecaptcha(String recaptchaToken) {
    RestTemplate restTemplate = new RestTemplate();

    // Prepare the request body
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("secret", recaptchaSecret);
    requestBody.add("response", recaptchaToken);

    // Set headers
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/x-www-form-urlencoded");

    // Create the HTTP entity
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody,
        headers);

    // Send the POST request to Google's reCAPTCHA API
    ResponseEntity<Map> response = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, requestEntity,
        Map.class);

    // Parse the response
    Map<String, Object> responseBody = response.getBody();
    if (responseBody == null) {
      return false;
    }

    Boolean success = (Boolean) responseBody.get("success");
    Double score = (Double) responseBody.get("score");

    // Check if the reCAPTCHA is valid and the score is above the threshold
    System.out.println("Score: " + score);
    return success != null && success && score != null && score >= 0.5;
  }
}