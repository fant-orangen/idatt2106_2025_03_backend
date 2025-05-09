package stud.ntnu.backend.service.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.EmailTokenRepository;
import stud.ntnu.backend.repository.user.UserRepository;

/**
 * Service responsible for handling email sending operations, such as verification emails. Uses
 * Spring's JavaMailSender for dispatching emails.
 */
@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final String senderEmail;
  private final UserRepository userRepository;
  private final EmailTokenRepository emailTokenRepository;
  private final MessageSource messageSource;

  /**
   * Constructs the EmailService with necessary dependencies injected by Spring.
   *
   * @param mailSender           The Spring JavaMailSender bean for sending emails.
   * @param senderEmail          The sender's email address, injected from application properties
   *                             (spring.mail.username).
   * @param userRepository       The repository for user operations.
   * @param emailTokenRepository The repository for email token operations.
   * @param messageSource        The MessageSource for internationalization.
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
   * verification link. Includes content in both Norwegian and English.
   *
   * @param user  The User object representing the recipient. Must have a valid email address.
   * @param token The unique verification token string to include in the link.
   */
  public void sendVerificationEmail(User user, String token) {
    if (user == null || user.getEmail() == null || token == null) {
      throw new IllegalArgumentException(
          "Cannot send verification email. User or token is null or user email is null.");
    }

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(senderEmail);
      message.setTo(user.getEmail());

      String userName = (user.getName() != null ? user.getName() : "Bruker/User");
      String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;

      message.setSubject(messageSource.getMessage("verification.email.subject", null,
          LocaleContextHolder.getLocale()));
      message.setText(messageSource.getMessage("verification.email.body",
          new Object[]{userName, verificationUrl},
          LocaleContextHolder.getLocale()));

      mailSender.send(message);

    } catch (RuntimeException e) {
      throw new RuntimeException("Failed to send verification email", e);
    }
  }

  /**
   * Sends a 2FA email to the specified user.
   *
   * @param email The email address of the user to send the 2FA code to.
   * @param code  The unique 2FA token string to include in the email.
   */
  public void send2FAEmail(String email, Integer code) throws MessagingException {
    if (email == null || code == null) {
      throw new IllegalArgumentException("Cannot send 2FA email. Email or code is null.");
    }

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

    try {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(user.getEmail());
        helper.setFrom(senderEmail);

        String userName = (user.getName() != null ? user.getName() : "Bruker/User");

        helper.setSubject(messageSource.getMessage("twofa.email.subject", null, LocaleContextHolder.getLocale()));
        String emailBody = messageSource.getMessage("twofa.email.body",
            new Object[]{userName, String.format("%06d", code)},
            LocaleContextHolder.getLocale());
        helper.setText(emailBody, true); // Set 'true' to indicate HTML content

      mailSender.send(mimeMessage);

    } catch (RuntimeException e) {
      throw new RuntimeException("Failed to send 2FA email", e);
    } catch (MessagingException e) {
        throw new MessagingException("Mail sending error for 2fa", e);
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
      throw new IllegalArgumentException(
          "Cannot send password reset email. User or token is null or user email is null.");
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
          new Object[]{userName, resetPasswordUrl},
          LocaleContextHolder.getLocale()), true);

      mailSender.send(mimeMessage);

    } catch (MessagingException | RuntimeException e) {
      throw new RuntimeException("Failed to send password reset email", e);
    }
  }

  /**
   * Sends a safety confirmation email to a specific household member.
   *
   * @param requestingUser The user requesting safety confirmation
   * @param receivingUser  The user receiving the safety confirmation request
   * @param token          The unique token for this safety confirmation
   * @throws MessagingException if there are issues sending the email
   * @throws RuntimeException   for other unexpected errors
   */
  public void sendSafetyConfirmationEmail(User requestingUser, User receivingUser, String token) {
    if (requestingUser == null || receivingUser == null || token == null) {
      throw new IllegalArgumentException("Invalid parameters for safety confirmation email.");
    }

    try {
      String requestingUserName = (requestingUser.getName() != null ? requestingUser.getName()
          : "et husstandsmedlem");
      String receivingUserName = (receivingUser.getName() != null ? receivingUser.getName()
          : "Bruker");

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

      helper.setFrom(senderEmail);
      helper.setTo(receivingUser.getEmail());

      String subject = messageSource.getMessage("safety.confirmation.subject", null,
          LocaleContextHolder.getLocale());
      helper.setSubject(subject);

      String emailBody = messageSource.getMessage("safety.confirmation.body",
          new Object[]{receivingUserName, requestingUserName, token},
          LocaleContextHolder.getLocale());

      helper.setText(emailBody, true);

      mailSender.send(mimeMessage);

    } catch (MessagingException | RuntimeException e) {
      throw new RuntimeException("Failed to send safety confirmation email", e);
    }
  }
}
