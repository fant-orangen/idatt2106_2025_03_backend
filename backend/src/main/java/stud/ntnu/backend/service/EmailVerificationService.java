package stud.ntnu.backend.service;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.user.User;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailService emailService;

    public EmailVerificationService(EmailService emailService) {
        this.emailService = emailService;
    }


    public Integer generateVerificationCode() {
        return 100000 + new Random().nextInt(900000); // Ensures a 6-digit integer
    }

    public void sendVerificationCode(User user) {
        Integer code = generateVerificationCode();
        emailService.send2FAEmail(user, code);
    }

    public boolean verifyCode(Integer userEnteredCode, Integer sentCode) {
        return sentCode.equals(userEnteredCode);
    }
}