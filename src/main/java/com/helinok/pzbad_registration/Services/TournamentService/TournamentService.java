package com.helinok.pzbad_registration.Services.TournamentService;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.*;
import com.helinok.pzbad_registration.Repositories.TournamentCategoryPriceRepository;
import com.helinok.pzbad_registration.Repositories.TournamentRepository;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TournamentService implements ITournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentCategoryPriceRepository categoryPriceRepository;
    private final IUserService userService;
    private final ITournamentServiceCache tournamentServiceCache;

    @Override
    public Tournament findEntityById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tournament not found with id: " + id));
    }

    @Override
    public List<TournamentDto> getAllTournaments() {
        return tournamentServiceCache.getAllTournaments();
    }

    @Override
    public List<TournamentDto> getUpcomingTournaments() {
        return tournamentServiceCache.getUpcomingTournaments();
    }

    @Override
    public List<Tournament> getTournamentsByOrganizer(User organizer) {
        return tournamentRepository.findAll().stream()
                .filter(t -> t.getCreatorOfTournament() != null &&
                        t.getCreatorOfTournament().equals(organizer))
                .collect(Collectors.toList());
    }

    @Override
    public TournamentDto createTournament(TournamentDto dto, User creator) {
        Tournament tournament = new Tournament();
        tournament.setNameTournament(dto.getNameTournament());
        tournament.setDateOfTournament(dto.getDateOfTournament());
        tournament.setRegistrationExpired(dto.getRegistrationExpired());
        tournament.setUrlTournament(dto.getUrlTournament());
        tournament.setCategories(dto.getCategories());
        tournament.setLocation(dto.getLocation());
        tournament.setDescription(dto.getDescription());
        tournament.setMaxPlayers(dto.getMaxPlayers());
        tournament.setEntryFee(dto.getEntryFee());
        tournament.setCreatorOfTournament(creator);
        tournament.setMaxCategoriesToPlay(dto.getMaxCategoriesToPlay());

        Tournament savedTournament = tournamentRepository.save(tournament);

        if (dto.getCategoryPrices() != null && !dto.getCategoryPrices().isEmpty()) {
            List<TournamentCategoryPrice> categoryPrices = new ArrayList<>();

            for (Map.Entry<GameCategories, BigDecimal> entry : dto.getCategoryPrices().entrySet()) {
                TournamentCategoryPrice categoryPrice = new TournamentCategoryPrice();
                categoryPrice.setTournament(savedTournament);
                categoryPrice.setCategory(entry.getKey());
                categoryPrice.setPrice(entry.getValue());
                categoryPrices.add(categoryPrice);
            }

            categoryPrices = categoryPriceRepository.saveAll(categoryPrices);
            savedTournament.setCategoryPrices(categoryPrices);
        }

        tournamentServiceCache.evictAllTournaments();
        tournamentServiceCache.evictAllUpcomingTournaments();
        tournamentServiceCache.getAllTournaments();
        tournamentServiceCache.getUpcomingTournaments();

        return tournamentServiceCache.convertToDto(savedTournament);
    }

    @Override
    public TournamentDto updateTournament(Long id, TournamentDto dto, String auth) {

        Tournament tournament = findEntityById(id);
        User user = userService.findUserEntityByEmail(auth);

        boolean isOrganizer = tournament.getCreatorOfTournament() != null &&
                tournament.getCreatorOfTournament().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isOrganizer && !isAdmin) {
            throw new ConflictException("You are not allowed to update the tournament");
        }

        tournament.setNameTournament(dto.getNameTournament());
        tournament.setDateOfTournament(dto.getDateOfTournament());
        tournament.setRegistrationExpired(dto.getRegistrationExpired());
        tournament.setUrlTournament(dto.getUrlTournament());
        tournament.setCategories(dto.getCategories());
        tournament.setLocation(dto.getLocation());
        tournament.setDescription(dto.getDescription());
        tournament.setMaxPlayers(dto.getMaxPlayers());
        tournament.setEntryFee(dto.getEntryFee());
        tournament.setMaxCategoriesToPlay(dto.getMaxCategoriesToPlay());

        log.info("setted in service");

        if (dto.getCategoryPrices() != null) {
            tournament.getCategoryPrices().clear();

            dto.getCategoryPrices().forEach((category, price) -> {
                TournamentCategoryPrice categoryPrice = new TournamentCategoryPrice();
                categoryPrice.setTournament(tournament);
                categoryPrice.setCategory(category);
                categoryPrice.setPrice(price);
                tournament.getCategoryPrices().add(categoryPrice);
            });
        }
        log.info("updated service at all");

        Tournament save = tournamentRepository.save(tournament);

        tournamentServiceCache.evictAllTournaments();
        tournamentServiceCache.evictAllUpcomingTournaments();
        tournamentServiceCache.evictTournamentsDto(save.getId());
        tournamentServiceCache.getAllTournaments();
        tournamentServiceCache.getUpcomingTournaments();
        tournamentServiceCache.getTournamentById(save.getId());

        return tournamentServiceCache.convertToDto(save);
    }

    @Override
    public boolean isRegistrationOpen(Tournament tournament) {
        return LocalDateTime.now().isBefore(tournament.getRegistrationExpired()) && !tournament.isClosed();
    }

    @Override
    public List<Tournament> searchTournaments(String query) {
        return tournamentRepository.findAll().stream()
                .filter(t -> t.getNameTournament().toLowerCase().contains(query.toLowerCase()) ||
                        (t.getLocation() != null && t.getLocation().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
    }


    @Override
    public List<TournamentDto> filterByCategories(List<String> categories, int daysFrom, int daysTo) {

        List<Tournament> allTournaments = tournamentRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateFrom = now.plusDays(daysFrom);
        LocalDateTime dateTo = now.plusDays(daysTo);

       return allTournaments.stream().filter(e ->  { //Date of tournament
            LocalDateTime date = e.getDateOfTournament();
            return date.isAfter(dateFrom) && date.isBefore(dateTo);
        }).filter(e -> {
            if (categories == null || categories.isEmpty()) {
                return true;
            }
            Set<String> tournamentCategoriesSet = e.getCategories().stream()
                    .map(GameCategories::getDisplayName)
                    .collect(Collectors.toSet());

            for (String filterCategory : categories) {
                for (String tournamentCategory : tournamentCategoriesSet) {
                    if (tournamentCategory.contains(filterCategory)) {
                        return true;
                    }
                }
            }
            return false;

        })
               .map(tournamentServiceCache::convertToDto)
               .collect(Collectors.toList());

    }

    @Override
    public List<Tournament> getAllAdminTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public List<Tournament> getTournamentsToClose() {
        return tournamentRepository.findTournamentsWhereRegistrationDateExpired(LocalDateTime.now())
                .orElse(Collections.emptyList());
    }

}