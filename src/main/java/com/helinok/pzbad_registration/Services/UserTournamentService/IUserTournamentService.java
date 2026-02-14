package com.helinok.pzbad_registration.Services.UserTournamentService;

import com.helinok.pzbad_registration.Dtos.UserTournamentProfileDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.UserTournament;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IUserTournamentService {
    UserTournament registerUserForTournament(User user, Tournament tournament, Set<GameCategories> categories);
    List<UserTournamentProfileDto> getUserRegistrations(Long id);
    List<UserTournament> getTournamentRegistrations(Tournament tournament);
    UserTournament findById(Long id);
    void cancelRegistration(Long registrationId, Long currentUser);
    UserTournament updateRegistrationStatus(Long registrationId, RegistrationStatus status);
    UserTournament confirmPayment(Long registrationId);
    boolean isUserRegisteredForTournament(User user, Tournament tournament);
    int getRegisteredUsersCount(Tournament tournament);
    double calculateTotalFee(Tournament tournament, Set<GameCategories> categories);
    boolean isUserAvailableForCategory(User user, Tournament tournament, GameCategories category);

    int amountOfPlayerCategories(Long userId, Long tournamentId);

    boolean areAvailableFreeCategoriesForUser(UserTournament userTournament);

    void removeCategoryByPlayer(Long userTournamentId, GameCategories category, String username);

    @Transactional
    void removeCategoryForPlayerByModerator(Long userTournamentId,
                                            GameCategories category,
                                            String username);

    UserTournament findUserTournamentByUserIdAndTournamentId(Long userId, Long tournamentId);

    UserTournament findUserTournamentByUserIdAndTournamentIdIfExists(Long userId, Long tournamentId);
}

