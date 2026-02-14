package com.helinok.pzbad_registration.Dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersAdminDto {
    private Long id;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String teamName;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd-HH:mm")
    private LocalDateTime registrationTime;
    List<String> roles;
    private boolean status;
}
