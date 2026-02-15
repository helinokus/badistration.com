package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Models.UserTournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTournamentRepository extends JpaRepository<UserTournament, Long> {

    boolean existsByUserAndTournament(User user, Tournament tournament);

    Optional<UserTournament> findByUserAndTournament(User user, Tournament tournament);

    List<UserTournament> findByUserId(Long id);

    Optional<List<UserTournament>> findByTournamentOrderByRegistrationTimeAsc(Tournament tournament);


    @Query("SELECT ut FROM UserTournament ut " +
            "JOIN ut.registeredCategory rc " +
            "WHERE ut.user = :user " +
            "AND ut.tournament = :tournament " +
            "AND rc.category = :category")
    UserTournament findUserTournamentByUserAndTournamentAndCategory(@Param("user") User user,
                                                                    @Param("tournament") Tournament tournament,
                                                                    @Param("category") GameCategories category);

    Optional<UserTournament> findUserTournamentByUserIdAndTournamentId(Long userId, Long tournamentId);


    @Query("select u from UserTournament u where u.tournament = :tournament and u.registrationTime between :from and :to")
    Optional<List<UserTournament>> findTournamentRegistrationsByTournamentAndRange(@Param("tournament") Tournament tournament,
                                                                                   @Param("from") LocalDateTime from,
                                                                                   @Param("to") LocalDateTime to);

    @Query("select u from UserTournament u where u.tournament = :tournament and u.id in :regsIds ")
    Optional<List<UserTournament>> findUserTournamentsById(@Param("tournament") Tournament tournament,
                                                           @Param("regsIds") List<Long> regsIds);





}