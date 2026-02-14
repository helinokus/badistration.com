package com.helinok.pzbad_registration.Utils;

import com.helinok.pzbad_registration.Services.PasswordResetService.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetCleanupTask {

    private final PasswordResetService passwordResetService;

    @Scheduled(cron = "0 */30 * * * *", zone = "Europe/Warsaw")
    public void cleanupExpiredTokens() {
        log.debug("Starting scheduled cleanup of expired password reset tokens");
        int deletedCount = passwordResetService.cleanupAllExpiredTokens();
        if (deletedCount > 0) {
            log.info("Scheduled cleanup completed: {} tokens removed", deletedCount);
        }
    }
}