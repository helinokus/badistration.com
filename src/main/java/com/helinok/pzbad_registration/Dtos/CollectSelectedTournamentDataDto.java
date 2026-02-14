package com.helinok.pzbad_registration.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CollectSelectedTournamentDataDto {
    private Long tournamentId;
    private String notes;
    private List<Long> ids;
}
