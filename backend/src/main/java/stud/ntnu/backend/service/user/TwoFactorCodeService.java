package stud.ntnu.backend.service.user;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.model.user.TwoFactorCode;
import stud.ntnu.backend.repository.user.TwoFactorCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

/**
 * <h2>TwoFactorCodeService</h2>
 *
 * <p>Service for managing two-factor authentication codes. This service handles the generation,
 * sending, and verification of two-factor codes for users. It also manages the expiration of these
 * codes.</p>
 */
@Service
@RequiredArgsConstructor
public class TwoFactorCodeService {

    @Value("${twofactor.code.expiration.minutes}")
    private int codeExpirationMinutes;
    private final EmailService emailService;
    private final TwoFactorCodeRepository twoFactorCodeRepository;

    /**
     * Generates a random 6-digit verification code.
     *
     * @return A random 6-digit integer.
     */
    public Integer generateVerificationCode() {
        int code = 100000 + new Random().nextInt(900000);
        System.out.println("Generated verification code: " + code);
        return code;
    }

    /**
     * Sends a verification code to the specified email address. If a code already exists for the
     * email, it is deleted before sending a new one.
     *
     * @param email The email address to send the verification code to.
     */
    @Transactional
    public void sendVerificationCode(String email) throws MessagingException {
        twoFactorCodeRepository.deleteByEmail(email);
        Integer code = generateVerificationCode();
        saveCode(email, code);
        emailService.send2FAEmail(email, code);
    }

    /**
     * Saves the verification code and its expiration time to the database.
     *
     * @param email The email address associated with the verification code.
     * @param code  The verification code to save.
     */
    public void saveCode(String email, Integer code) {
        TwoFactorCode twoFactorCode = new TwoFactorCode();
        twoFactorCode.setEmail(email);
        twoFactorCode.setCode(code);

        twoFactorCode.setExpiresAt(LocalDateTime.now().plusMinutes(codeExpirationMinutes));
        twoFactorCodeRepository.save(twoFactorCode);
    }

    /**
     * Verifies the provided code against the one stored in the database for the given email. If the
     * code is valid and not expired, it deletes the code from the database.
     *
     * @param email The email address associated with the verification code.
     * @param code  The verification code to verify.
     * @return true if the code is valid and not expired, false otherwise.
     */
    @Transactional
    public boolean verifyCode(String email, Integer code) {
        Optional<TwoFactorCode> optionalCode = twoFactorCodeRepository.findByEmail(email);
        if (optionalCode.isEmpty()) {
            return false;
        }

        TwoFactorCode twoFactorCode = optionalCode.get();
        if (twoFactorCode.getExpiresAt().isBefore(LocalDateTime.now()) || !twoFactorCode.getCode()
            .equals(code)) {
            return false;
        }

        // Delete the code after successful verification
        twoFactorCodeRepository.deleteByEmail(email);
        return true;
    }
}
