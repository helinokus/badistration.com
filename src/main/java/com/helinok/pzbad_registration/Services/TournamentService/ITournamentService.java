package com.helinok.pzbad_registration.Services.TournamentService;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;

import java.util.List;

public interface ITournamentService {
    Tournament findEntityById(Long id);

    List<TournamentDto> getAllTournaments();
    List<TournamentDto> getUpcomingTournaments();
    List<Tournament> getTournamentsByOrganizer(User organizer);
    TournamentDto createTournament(TournamentDto dto, User creator);
    TournamentDto updateTournament(Long id, TournamentDto dto, String auth);
    boolean isRegistrationOpen(Tournament tournament);
    List<Tournament> searchTournaments(String query);
    List<TournamentDto> filterByCategories(List<String> categories, int daysFrom, int daysTo);

    List<Tournament> getAllAdminTournaments();
    List<Tournament> getTournamentsToClose();

}
