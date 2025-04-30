package stud.ntnu.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;

/**
 * Service responsible for handling email sending operations, such as verification emails.
 * Uses Spring's JavaMailSender for dispatching emails.
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
     * @param senderEmail The sender's email address, injected from application properties (spring.mail.username).
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
     * Sends a verification email to the specified user.
     * The email contains a unique token within a verification link.
     * Includes content in both Norwegian and English.
     * Logs success or errors during the sending process.
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
            log.error("Mail sending error for verification email to {}: {}", user.getEmail(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending verification email to {}: {}", user.getEmail(), e);
        }
    }

    /**
     * Sends a 2FA email to the specified user.
     *
     * @param email The email address of the user to send the 2FA code to.
     * @param code The unique 2FA token string to include in the email.
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
            log.error("Mail sending error for verification email to {}: {}", user.getEmail(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending 2FA email to {}: {}", user.getEmail(), e);
        }
    }

    /**
     * Sends a password reset email to the specified user.
     *
     * @param user The User object representing the recipient. Must have a valid email address.
     * @param token The unique reset token string to include in the link.
     */
    public void sendPasswordResetEmail(User user, String token) {
        if (user == null || user.getEmail() == null || token == null) {
            log.error("Cannot send password reset email. User or token is null or user email is null.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(user.getEmail());
            message.setSubject("Krisefikser.no - Tilbakestill passord / Reset Your Password");

            String userName = (user.getName() != null ? user.getName() : "Bruker/User");

            // Bilingual Email Body using Text Block and .formatted()
            String emailBody = """
                Hei %s,

                Vi har mottatt en forespørsel om å tilbakestille passordet ditt.
                
                Vennligst benytt denne koden for å sette et nytt passord: %s

                Hvis du ikke ba om å tilbakestille passordet ditt, vennligst se bort fra denne e-posten.

                Med vennlig hilsen,
                Krisefikser-teamet

                ----------------------------------------

                Hello %s,

                We have received a request to reset your password.
                
                Please use this code to set a new password: %s

                If you did not request a password reset, please ignore this email.

                Regards,
                The Krisefikser Team
                """.formatted(userName, token, userName, token);

            message.setText(emailBody);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", user.getEmail());

        } catch (MailException e) {
            log.error("Mail sending error for password reset email to {}: {}", user.getEmail(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending password reset email to {}: {}", user.getEmail(), e);
        }
    }
}
