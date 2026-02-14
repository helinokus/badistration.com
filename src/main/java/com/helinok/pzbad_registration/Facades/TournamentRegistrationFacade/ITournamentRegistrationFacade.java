package com.helinok.pzbad_registration.Facades.TournamentRegistrationFacade;

import com.helinok.pzbad_registration.Dtos.AcceptPartnershipDto;
import com.helinok.pzbad_registration.Dtos.CreatePartnershipDto;
import com.helinok.pzbad_registration.Dtos.RecentRegistrationDto;
import com.helinok.pzbad_registration.Dtos.TournamentRegistrationDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Services.TournamentDataExport.PlayerTournamentRegistrationDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITournamentRegistrationFacade {
    @Transactional
    void createRegistrationWithPartnerships(User user, Tournament tournament,
                                            TournamentRegistrationDto request
    );

    List<String> addNewListCategories(List<GameCategories> categories,
                                      Map<GameCategories, List<Long>> mapDoubledCategoriesToUsers,
                                      Long initiatorUserTournamentId,
                                      String username);

    void acceptNewAssignToTournament(Long accepterId, AcceptPartnershipDto dto, Set<GameCategories> categories);

    void acceptNewCategoryForPartner(Long accepterId, AcceptPartnershipDto dto);

    void acceptNewCategoryForInitiator(AcceptPartnershipDto dto);

    void addNewCategoryAfterPartnership(User user, Tournament tournament, GameCategories category);

    void createPartnership(Long initiatorId, CreatePartnershipDto dto);

    @Transactional
    void createOnlyPartnership(Long initiatorId, CreatePartnershipDto dto);

    void acceptNewPartnership(Long accepterId, AcceptPartnershipDto dto, Set<GameCategories> categories);

    List<RecentRegistrationDto> getAllRegistrations(Long tournamentId);

    List<PlayerTournamentRegistrationDto> getAllPlayersInfo(Long tournamentId);
}
