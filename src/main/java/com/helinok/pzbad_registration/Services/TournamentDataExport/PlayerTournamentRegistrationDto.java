package com.helinok.pzbad_registration.Services.TournamentDataExport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class PlayerTournamentRegistrationDto {
    private Long playerId;
    private String fullName;
    private String email;
    private List<String> categories;
    private BigDecimal amount;
}
