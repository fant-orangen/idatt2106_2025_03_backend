package stud.ntnu.backend.service.user;

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
import java.util.List;
import stud.ntnu.backend.model.household.Household;

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


  /**
   * Constructs the EmailService with necessary dependencies injected by Spring.
   *
   * @param mailSender  The Spring JavaMailSender bean for sending emails.
   * @param senderEmail The sender's email address, injected from application properties
   *                    (spring.mail.username).
   * @param userRepository The repository for user operations.
   */
  @Autowired
  public EmailService(JavaMailSender mailSender,
      @Value("${spring.mail.username}") String senderEmail,
      UserRepository userRepository) {
    this.mailSender = mailSender;
    this.senderEmail = senderEmail;
    this.userRepository = userRepository;
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
      log.error(
          "Cannot send verification email. User or token is null or user email is null.");
      return;
    }

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(senderEmail);
      message.setTo(user.getEmail());
      message.setSubject(
          "Krisefikser.no - Vennligst bekreft e-posten din / Please Verify Your Email");

      String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
      String userName = (user.getName() != null ? user.getName() : "Bruker/User");

      // Bilingual Email Body using Text Block and .formatted()
      String emailBody = """
          Hei %s,
          
          Takk for at du registrerte deg hos Krisefikser.no.
          Vennligst klikk på lenken under for å bekrefte e-postadressen din:
          
          %s
          
          Hvis du ikke registrerte deg, vennligst se bort fra denne e-posten.
          
          Med vennlig hilsen,
          Krisefikser-teamet
          
          ----------------------------------------
          
          Hello %s,
          
          Thank you for registering with Krisefikser.no.
          Please click the link below to verify your email address:
          
          %s
          
          If you did not register, please ignore this email.
          
          Regards,
          The Krisefikser Team
          """.formatted(userName, verificationUrl, userName, verificationUrl);

      message.setText(emailBody);

      message.setText(emailBody);

      mailSender.send(message);
      log.info("Verification email sent successfully to: {}", user.getEmail());


    } catch (MailException e) {
      log.error("Mail sending error for verification email to {}", user.getEmail());
    } catch (Exception e) {
      log.error("Unexpected error sending verification email to {}: {}", user.getEmail(),
          e.getMessage());
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
      message.setSubject("Krisefikser - 2FA Verification Code / Verifiseringskode");

      String userName = (user.getName() != null ? user.getName() : "Bruker/User");

      String emailBody = """
          Hei %s,
          
          Din 2FA-verifiseringskode er: %s
          
          Vennligst skriv inn denne koden for å fullføre innloggingen din.
          
          Med vennlig hilsen,
          Krisefikser-teamet
          
          ----------------------------------------
          
          Hello %s,
          
          Your 2FA verification code is: %s
          
          Please enter this code to complete your login.
          
          Regards,
          The Krisefikser Team
          """.formatted(userName, code, userName, code);

      message.setText(emailBody);

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
      helper.setSubject("Krisefikser.no - Tilbakestill passord / Reset Your Password");

      String userName = (user.getName() != null ? user.getName() : "Bruker/User");

      // Construct the reset password URL
      String resetPasswordUrl = "http://localhost:5173/reset-password/" + token;

      // Bilingual HTML Email Body
      String emailBody = """
    <html>
    <body>
        <p>Hei %s,</p>
        <p>Vi har mottatt en forespørsel om å tilbakestille passordet ditt.</p>
        <p>Benytt denne koden og lenken for å tilbakestille passordet ditt:</p>
        <p><strong>Kode:</strong> %s</p
        <p>Vi anbefaler å kopiere koden</p>
        <p>Koden er gyldig i 10 minutter.</p>
        <p><strong>Lenke:</strong> <a href="%s" title="Tilbakestill passord">Klikk her for å tilbakestille passordet ditt</a></p>
        <p>Hvis du ikke ba om å tilbakestille passordet ditt, vennligst se bort fra denne e-posten.</p>
        <p>Med vennlig hilsen,<br>Krisefikser-teamet</p>
        <hr>
        <p>Hello %s,</p>
        <p>We have received a request to reset your password.</p>
        <p>Please use the code and link below to set a new password:</p>
        <p><strong>Code:</strong> %s</p>
        <p>We recommend copying the code</p>
        <p>The code is valid for 10 minutes.</p>
        <p><strong>Link:</strong> <a href="%s" title="Reset your password">Click here to reset your password</a></p>
        <p>If you did not request a password reset, please ignore this email.</p>
        <p>Regards,<br>The Krisefikser Team</p>
    </body>
    </html>
    """.formatted(userName, token, resetPasswordUrl, userName, token, resetPasswordUrl);

      helper.setText(emailBody, true); // Set 'true' to indicate HTML content

      mailSender.send(mimeMessage);
      log.info("Password reset email sent successfully to: {}", user.getEmail());

    } catch (MailException e) {
      log.error("Mail sending error for password reset email to {}", user.getEmail());
    } catch (Exception e) {
      log.error("Unexpected error sending password reset email to {}: {}", user.getEmail(), e.getMessage());
    }
  }

  /**
   * Sends safety confirmation emails to all other members of a user's household.
   *
   * @param user  The User object representing the person requesting safety confirmation
   * @param token The unique token string to include in the confirmation link
   */
  public void sendSafetyConfirmationEmail(User user, String token) {
    if (user == null || user.getEmail() == null || token == null) {
      log.error("Cannot send safety confirmation email. User or token is null or user email is null.");
      return;
    }

    Household household = user.getHousehold();
    if (household == null) {
      log.error("Cannot send safety confirmation email. User {} does not belong to a household.", user.getEmail());
      return;
    }

    List<User> householdMembers = userRepository.findByHousehold(household);
    String requestingUserName = (user.getName() != null ? user.getName() : "et husstandsmedlem");

    for (User member : householdMembers) {
      // Skip sending email to the requesting user
      if (member.getEmail().equals(user.getEmail())) {
        continue;
      }

      try {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom(senderEmail);
        helper.setTo(member.getEmail());
        helper.setSubject("Krisefikser.no - Bekreft din sikkerhetsstatus");

        String memberName = (member.getName() != null ? member.getName() : "Bruker");
        String confirmationUrl = "http://localhost:8080/api/user/confirm-safety/" + token;

        String emailBody = """
            <html>
            <body>
                <p>Hei %s,</p>
                
                <p>%s fra din husstand har bedt om en bekreftelse på at du er trygg.</p>
                
                <p>Vennligst klikk på lenken under for å bekrefte at du er i sikkerhet:</p>
                
                <p><a href="%s">Klikk her for å bekrefte at du er trygg</a></p>
                
                <p>Hvis du ikke kan klikke på lenken, kan du kopiere og lime inn denne adressen i nettleseren din:</p>
                <p>%s</p>
                
                <p>Hvis du ikke er i stand til å bekrefte din sikkerhet, vennligst kontakt nødetatene umiddelbart.</p>
                
                <p>Med vennlig hilsen,<br>
                Krisefikser-teamet</p>
            </body>
            </html>
            """.formatted(memberName, requestingUserName, confirmationUrl, confirmationUrl);

        helper.setText(emailBody, true); // Set 'true' to indicate HTML content

        mailSender.send(mimeMessage);
        log.info("Safety confirmation email sent successfully to: {}", member.getEmail());

      } catch (MailException e) {
        log.error("Mail sending error for safety confirmation email to {}", member.getEmail());
      } catch (Exception e) {
        log.error("Unexpected error sending safety confirmation email to {}: {}", member.getEmail(), e.getMessage());
      }
    }
  }
}
