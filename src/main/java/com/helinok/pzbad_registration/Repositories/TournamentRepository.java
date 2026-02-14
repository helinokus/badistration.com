package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.UserTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findTournamentByNameTournament(String nameTournament);

    @Query("select t from Tournament t where t.registrationExpired <= :registrationExpiredDate and t.isClosed = false")
    Optional<List<Tournament>> findTournamentsWhereRegistrationDateExpired(@Param("registrationExpiredDate")
                                                                           LocalDateTime registrationExpiredDate);
}
