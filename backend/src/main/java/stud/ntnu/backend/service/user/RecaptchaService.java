package stud.ntnu.backend.service.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Service responsible for verifying Google reCAPTCHA tokens.
 * This service handles the communication with Google's reCAPTCHA API to validate
 * tokens and determine if a user's interaction is legitimate based on a score threshold.
 */
@Service
public class RecaptchaService {

  @Value("${recaptcha.secret}")
  private String recaptchaSecret;

  private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  /**
   * Verifies a reCAPTCHA token by sending it to Google's verification API.
   * The verification process checks both the token's validity and the associated risk score.
   * A score of 0.5 or higher is considered acceptable for most use cases.
   *
   * @param recaptchaToken The reCAPTCHA token to verify, obtained from the client-side reCAPTCHA widget
   * @return true if the token is valid and the risk score is 0.5 or higher, false otherwise
   * @throws IllegalArgumentException if the recaptchaToken is null or empty
   */
  public boolean verifyRecaptcha(String recaptchaToken) {
    if (recaptchaToken == null || recaptchaToken.trim().isEmpty()) {
      throw new IllegalArgumentException("Recaptcha token cannot be null or empty");
    }

    RestTemplate restTemplate = new RestTemplate();

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("secret", recaptchaSecret);
    requestBody.add("response", recaptchaToken);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/x-www-form-urlencoded");

    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, requestEntity, Map.class);

    Map<String, Object> responseBody = response.getBody();
    if (responseBody == null) {
      return false;
    }

    Boolean success = (Boolean) responseBody.get("success");
    Double score = (Double) responseBody.get("score");

    return success != null && success && score != null && score >= 0.5;
  }
}