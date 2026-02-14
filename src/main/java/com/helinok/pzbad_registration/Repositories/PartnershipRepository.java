package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Models.Partnership;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnershipRepository extends JpaRepository<Partnership, Long> {

    @Query("select p from Partnership p where p.player1 = :user or p.player2 = :user")
    List<Partnership> findAllUserPartnerships(@Param("user")User user);


    @Query("select p from Partnership p where (p.player1 = :player or p.player2 = :player) and p.tournament = :tournament")
    List<Partnership> findPartnershipByTournamentAndPlayer(
            @Param("tournament") Tournament tournament,
            @Param("player") User player);

    @Query("select p from Partnership p where (p.player1 = :player or p.player2 = :player) and p.tournament = :tournament and p.category = :category")
    List<Partnership> findPartnershipsByTournamentAndPlayerAndCategory(
            @Param("tournament") Tournament tournament,
            @Param("player") User player,
            @Param("category") GameCategories category);

    @Query("SELECT p from Partnership p where p.tournament.id = :tournamentId and " +
            "p.category = :category and ((p.player1.id = :user1 and p.player2.id = :user2) or (p.player1.id = :user2 and p.player2.id = :user1)) ")
    Partnership findPartnershipByCategoryAndTournament(@Param("tournamentId") Long tournamentId,
                                                       @Param("category") GameCategories category,
                                                       @Param("user1") Long user1,
                                                       @Param("user2") Long user2);

    List<Partnership> findAllPartnershipsByTournamentAndCategory(Tournament tournament, GameCategories category);

    List<Partnership> findPartnershipByTournament(Tournament tournament);

    @Query("select p from Partnership p where p.tournament = :tournament" +
            " and p.status = 'PENDING'")
    List<Partnership> findPartnershipsByTournamentAndStatus(
            @Param("tournament") Tournament tournament);

    @Query("select p from Partnership p " +
            "where p.player1.id = :userId")
    List<Partnership> findAllSentPartnerships(@Param("userId") Long userId);

    @Query("select p from Partnership p " +
            "where p.player2.id = :userId")
    List<Partnership> findAllReceivedPartnerships(@Param("userId") Long userId);
}