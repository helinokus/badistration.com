package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcceptPartnershipDto {
    private Long partnershipId;
    private Long tournamentId;
    private Long initiatorId;
    private GameCategories category;
}
