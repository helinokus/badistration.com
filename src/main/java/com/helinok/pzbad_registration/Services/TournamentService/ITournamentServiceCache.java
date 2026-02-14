package com.helinok.pzbad_registration.Services.TournamentService;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Models.Tournament;

import java.util.List;

public interface ITournamentServiceCache {

    List<TournamentDto> getAllTournaments();

    List<TournamentDto> getUpcomingTournaments();

    TournamentDto getTournamentById(Long id);

    void evictAllTournaments();

    void evictAllUpcomingTournaments();

    void evictTournamentsDto(Long id);

    TournamentDto convertToDto(Tournament tournament);
}
