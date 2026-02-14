package com.helinok.pzbad_registration.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class CollectRangedTournamentDataDto {
    private Long tournamentId;
    private String notes;
    private String from;
    private String to;

}
