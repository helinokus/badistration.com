package com.helinok.pzbad_registration.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDto {
    private String email;
}
