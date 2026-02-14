package com.helinok.pzbad_registration.Services.PartnershipService;

import com.helinok.pzbad_registration.Dtos.PartnershipInitiatorDto;
import com.helinok.pzbad_registration.Dtos.PartnershipReceiverDto;
import com.helinok.pzbad_registration.Models.Partnership;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;

import java.util.List;

public interface IPartnershipService {



    void declinePartnership(Long partnershipId, String username);
    void cancelPartnership(Long partnershipId, String username); // cancel by initiator, if he doesn't want to play with somebody yet

    List<PartnershipInitiatorDto> getAllSentPartnerships(Long userId);
    List<PartnershipReceiverDto> getAllReceivedPartnerships(Long userId);

    int expireAllPendingPartnershipsByTournament(Tournament tournament);
    Partnership getPartnershipById(Long partnershipId, User user);

}
