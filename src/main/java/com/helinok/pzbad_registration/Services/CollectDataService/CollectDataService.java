package com.helinok.pzbad_registration.Services.CollectDataService;

import com.helinok.pzbad_registration.Dtos.ExportDataDto;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.UserTournament;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectDataService implements ICollectDataService{

    private final IUserTournamentService userTournamentService;
    private final ITournamentService tournamentService;


    @Override
    public ExportDataDto getExportData(Long tournamentId) {
        Tournament tournament = tournamentService.findEntityById(tournamentId);
        List<UserTournament> registrations = userTournamentService.getTournamentRegistrations(tournament);

        List<UserTournament> approvedRegistrations = registrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.APPROVED && r.isPaid())
                .toList();

        return ExportDataDto.builder()
                .tournament(ExportDataDto.TournamentData.builder()
                        .name(tournament.getNameTournament())
                        .date(tournament.getDateOfTournament())
                        .location(tournament.getLocation())
                        .categories(tournament.getCategories())
                        .build())
                .players(approvedRegistrations.stream()
                        .map(reg -> ExportDataDto.PlayerData.builder()
                                .firstName(reg.getUser().getFirstName())
                                .lastName(reg.getUser().getLastName())
                                .email(reg.getUser().getEmail())
                                .phone(reg.getUser().getPhoneNumber())
                                .club(reg.getUser().getTeamName())
                                .birthDate(reg.getUser().getBirthDate())
                                .categories(reg.getGameCategories() != null ? reg.getGameCategories() : null)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
