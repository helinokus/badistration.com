package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Models.TournamentCategoryPrice;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Enums.GameCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentCategoryPriceRepository extends JpaRepository<TournamentCategoryPrice, Long> {
    List<TournamentCategoryPrice> findByTournament(Tournament tournament);
    Optional<TournamentCategoryPrice> findByTournamentAndCategory(Tournament tournament, GameCategories category);
}