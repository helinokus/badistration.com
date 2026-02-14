package com.helinok.pzbad_registration.Controllers.Auth;

import com.helinok.pzbad_registration.Dtos.PasswordResetVerifyDto;
import com.helinok.pzbad_registration.Services.PasswordResetService.IPasswordResetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest {
    @Mock
    IPasswordResetService passwordResetService;

    @InjectMocks
    ResetPasswordController resetPasswordController;

    @Test
    void resetPasswordTest() {
        // Arrange
        var email = "test@test.com";
        var request = new com.helinok.pzbad_registration.Dtos.PasswordResetRequestDto();
        request.setEmail(email);

        // mock service
        doNothing().when(passwordResetService).requestPasswordReset(email);

        // Act
        var response = resetPasswordController.resetPassword(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Password reset successful", response.getBody().getMessage());

        verify(passwordResetService).requestPasswordReset(email);
    }

    @Test
    void resetPasswordTest_UserNotFound_Returns404() {
        // Arrange
        var email = "nonexistent@test.com";
        var request = new com.helinok.pzbad_registration.Dtos.PasswordResetRequestDto();
        request.setEmail(email);

        // Mock service
        doThrow(new com.helinok.pzbad_registration.Exceptions.NotFoundException("User not found"))
                .when(passwordResetService).requestPasswordReset(email);

        // Act
        var response = resetPasswordController.resetPassword(request);

        // Assert
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());

        verify(passwordResetService).requestPasswordReset(email);
    }
}