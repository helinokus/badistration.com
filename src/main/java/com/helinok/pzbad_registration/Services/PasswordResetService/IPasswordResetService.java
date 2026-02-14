package com.helinok.pzbad_registration.Services.PasswordResetService;

public interface IPasswordResetService {
    void requestPasswordReset(String email);
    boolean verifyResetCode(String email, String code);
    void completePasswordReset(String email, String code, String newPassword);
    String generateSecureCode();
}
