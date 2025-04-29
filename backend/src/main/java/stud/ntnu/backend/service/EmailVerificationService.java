package stud.ntnu.backend.service;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.user.User;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailService emailService;

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void sendVerificationCode(User user) {
        String code = generateVerificationCode();
        emailService.send2FAEmail(user, code);
    }

    public boolean verifyCode(String userEmail, String userEnteredCode, String sentCode) {
        return sentCode.equals(userEnteredCode);
    }
}