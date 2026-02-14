package com.helinok.pzbad_registration.Controllers.User;

import com.helinok.pzbad_registration.Dtos.AcceptPartnershipDto;
import com.helinok.pzbad_registration.Dtos.CreatePartnershipDto;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Facades.TournamentRegistrationFacade.ITournamentRegistrationFacade;
import com.helinok.pzbad_registration.Models.Partnership;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Services.PartnershipService.IPartnershipService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnershipControllerTest {

    @Mock
    IPartnershipService partnershipService;

    @Mock
    IUserService userService;

    @Mock
    ITournamentRegistrationFacade tournamentRegistrationFacade;

    @InjectMocks
    PartnershipController partnershipController;

    @Test
    void createPartnership_Success_Returns201() {
        var dto = new CreatePartnershipDto();
        dto.setTournamentId(1L);
        dto.setPartnerId(2L);
        dto.setCategory(GameCategories.MD_A);

        var currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("player1@test.com");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("player1@test.com");


        when(userService.findUserEntityByEmail("player1@test.com"))
                .thenReturn(currentUser);

        doNothing().when(tournamentRegistrationFacade)
                .createOnlyPartnership(currentUser.getId(), dto);



        var response = partnershipController.createPartnership(dto, mockAuth);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Partnerstwo zostało utworzone", response.getBody().getMessage());

        verify(userService).findUserEntityByEmail("player1@test.com");
        verify(tournamentRegistrationFacade).createOnlyPartnership(currentUser.getId(), dto);
    }

    @Test
    void acceptNewPartnership_Success_Returns200() {
        Long partnershipId = 1L;
        Set<GameCategories> additionalCategories = new HashSet<>();
        additionalCategories.add(GameCategories.MD_A);

        var currentUser = new User();
        currentUser.setId(2L);
        currentUser.setEmail("player2@test.com");

        var initiator = new User();
        initiator.setId(1L);

        var tournament = new Tournament();
        tournament.setId(10L);

        var partnership = new Partnership();
        partnership.setId(partnershipId);
        partnership.setPlayer1(initiator);
        partnership.setTournament(tournament);
        partnership.setCategory(GameCategories.MD_A);

        Authentication mockAuth = mock(Authentication.class);


        when(mockAuth.getName()).thenReturn("player2@test.com");

        when(userService.findUserEntityByEmail("player2@test.com"))
                .thenReturn(currentUser);
        when(partnershipService.getPartnershipById(partnershipId, currentUser))
                .thenReturn(partnership);

        doNothing().when(tournamentRegistrationFacade)
                .acceptNewPartnership(eq(currentUser.getId()), any(AcceptPartnershipDto.class), anySet());

        var response = partnershipController.acceptNewPartnership(
                partnershipId,
                additionalCategories,
                mockAuth
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Partnerstwo zostało zaakceptowane", response.getBody().getMessage());

        verify(userService).findUserEntityByEmail("player2@test.com");
        verify(partnershipService).getPartnershipById(partnershipId, currentUser);
        verify(tournamentRegistrationFacade).acceptNewPartnership(
                eq(currentUser.getId()),
                any(AcceptPartnershipDto.class),
                anySet()
        );
    }

    @Test
    void declinePartnership_Success_Returns200() {
        Long partnershipId = 1L;

        var currentUser = new User();
        currentUser.setId(2L);
        currentUser.setEmail("player2@test.com");

        var partnership = new Partnership();
        partnership.setId(partnershipId);
        partnership.setCategory(GameCategories.MD_A);

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("player2@test.com");

        when(userService.findUserEntityByEmail("player2@test.com"))
                .thenReturn(currentUser);
        when(partnershipService.getPartnershipById(partnershipId, currentUser))
                .thenReturn(partnership);

        doNothing().when(partnershipService)
                .declinePartnership(partnershipId, "player2@test.com");

        var response = partnershipController.declinePartnership(partnershipId, mockAuth);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Zaproszenie na partnerstwo zostało odrzucone", response.getBody().getMessage());

        verify(userService).findUserEntityByEmail("player2@test.com");
        verify(partnershipService).getPartnershipById(partnershipId, currentUser);
        verify(partnershipService).declinePartnership(partnershipId, "player2@test.com");
    }
}
