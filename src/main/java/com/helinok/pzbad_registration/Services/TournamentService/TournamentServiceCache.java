package com.helinok.pzbad_registration.Services.TournamentService;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.TournamentCategoryPrice;
import com.helinok.pzbad_registration.Repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TournamentServiceCache implements ITournamentServiceCache{


    private final TournamentRepository tournamentRepository;

    @Cacheable(value = "tournaments:listAll", key = "'all-tournaments'")
    @Override
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Cacheable(value = "tournaments:listAllUpcoming", key = "'all-tournaments-upcoming'")
    @Override
    public List<TournamentDto> getUpcomingTournaments() {
        LocalDateTime now = LocalDateTime.now();
        return tournamentRepository.findAll().stream()
                .filter(t -> t.getDateOfTournament().isAfter(now))
                .sorted((t1, t2) -> t1.getDateOfTournament().compareTo(t2.getDateOfTournament()))
                .map(this::convertToDto)
                .toList();
    }

    @Cacheable(value = "tournaments:dto", key = "#id")
    @Override
    public TournamentDto getTournamentById(Long id){
        return tournamentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Tournament not found with id: " + id));
    }



    @CacheEvict(value = "tournaments:listAll", allEntries = true)
    @Override
    public void evictAllTournaments(){

    }

    @CacheEvict(value = "tournaments:listAllUpcoming", allEntries = true)
    @Override
    public void evictAllUpcomingTournaments(){

    }


    @CacheEvict(value = "tournaments:dto", key = "#id")
    @Override
    public void evictTournamentsDto(Long id){

    }






    private boolean isRegistrationOpen(Tournament tournament) {
        return LocalDateTime.now().isBefore(tournament.getRegistrationExpired()) && !tournament.isClosed();
    }

    @Override
    public TournamentDto convertToDto(Tournament tournament) {
        int currentPlayers;
        Set<Long> playersIds = new HashSet<>();
        if (tournament.getUserTournaments() != null) {
            tournament.getUserTournaments().stream()
                    .filter(userTournament -> !userTournament.getGameCategories().isEmpty())
                    .forEach(userTournament -> {
                        playersIds.add(userTournament.getUser().getId());
                    });
        }
        if (tournament.getPartnerships() != null) {
            tournament.getPartnerships()
                    .stream()
                    .filter(e -> e.getStatus() == PartnershipStatus.CONFIRMED)
                    .forEach(partnership -> {
                        playersIds.add(partnership.getPlayer1().getId());
                        playersIds.add(partnership.getPlayer2().getId());
                    });
        }
        log.info("playerIds {}", playersIds);
        currentPlayers = playersIds.size();


        Map<GameCategories, BigDecimal> prices = new HashMap<>();
        if (tournament.getCategoryPrices() != null) {
            prices = tournament.getCategoryPrices().stream()
                    .collect(Collectors.toMap(
                            TournamentCategoryPrice::getCategory,
                            TournamentCategoryPrice::getPrice
                    ));
        }

        log.info(prices.toString());

        log.info("is OPEN {}", isRegistrationOpen(tournament));

        return TournamentDto.builder()
                .id(tournament.getId())
                .nameTournament(tournament.getNameTournament())
                .dateOfTournament(tournament.getDateOfTournament())
                .registrationExpired(tournament.getRegistrationExpired())
                .urlTournament(tournament.getUrlTournament())
                .categories(tournament.getCategories())
                .location(tournament.getLocation())
                .description(tournament.getDescription())
                .maxPlayers(tournament.getMaxPlayers())
                .currentPlayers(currentPlayers)
                .entryFee(tournament.getEntryFee())
                .creatorName(tournament.getCreatorOfTournament() != null ?
                        tournament.getCreatorOfTournament().getFirstName() + " " +
                                tournament.getCreatorOfTournament().getLastName() : null)
                .isClosed(!isRegistrationOpen(tournament))
                .categoryPrices(prices)
                .creatorEmail(tournament.getCreatorOfTournament() != null ?
                        tournament.getCreatorOfTournament().getEmail() : null)
                .maxCategoriesToPlay(tournament.getMaxCategoriesToPlay())
                .build();
    }
}
