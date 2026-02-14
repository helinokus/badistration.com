package com.helinok.pzbad_registration.Controllers.Auth;

import com.helinok.pzbad_registration.Dtos.PasswordResetCompleteDto;
import com.helinok.pzbad_registration.Dtos.PasswordResetRequestDto;
import com.helinok.pzbad_registration.Dtos.PasswordResetVerifyDto;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.PasswordResetService.IPasswordResetService;
import com.helinok.pzbad_registration.Services.Security.JwtService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final IPasswordResetService passwordResetService;

    @PostMapping("/reset-password/request")
    @Operation(summary = "Reset password request")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetRequestDto request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(new ApiResponse("Password reset successful"));
        }catch (ConflictException e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage()));
        }
    }
    @PostMapping("/reset-password/verify")
    @Operation(summary = "verify the reset password request")
    public ResponseEntity<ApiResponse> verifyPassword(@RequestBody PasswordResetVerifyDto request) {
        try {
            passwordResetService.verifyResetCode(request.getEmail(), request.getCode());
            return ResponseEntity.ok(new ApiResponse("Password verification successful"));
        }catch (ConflictException e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password/complete")
    @Operation(summary = "Reset password to the new one")
    public ResponseEntity<ApiResponse> completePassword(@RequestBody PasswordResetCompleteDto request) {
        try {
            passwordResetService.completePasswordReset(request.getEmail(), request.getCode(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Password complete successful"));
        }catch (ConflictException e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage()));
        }
    }
}
