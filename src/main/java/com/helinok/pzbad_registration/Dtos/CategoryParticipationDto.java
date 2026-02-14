package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryParticipationDto {
    private GameCategories category;
    private boolean isDoubles;  // true если это парная категория
    private PartnerInfoDto partner; // null если играет индивидуально
}