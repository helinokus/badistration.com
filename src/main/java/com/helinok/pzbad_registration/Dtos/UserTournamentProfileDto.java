package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTournamentProfileDto {
    private String userName;
    private String tournamentName;
    private LocalDateTime tournamentDate;
    private Set<GameCategories> categories;
    private String tournamentUrl;
    private Long tournamentId;
    private Long userTournamentId;
}
