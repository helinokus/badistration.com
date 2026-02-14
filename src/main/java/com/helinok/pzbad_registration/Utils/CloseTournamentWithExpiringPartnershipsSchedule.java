package com.helinok.pzbad_registration.Utils;


import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Repositories.TournamentRepository;
import com.helinok.pzbad_registration.Services.PartnershipService.IPartnershipService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CloseTournamentWithExpiringPartnershipsSchedule {
    private final IPartnershipService partnershipService;
    private final ITournamentService tournamentService;
    private final TournamentRepository tournamentRepository;

    @Scheduled(cron = "0 */30 * * * *", zone = "Europe/Warsaw")
    @Transactional
    public void closeTournamentWithExpiringPartnerships() {
        log.info("Starting closing tournament with expiring partnerships");
        List<Tournament> tournamentsToClose = tournamentService.getTournamentsToClose();
        if (tournamentsToClose == null || tournamentsToClose.isEmpty()) {
            log.debug("No tournament to close");
            return;
        }
        log.debug("Found {} tournament to close", tournamentsToClose.size());

        tournamentsToClose.forEach(tournament -> {
            log.info("Closing tournament: {} (ID: {})", tournament.getNameTournament(), tournament.getId());

            tournament.setClosed(true);
            tournamentRepository.save(tournament);

            int i = partnershipService.expireAllPendingPartnershipsByTournament(tournament);
            log.info("Expired {} partnership(s) for tournament {}", i, tournament.getId());
        });

        log.debug("Finished closing tournament with expiring partnerships");
    }
}
