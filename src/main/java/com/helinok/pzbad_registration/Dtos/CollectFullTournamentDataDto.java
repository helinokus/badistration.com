package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CollectFullTournamentDataDto {
    private Long tournamentId;
    private String notes;
}
