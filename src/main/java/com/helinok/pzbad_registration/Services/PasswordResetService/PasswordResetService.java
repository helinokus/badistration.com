package com.helinok.pzbad_registration.Services.PasswordResetService;

import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.PasswordResetToken;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Repositories.PasswordResetTokenRepository;
import com.helinok.pzbad_registration.Repositories.UserRepository;
import com.helinok.pzbad_registration.Services.MailSenderService.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordResetService implements IPasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        cleanupExpiredTokensForUser(email);
        Optional<PasswordResetToken> byEmailAndUsedFalse = passwordResetTokenRepository.findByEmailAndUsedFalse(email);
        if (byEmailAndUsedFalse.isPresent()) {
            PasswordResetToken existingToken = byEmailAndUsedFalse.get();
            if (existingToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                throw new ConflictException("You already have a password reset token for this email, " +
                    "wait 15 minutes and try again or use last sent code");
            }
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        passwordResetToken.setEmail(email);
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setUser(user);
        passwordResetToken.setCode(generateSecureCode());
        mailSenderService.sendMail(email, "Password Reset Token", generatePasswordResetPlainText(email, passwordResetToken.getCode()));
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public String generatePasswordResetPlainText(String recipientName, String resetCode) {
        return """
            Badistration - Resetowanie hasła
            
            Cześć %s,
            
            Otrzymaliśmy prośbę o zresetowanie hasła do Twojego konta.
            
            Twój kod weryfikacyjny: %s
            
            Aby zresetować hasło:
            1. Wróć do strony resetowania hasła
            2. Wprowadź swój adres email
            3. Wpisz powyższy kod weryfikacyjny
            4. Ustaw nowe hasło
            
            Jeśli nie prosiłeś o resetowanie hasła, zignoruj tę wiadomość.
            
            Potrzebujesz pomocy? Skontaktuj się z nami: admin@badistration.com
            
            ---
            """.formatted(recipientName, resetCode);
    }

    @Override
    public boolean verifyResetCode(String email, String code) {
        cleanupExpiredTokensForUser(email);

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByEmailAndCodeAndUsedFalse(email, code);

        if (tokenOpt.isEmpty()) {
            PasswordResetToken existingToken = passwordResetTokenRepository.findByEmailAndUsedFalse(email).orElse(null);
            if (existingToken != null) {
                if (existingToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                    passwordResetTokenRepository.delete(existingToken);
                    throw new ConflictException("Password reset code has expired. Please request a new one.");
                }

                if (existingToken.getAttempts() >= 5) {
                    throw new ConflictException("You already used all tries for reset password. Wait 15 minutes and try to reset one more time");
                }

                existingToken.setAttempts(existingToken.getAttempts() + 1);
                passwordResetTokenRepository.save(existingToken);
                throw new ConflictException("You entered the wrong code. Attempts left: " + (5 - existingToken.getAttempts()));
            }
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(token);
            throw new ConflictException("Password reset code has expired. Please request a new one.");
        }

        return true;
    }

    @Override
    @Transactional
    public void completePasswordReset(String email, String code, String newPassword) {
        cleanupExpiredTokensForUser(email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        if (verifyResetCode(email, code)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            log.info("New raw password : " + newPassword);
            log.info("New encoded password : " + user.getPassword());
            userRepository.save(user);

            PasswordResetToken token = passwordResetTokenRepository
                    .findByEmailAndCodeAndUsedFalse(email, code)
                    .orElseThrow(() -> new ConflictException("Invalid reset code"));

            token.setUsed(true);
            passwordResetTokenRepository.save(token);
        } else {
            throw new ConflictException("Invalid or expired password reset code");
        }
    }

    @Override
    public String generateSecureCode() {
        return String.valueOf((int)(Math.random()*900000+100000));
    }

    private void cleanupExpiredTokensForUser(String email) {
        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = passwordResetTokenRepository.deleteExpiredTokensForEmail(email, now);
            if (deletedCount > 0) {
                log.debug("Cleaned up {} expired tokens for user: {}", deletedCount, email);
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup expired tokens for user: {}", email, e);
        }
    }

    public int cleanupAllExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = passwordResetTokenRepository.deleteExpiredAndUsedTokens(now);
            if (deletedCount > 0) {
                log.info("Cleaned up {} expired password reset tokens", deletedCount);
            }
            return deletedCount;
        } catch (Exception e) {
            log.error("Error during password reset tokens cleanup", e);
            return 0;
        }
    }
}
