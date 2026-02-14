package com.helinok.pzbad_registration.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetCompleteDto {
    private String email;
    private String code;
    private String newPassword;
}
