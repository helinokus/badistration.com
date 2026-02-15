package com.helinok.pzbad_registration.Services.PartnershipService;

import com.helinok.pzbad_registration.Dtos.*;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Exceptions.PartnerAlreadyPlayCategoryException;
import com.helinok.pzbad_registration.Models.Partnership;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.UserTournament;
import com.helinok.pzbad_registration.Repositories.PartnershipRepository;
import com.helinok.pzbad_registration.Repositories.UserTournamentRepository;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class PartnershipService implements IPartnershipService {

    private final PartnershipRepository partnershipRepository;
    private final IUserService userService;
    private final UserTournamentRepository userTournamentRepository;

    @Override
    public void declinePartnership(Long partnershipId, String username) {
        Partnership partnershipByCategoryAndTournament =
                partnershipRepository.findById(partnershipId)
                        .orElseThrow(() -> new NotFoundException("Nie znaleziono partnerstwa"));

        if (partnershipByCategoryAndTournament.getStatus() != PartnershipStatus.PENDING) {
            throw new ConflictException("Status partnerstwa nie posiada statusu PENDING");
        }

        User user = userService.findUserEntityByEmail(username);
        if (!Objects.equals(partnershipByCategoryAndTournament.getPlayer2(), user)) {
            throw new ConflictException("You are not allowed to decline this partnership");
        }

        partnershipByCategoryAndTournament.setStatus(PartnershipStatus.DECLINED);
        partnershipRepository.save(partnershipByCategoryAndTournament);
    }

    @Override
    public void cancelPartnership(Long partnershipId, String username) {
        Partnership partnership =
                partnershipRepository.findById(partnershipId)
                        .orElseThrow(() -> new NotFoundException("Nie znaleziono partnerstwa"));

        User user = userService.findUserEntityByEmail(username);
        if (!Objects.equals(partnership.getPlayer1(), user)) {
            throw new ConflictException("You are not allowed to cancel this partnership");
        }

        if (partnership.getStatus() == PartnershipStatus.CONFIRMED) {
            Tournament tournament = partnership.getTournament();
            GameCategories category = partnership.getCategory();

            UserTournament initiatorUT = userTournamentRepository
                    .findByUserAndTournament(partnership.getPlayer1(), tournament)
                    .orElse(null);
            if (initiatorUT != null) {
                initiatorUT.removeCategory(category);
                userTournamentRepository.save(initiatorUT);
            }

            UserTournament partnerUT = userTournamentRepository
                    .findByUserAndTournament(partnership.getPlayer2(), tournament)
                    .orElse(null);
            if (partnerUT != null) {
                partnerUT.removeCategory(category);
                userTournamentRepository.save(partnerUT);
            }
        }

        partnership.setStatus(PartnershipStatus.CANCELLED);
        partnership.setRespondedAt(LocalDateTime.now());
        partnershipRepository.save(partnership);
    }

    @Override
    public List<PartnershipInitiatorDto> getAllSentPartnerships(Long userId) {
        List<PartnershipInitiatorDto> list = partnershipRepository.findAllSentPartnerships(userId).stream()
                .map(this::toPartnershipInitiatorDto).toList();
        System.out.println(userId);
        return list;
    }

    @Override
    public List<PartnershipReceiverDto> getAllReceivedPartnerships(Long userId) {
        List<PartnershipReceiverDto> list = partnershipRepository.findAllReceivedPartnerships(userId).stream()
                .map(this::toPartnershipReceiverDto).toList();
        System.out.println(list);
        System.out.println(userId);
        return list;
    }


    public PartnershipInitiatorDto toPartnershipInitiatorDto(Partnership partnership) {
        PartnershipInitiatorDto build = PartnershipInitiatorDto.builder()
                .id(partnership.getId())
                .tournamentId(partnership.getTournament().getId())
                .tournamentName(partnership.getTournament().getNameTournament())
                .category(partnership.getCategory())
                .status(partnership.getStatus())
                .partnerId(partnership.getPlayer2().getId())
                .partnerName(partnership.getPlayer2().getFullName())
                .partnerEmail(partnership.getPlayer2().getEmail())
                .createdAt(partnership.getCreatedAt())
                .respondedAt(partnership.getRespondedAt())
                .build();
        if (partnership.isPending()){
            build.setStatus(PartnershipStatus.PENDING);
            build.setCanCancel(true);
            build.setCanDecline(false);
            build.setCanAccept(false);
        }
        return build;
    }
    public PartnershipReceiverDto toPartnershipReceiverDto(Partnership partnership) {
        PartnershipReceiverDto build = PartnershipReceiverDto.builder()
                .id(partnership.getId())
                .tournamentId(partnership.getTournament().getId())
                .tournamentName(partnership.getTournament().getNameTournament())
                .category(partnership.getCategory())
                .status(partnership.getStatus())
                .initiatorId(partnership.getPlayer1().getId())
                .initiatorName(partnership.getPlayer1().getFullName())
                .initiatorEmail(partnership.getPlayer1().getEmail())
                .createdAt(partnership.getCreatedAt())
                .respondedAt(partnership.getRespondedAt())
                .build();
        if (partnership.isPending()){
            build.setStatus(PartnershipStatus.PENDING);
            build.setCanAccept(true);
            build.setCanDecline(true);
            build.setCanCancel(false);
        }
        return build;
    }

    @Override
    public int expireAllPendingPartnershipsByTournament(Tournament tournament){
        List<Partnership> partnerships = partnershipRepository.findPartnershipsByTournamentAndStatus(tournament);
        partnerships.forEach(p -> {
            p.setStatus(PartnershipStatus.EXPIRED);
            p.setRespondedAt(LocalDateTime.now());
        });
        partnershipRepository.saveAll(partnerships);
        return partnerships.size();
    }

    @Override
    public Partnership getPartnershipById(Long partnershipId, User user) {
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new NotFoundException("Nie znalazłem tego partnerstwa"));
        if (!user.getId().equals(partnership.getPlayer2().getId())) {
            throw new ConflictException("Nie masz dostępu do tego partnerstwa");
        }
        return partnership;
    }

}
