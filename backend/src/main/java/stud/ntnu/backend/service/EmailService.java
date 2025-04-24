package stud.ntnu.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.User;

@Service
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender mailSender;
  private final String senderEmail;

  // Constructor injection
  @Autowired
  public EmailService(JavaMailSender mailSender,
      @Value("${spring.mail.username}") String senderEmail) {
    this.mailSender = mailSender;
    this.senderEmail = senderEmail;
  }

  public void sendVerificationEmail(User user, String token) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(senderEmail);
      message.setTo(user.getEmail());
      message.setSubject("Krisefikser.no - Vennligst bekreft e-posten din / Please Verify Your Email");

      String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
      String userName = (user.getName() != null ? user.getName() : "Bruker/User");

      // Bilingual Email Body
      String emailBody = "Hei " + userName + ",\n\n"
          + "Takk for at du registrerte deg hos Krisefikser.no.\n"
          + "Vennligst klikk på lenken under for å bekrefte e-postadressen din:\n\n"
          + verificationUrl + "\n\n"
          + "Hvis du ikke registrerte deg, vennligst se bort fra denne e-posten.\n\n"
          + "Med vennlig hilsen,\nKrisefikser-teamet\n\n"
          + "----------------------------------------\n\n"
          + "Hello " + userName + ",\n\n"
          + "Thank you for registering with Krisefikser.no.\n"
          + "Please click the link below to verify your email address:\n\n"
          + verificationUrl + "\n\n"
          + "If you did not register, please ignore this email.\n\n"
          + "Regards,\nThe Krisefikser Team";

      message.setText(emailBody);
      mailSender.send(message);
      log.info("Verification email sent successfully to: {}", user.getEmail());
    } catch (Exception e) {
      log.error("Error sending verification email to {}: {}", user.getEmail(), e.getMessage(), e);
    }
  }
}