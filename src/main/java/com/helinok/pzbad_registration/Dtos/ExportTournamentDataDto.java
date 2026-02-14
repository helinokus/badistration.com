package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExportTournamentDataDto {
    private String firstName;
    private String lastName;
    private GameCategories gameCategory;

    private String partnerFirstName;
    private String partnerLastName;


}
