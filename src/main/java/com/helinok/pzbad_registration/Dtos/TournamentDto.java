package com.helinok.pzbad_registration.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentDto {
    private Long id;
    private String nameTournament;
    private LocalDateTime dateOfTournament;
    private LocalDateTime registrationExpired;
    private String urlTournament;
    private Set<GameCategories> categories;
    private String location;
    private String description;
    private Integer maxPlayers;
    private Integer currentPlayers;
    private String entryFee;
    private String creatorName;
    private String creatorEmail;
    @JsonProperty("isClosed")
    private boolean isClosed;
    private Map<GameCategories, BigDecimal> categoryPrices;
    private Integer maxCategoriesToPlay;
}