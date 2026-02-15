package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantCardDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String teamName;

    private List<CategoryParticipationDto> categoryParticipations;

    private RegistrationStatus status;
    private boolean isPaid;
    private LocalDateTime registrationTime;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
