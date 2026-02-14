package com.helinok.pzbad_registration.Controllers.Moderator;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentServiceCache;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentControllerTest {

    @Mock
    ITournamentService tournamentService;

    @Mock
    ITournamentServiceCache tournamentServiceCache;

    @Mock
    IUserService userService;

    @InjectMocks
    TournamentController tournamentController;

    @Test
    void getAllTournaments() {
        var tournamentDto1 = new TournamentDto();
        tournamentDto1.setId(1L);
        tournamentDto1.setDateOfTournament(LocalDateTime.now());
        tournamentDto1.setNameTournament("Tournament1");

        var tournamentDto2 = new TournamentDto();
        tournamentDto2.setId(2L);
        tournamentDto2.setDateOfTournament(LocalDateTime.now());
        tournamentDto2.setNameTournament("Tournament2");

        List<TournamentDto> tournaments = List.of(tournamentDto1, tournamentDto2);

        when(tournamentService.getAllTournaments())
                .thenReturn(tournaments);

        var response = tournamentController.getAllTournaments();

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        List<TournamentDto> elements = (List<TournamentDto>) Objects.requireNonNull(response.getBody()).getData();
        assertEquals(tournaments, elements);
        assertEquals(2, elements.size());
    }

    @Test
    void getTournamentById() {
        var tournamentDto = new TournamentDto();
        tournamentDto.setId(1L);
        tournamentDto.setDateOfTournament(LocalDateTime.now());
        tournamentDto.setNameTournament("Tournament1");

        when(tournamentServiceCache.getTournamentById(1L))
                .thenReturn(tournamentDto);

        var response = tournamentController.getTournamentById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Szczegóły turnieju", response.getBody().getMessage());
    }

    @Test
    void createTournament() {
        var tournamentDto = new TournamentDto();
        tournamentDto.setId(1L);
        tournamentDto.setDateOfTournament(LocalDateTime.now());
        tournamentDto.setNameTournament("Tournament1");
        tournamentDto.setUrlTournament("badistration.com");
        tournamentDto.setCreatorEmail("admin@badistration.com");

        var creator = new User();
        creator.setId(1L);
        creator.setEmail("admin@badistration.com");

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("admin@badistration.com");

        when(userService.findUserEntityByEmail("admin@badistration.com"))
                .thenReturn(creator);

        when(tournamentService.createTournament(tournamentDto, creator))
                .thenReturn(tournamentDto);

        var response = tournamentController.createTournament(tournamentDto, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Turniej utworzony", response.getBody().getMessage());
        verify(userService).findUserEntityByEmail("admin@badistration.com");
        verify(tournamentService).createTournament(tournamentDto, creator);
    }
}