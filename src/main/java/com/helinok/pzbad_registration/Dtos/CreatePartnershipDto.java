package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CreatePartnershipDto {
    private GameCategories category;
    private Long partnerId;
    private Long tournamentId;
}
