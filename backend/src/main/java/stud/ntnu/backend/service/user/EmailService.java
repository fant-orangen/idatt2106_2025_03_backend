package stud.ntnu.backend.service.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.repository.user.EmailTokenRepository;
import stud.ntnu.backend.model.user.EmailToken;
import stud.ntnu.backend.model.user.EmailToken.TokenType;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import stud.ntnu.backend.model.household.Household;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Service responsible for handling email sending operations, such as verification emails. Uses
 * Spring's JavaMailSender for dispatching emails.
 */
@Service
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender mailSender;
  private final String senderEmail;
  private final UserRepository userRepository;
  private final EmailTokenRepository emailTokenRepository;
  private final MessageSource messageSource;

  /**
   * Constructs the EmailService with necessary dependencies injected by Spring.
   *
   * @param mailSender  The Spring JavaMailSender bean for sending emails.
   * @param senderEmail The sender's email address, injected from application properties
   *                    (spring.mail.username).
   * @param userRepository The repository for user operations.
   * @param emailTokenRepository The repository for email token operations.
   * @param messageSource The MessageSource for internationalization.
   */
  @Autowired
  public EmailService(JavaMailSender mailSender,
      @Value("${spring.mail.username}") String senderEmail,
      UserRepository userRepository,
      EmailTokenRepository emailTokenRepository,
      MessageSource messageSource) {
    this.mailSender = mailSender;
    this.senderEmail = senderEmail;
    this.userRepository = userRepository;
    this.emailTokenRepository = emailTokenRepository;
    this.messageSource = messageSource;
  }

  /**
   * Sends a verification email to the specified user. The email contains a unique token within a
   * verification link. Includes content in both Norwegian and English. Logs success or errors
   * during the sending process.
   *
   * @param user  The User object representing the recipient. Must have a valid email address.
   * @param token The unique verification token string to include in the link.
   */
  public void sendVerificationEmail(User user, String token) {
    if (user == null || user.getEmail() == null || token == null) {
      log.error("Cannot send verification email. User or token is null or user email is null.");
      return;
    }

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(senderEmail);
      message.setTo(user.getEmail());
      
      String userName = (user.getName() != null ? user.getName() : "Bruker/User");
      String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;

      message.setSubject(messageSource.getMessage("verification.email.subject", null, LocaleContextHolder.getLocale()));
      message.setText(messageSource.getMessage("verification.email.body", 
          new Object[]{userName, verificationUrl}, 
          LocaleContextHolder.getLocale()));

      mailSender.send(message);
      log.info("Verification email sent successfully to: {}", user.getEmail());

    } catch (MailException e) {
      log.error("Mail sending error for verification email to {}", user.getEmail());
    } catch (Exception e) {
      log.error("Unexpected error sending verification email to {}: {}", user.getEmail(), e.getMessage());
    }
  }

  /**
   * Sends a 2FA email to the specified user.
   *
   * @param email The email address of the user to send the 2FA code to.
   * @param code  The unique 2FA token string to include in the email.
   */
  public void send2FAEmail(String email, Integer code) {
    if (email == null || code == null) {
      log.error("Cannot send 2FA email. User or token is null or user email is null.");
      return;
    }

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(user.getEmail());
      message.setFrom(senderEmail);
      
      String userName = (user.getName() != null ? user.getName() : "Bruker/User");

      message.setSubject(messageSource.getMessage("twofa.email.subject", null, LocaleContextHolder.getLocale()));
      message.setText(messageSource.getMessage("twofa.email.body", 
          new Object[]{userName, code}, 
          LocaleContextHolder.getLocale()));

      mailSender.send(message);
      log.info("2FA email sent successfully to: {}", user.getEmail());

    } catch (MailException e) {
      log.error("Mail sending error for verification email to {}", user.getEmail());
    } catch (Exception e) {
      log.error("Unexpected error sending 2FA email to {}: {}", user.getEmail(), e.getMessage());
    }
  }

  /**
   * Sends a password reset email to the specified user.
   *
   * @param user  The User object representing the recipient. Must have a valid email address.
   * @param token The unique reset token string to include in the link.
   */
  public void sendPasswordResetEmail(User user, String token) {
    if (user == null || user.getEmail() == null || token == null) {
      log.error("Cannot send password reset email. User or token is null or user email is null.");
      return;
    }

    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

      helper.setFrom(senderEmail);
      helper.setTo(user.getEmail());

      String userName = (user.getName() != null ? user.getName() : "Bruker/User");
      String resetPasswordUrl = "http://localhost:5173/reset-password/" + token;

      helper.setSubject(messageSource.getMessage("password.reset.subject", null, LocaleContextHolder.getLocale()));
      helper.setText(messageSource.getMessage("password.reset.body", 
          new Object[]{userName, token, resetPasswordUrl}, 
          LocaleContextHolder.getLocale()), true);

      mailSender.send(mimeMessage);
      log.info("Password reset email sent successfully to: {}", user.getEmail());

    } catch (MessagingException e) {
      log.error("Mail sending error for password reset email to {}", user.getEmail());
    } catch (Exception e) {
      log.error("Unexpected error sending password reset email to {}: {}", user.getEmail(), e.getMessage());
    }
  }

  /**
   * Sends a safety confirmation email to a specific household member.
   *
   * @param requestingUser The user requesting safety confirmation
   * @param receivingUser The user receiving the safety confirmation request
   * @param token The unique token for this safety confirmation
   * @throws MessagingException if there are issues sending the email
   * @throws RuntimeException for other unexpected errors
   */
  public void sendSafetyConfirmationEmail(User requestingUser, User receivingUser, String token) {
    if (requestingUser == null || receivingUser == null || token == null) {
      log.error("Cannot send safety confirmation email. Invalid parameters provided.");
      throw new IllegalArgumentException("Invalid parameters for safety confirmation email.");
    }

    try {
      log.info("Preparing to send safety confirmation email to: {}", receivingUser.getEmail());
      
      String requestingUserName = (requestingUser.getName() != null ? requestingUser.getName() : "et husstandsmedlem");
      String receivingUserName = (receivingUser.getName() != null ? receivingUser.getName() : "Bruker");

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

      helper.setFrom(senderEmail);
      helper.setTo(receivingUser.getEmail());
      
      String subject = messageSource.getMessage("safety.confirmation.subject", null, LocaleContextHolder.getLocale());
      helper.setSubject(subject);

      String emailBody = messageSource.getMessage("safety.confirmation.body", 
          new Object[]{receivingUserName, requestingUserName, token}, 
          LocaleContextHolder.getLocale());

      helper.setText(emailBody, true);

      mailSender.send(mimeMessage);
      log.info("Safety confirmation email sent successfully to: {}", receivingUser.getEmail());

    } catch (MessagingException e) {
      log.error("Mail sending error for safety confirmation email to {}: {}", receivingUser.getEmail(), e.getMessage());
      throw new RuntimeException("Failed to send safety confirmation email", e);
    } catch (Exception e) {
      log.error("Unexpected error sending safety confirmation email to {}: {}", receivingUser.getEmail(), e.getMessage(), e);
      throw new RuntimeException("Unexpected error during safety confirmation", e);
    }
  }
}
