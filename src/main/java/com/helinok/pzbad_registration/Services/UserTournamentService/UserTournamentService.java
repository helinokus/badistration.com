package com.helinok.pzbad_registration.Services.UserTournamentService;

import com.helinok.pzbad_registration.Dtos.UserTournamentProfileDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.TournamentCategoryPrice;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.UserTournament;
import com.helinok.pzbad_registration.Models.Partnership;
import com.helinok.pzbad_registration.Repositories.*;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserService.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserTournamentService implements IUserTournamentService {

    private final UserTournamentRepository userTournamentRepository;
    private final TournamentCategoryPriceRepository categoryPriceRepository;
    private final UserService userService;
    private final PartnershipRepository partnershipRepository;
    private final ITournamentService tournamentService;

    @Override
    @Transactional
    public UserTournament registerUserForTournament(User user, Tournament tournament, Set<GameCategories> categories) {
        log.info("SERVICE: Step 1 - Starting registration for user {} on tournament {}", user.getId(), tournament.getId());
        log.info("SERVICE: Categories received: {}", categories);

        log.info("SERVICE: Step 2 - Checking registration deadline");
        if (LocalDateTime.now().isAfter(tournament.getRegistrationExpired())) {
            log.error("SERVICE: Registration expired! Current time: {}, Expired: {}", LocalDateTime.now(), tournament.getRegistrationExpired());
            throw new ConflictException("Rejestracja na ten turniej została zamknięta");
        }
        if (!tournamentService.isRegistrationOpen(tournament)) {
            throw new ConflictException("Rejestracja na ten turniej została zamknięta");
        }
        log.info("SERVICE: Registration deadline OK");

        log.info("SERVICE: Step 3 - Checking if user already registered");
        if (userTournamentRepository.existsByUserAndTournament(user, tournament)) {
            log.error("SERVICE: User already registered!");
            throw new ConflictException("Jesteś już zarejestrowany na ten turniej");
        }
        log.info("SERVICE: User not registered yet - OK");

        log.info("SERVICE: Step 4 - Checking available spots");
        int currentRegistrations = getRegisteredUsersCount(tournament);
        log.info("SERVICE: Current registrations: {}, Max players: {}", currentRegistrations, tournament.getMaxPlayers());
        if (tournament.getMaxPlayers() != null && currentRegistrations >= tournament.getMaxPlayers()) {
            log.error("SERVICE: No available spots!");
            throw new ConflictException("/Brak wolnych miejsc na turnieju");
        }
        log.info("SERVICE: Available spots OK");

        log.info("SERVICE: Step 5 - Creating UserTournament entity");
        UserTournament registration = new UserTournament();
        registration.setUser(user);
        registration.setTournament(tournament);
        log.info("SERVICE: Step 6 - Adding categories: {}", categories);
        registration.addCategories(categories);
        log.info("SERVICE: Categories added successfully");

        registration.setRegistrationTime(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setPaid(false);

        log.info("SERVICE: Step 7 - Calculating total fee");
        Double totalFee = calculateTotalFee(tournament, categories);
        log.info("SERVICE: Total fee calculated: {}", totalFee);
        registration.setTotalFee(totalFee);

        log.info("SERVICE: Step 8 - Saving to database");
        UserTournament saved = userTournamentRepository.save(registration);
        log.info("SERVICE: Saved successfully with id={}", saved.getId());

        return saved;
    }

    @Override
    public List<UserTournamentProfileDto> getUserRegistrations(Long id) {
        return userTournamentRepository.findByUserId(id)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<UserTournament> getTournamentRegistrations(Tournament tournament) {
        return userTournamentRepository.findByTournamentOrderByRegistrationTimeAsc(tournament)
                .orElse(new ArrayList<>());
    }

    @Override
    public UserTournament findById(Long id) {
        return userTournamentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Registration not found with id: " + id));
    }

    @Override
    @Transactional
    public void cancelRegistration(Long registrationId, Long currentUserId) {
        UserTournament registration = findById(registrationId);

        if (!tournamentService.isRegistrationOpen(registration.getTournament())){
            throw new ConflictException("Tournament is not open");
        }

        if (!registration.getUser().getId().equals(currentUserId)) {
            throw new ConflictException("Nie masz uprawnień do anulowania tej rejestracji");
        }

        if (LocalDateTime.now().plusDays(1).isAfter(registration.getTournament().getDateOfTournament())) {
            throw new ConflictException("Nie można anulować rejestracji na mniej niż 24h przed turniejem");
        }

        if (registration.getStatus() == RegistrationStatus.APPROVED && registration.isPaid()) {
            throw new ConflictException("Nie można anulować potwierdzonej i opłaconej rejestracji");
        }

        User user = registration.getUser();
        Tournament tournament = registration.getTournament();

        List<Partnership> partnerships = partnershipRepository
                .findPartnershipByTournamentAndPlayer(tournament, user);

        if (!partnerships.isEmpty()) {
            partnerships.forEach(partnership -> {
                GameCategories category = partnership.getCategory();

                if (partnership.getStatus() == PartnershipStatus.CONFIRMED) {
                    User partnerUser = partnership.getPartnerOf(user);

                    UserTournament partnerUserTournament = userTournamentRepository
                            .findByUserAndTournament(partnerUser, tournament)
                            .orElse(null);

                    if (partnerUserTournament != null) {
                        partnerUserTournament.removeCategory(category);
                        userTournamentRepository.save(partnerUserTournament);
                    }
                }
            });

            partnershipRepository.deleteAll(partnerships);
        }

        userTournamentRepository.delete(registration);

    }

    @Override
    public UserTournament updateRegistrationStatus(Long registrationId, RegistrationStatus status) {
        UserTournament registration = findById(registrationId);
        registration.setStatus(status);
        return userTournamentRepository.save(registration);
    }

    @Override
    public UserTournament confirmPayment(Long registrationId) {
        UserTournament registration = findById(registrationId);
        registration.setPaid(true);
        registration.setStatus(RegistrationStatus.APPROVED);
        return userTournamentRepository.save(registration);
    }

    @Override
    public boolean isUserRegisteredForTournament(User user, Tournament tournament) {
        return userTournamentRepository.existsByUserAndTournament(user, tournament);
    }

    @Override
    public int getRegisteredUsersCount(Tournament tournament) {
        return (int) userTournamentRepository.findByTournamentOrderByRegistrationTimeAsc(tournament)
                .orElse(new ArrayList<>())
                .stream()
                .filter(ut -> !ut.getGameCategories().isEmpty())
                .count();
    }

    @Override
    public double calculateTotalFee(Tournament tournament, Set<GameCategories> categories) {
        BigDecimal total = BigDecimal.ZERO;

        for (GameCategories category : categories) {
            TournamentCategoryPrice price = categoryPriceRepository
                    .findByTournamentAndCategory(tournament, category)
                    .orElse(null);

            if (price != null) {
                total = total.add(price.getPrice());
            }
        }

        return total.doubleValue();
    }

    @Override
    public boolean isUserAvailableForCategory(User user, Tournament tournament, GameCategories category) {
        UserTournament userTournament = userTournamentRepository.findUserTournamentByUserAndTournamentAndCategory(user, tournament, category);
        return userTournament == null;
    }

    @Override
    public int amountOfPlayerCategories(Long userId, Long tournamentId) {
        UserTournament userTournament = userTournamentRepository.findUserTournamentByUserIdAndTournamentId(userId, tournamentId)
                .orElseThrow(() -> new NotFoundException("UserTournament Not Found"));
        return userTournament.getGameCategories().size();
    }


    @Override
    public boolean areAvailableFreeCategoriesForUser(UserTournament userTournament) {
        return amountOfPlayerCategories(
                userTournament.getUser().getId(),
                userTournament.getTournament().getId())
                < userTournament.getTournament().getMaxCategoriesToPlay();
    }

    @Override
    @Transactional
    public void removeCategoryByPlayer(Long userTournamentId, GameCategories category, String username) {
        UserTournament userTournament = userTournamentRepository.findById(userTournamentId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono uczestnictwa w tym turnieju"));
        if (!tournamentService.isRegistrationOpen(userTournament.getTournament())){
            throw new ConflictException("Tournament is not open");
        }
        isPlayerAuth(username, userTournament);

        removeDoubleCategory(category, userTournament);
        userTournament.removeCategory(category);

        userTournament.setTotalFee(calculateTotalFee(
                userTournament.getTournament(),
                userTournament.getGameCategories()
        ));
        userTournamentRepository.save(userTournament);
    }

    private void removeDoubleCategory(GameCategories category, UserTournament userTournament) {
        if (category.name().length() > 1 && category.name().charAt(1) == 'D') {
            User currentUser = userTournament.getUser();
            Tournament tournament = userTournament.getTournament();

            List<Partnership> partnerships = partnershipRepository
                    .findPartnershipsByTournamentAndPlayerAndCategory(tournament, currentUser, category);
            partnerships.forEach(partnership -> {
                log.info("Found partnerships {}", partnership.getId());
            });

            if (!partnerships.isEmpty()) {
                partnerships.forEach(partnership -> {
                    if (partnership.getStatus() == PartnershipStatus.CONFIRMED) {

                        User partnerUser = partnership.getPartnerOf(currentUser);

                        UserTournament partnerUserTournament = userTournamentRepository
                                .findByUserAndTournament(partnerUser, tournament)
                                .orElse(null);

                        if (partnerUserTournament != null) {
                            partnerUserTournament.removeCategory(category);
                            userTournamentRepository.save(partnerUserTournament);
                        }
                    }

                    partnership.setStatus(PartnershipStatus.CANCELLED);
                    partnership.setRespondedAt(LocalDateTime.now());
                    partnershipRepository.save(partnership);
                });
            }
        }
    }

    @Override
    public void removeCategoryForPlayerByModerator(Long userTournamentId,
                                                   GameCategories category,
                                                   String username) {
        UserTournament userTournament = findById(userTournamentId);
        User user = userService.findUserEntityByEmail(username);

        boolean isModerator = userTournament.getTournament().getModerators().contains(user);
        boolean isCreator = Objects.equals(userTournament.getTournament().getCreatorOfTournament(), user);
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isModerator || isCreator || isAdmin) {
            removeDoubleCategory(category, userTournament);
            userTournament.removeCategory(category);

            userTournament.setTotalFee(calculateTotalFee(
                    userTournament.getTournament(),
                    userTournament.getGameCategories()
            ));

            userTournamentRepository.save(userTournament);
        } else {
            throw new ConflictException("You are not allowed to remove a category");
        }
    }

    @Override
    public UserTournament findUserTournamentByUserIdAndTournamentId(Long userId, Long tournamentId) {
        return userTournamentRepository.findUserTournamentByUserIdAndTournamentId(userId, tournamentId)
                .orElseThrow(() -> new NotFoundException("UserTournament not found"));
    }

    @Override
    public UserTournament findUserTournamentByUserIdAndTournamentIdIfExists(Long userId, Long tournamentId) {
        return userTournamentRepository.findUserTournamentByUserIdAndTournamentId(userId, tournamentId).orElse(null);
    }


    private void isPlayerAuth(String username, UserTournament userTournament) {
        User user = userService.findUserEntityByEmail(username);
        if (!Objects.equals(userTournament.getUser(), user)) {
            throw new ConflictException("You are not allowed to do this operation");
        }
    }


    private UserTournamentProfileDto convertToDto(UserTournament userTournament) {
        return UserTournamentProfileDto.builder()
                .tournamentName(userTournament.getTournament().getNameTournament())
                .userName(userTournament.getUser().getFirstName() + " " + userTournament.getUser().getLastName())
                .tournamentDate(userTournament.getTournament().getDateOfTournament())
                .categories(userTournament.getGameCategories())
                .tournamentUrl(userTournament.getTournament().getUrlTournament())
                .tournamentId(userTournament.getTournament().getId())
                .userTournamentId(userTournament.getId())
                .build();
    }
}