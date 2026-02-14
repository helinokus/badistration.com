package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Models.UserTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantCardRepository extends JpaRepository<UserTournament, Long> {

}
