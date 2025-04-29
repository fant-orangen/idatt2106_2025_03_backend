package stud.ntnu.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.user.TwoFactorCode;
import stud.ntnu.backend.repository.user.TwoFactorCodeRepository;

@Service
public class TwoFactorCodeService {
    private final EmailService emailService;
    private final TwoFactorCodeRepository twoFactorCodeRepository;

    public TwoFactorCodeService(TwoFactorCodeRepository twoFactorCodeRepository, EmailService emailService) {
        this.twoFactorCodeRepository = twoFactorCodeRepository;
        this.emailService = emailService;
    }

    public Integer generateVerificationCode() {
        return 100000 + new Random().nextInt(900000); // Ensures a 6-digit integer
    }

    public void sendVerificationCode(String email) {
        Integer code = generateVerificationCode();
        saveCode(email, code);
        emailService.send2FAEmail(email, code);
    }

    public void saveCode(String email, Integer code) {
        TwoFactorCode twoFactorCode = new TwoFactorCode();
        twoFactorCode.setEmail(email);
        twoFactorCode.setCode(code);
        twoFactorCode.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // Code expires in 5 minutes
        twoFactorCodeRepository.save(twoFactorCode);
    }

    public boolean verifyCode(String email, Integer code) {
        Optional<TwoFactorCode> optionalCode = twoFactorCodeRepository.findByEmail(email);
        if (optionalCode.isEmpty()) {
            return false;
        }

        TwoFactorCode twoFactorCode = optionalCode.get();
        if (twoFactorCode.getExpiresAt().isBefore(LocalDateTime.now()) || !twoFactorCode.getCode().equals(code)) {
            return false;
        }

        // Delete the code after successful verification
        twoFactorCodeRepository.deleteByEmail(email);
        return true;
    }
}
