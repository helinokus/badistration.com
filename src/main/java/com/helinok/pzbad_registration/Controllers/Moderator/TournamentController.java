package com.helinok.pzbad_registration.Controllers.Moderator;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Dtos.ExportDataDto;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.CollectDataService.ICollectDataService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentServiceCache;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final ITournamentService tournamentService;
    private final ITournamentServiceCache tournamentServiceCache;
    private final IUserService userService;
    private final ICollectDataService collectDataService;

    @GetMapping
    @Operation(summary = "get all tournaments")
    public ResponseEntity<ApiResponse> getAllTournaments() {
        try {
            List<TournamentDto> tournaments = tournamentService.getAllTournaments();
            return ResponseEntity.ok(new ApiResponse("Lista turniejów", tournaments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania turniejów: " + e.getMessage()));
        }
    }

    @GetMapping("/filter")
    @Operation(summary = "get all tournaments by filter")
    public ResponseEntity<ApiResponse> getAllTournamentsFiltered(
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "0") Integer daysFrom,
            @RequestParam(defaultValue = "90") Integer daysTo){
        try {
            List<TournamentDto> tournamentDtoStream = tournamentService.filterByCategories(categories, daysFrom, daysTo);
            return ResponseEntity.ok(new ApiResponse("Filter by categories", tournamentDtoStream));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/upcoming")
    @Operation(summary = "get upcoming tournaments")
    public ResponseEntity<ApiResponse> getUpcomingTournaments(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<TournamentDto> tournaments = tournamentService.getUpcomingTournaments().stream()
                    .limit(limit)
                    .toList();
            return ResponseEntity.ok(new ApiResponse("Nadchodzące turnieje", tournaments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania turniejów: " + e.getMessage()));
        }
    }

    @GetMapping("/my-tournaments")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "get tournaments created by moderator (moderator/admin)")
    public ResponseEntity<ApiResponse> getModeratorTournaments(Authentication authentication) {
        try {
            User user = userService.findUserEntityByEmail(authentication.getName());
            List<Tournament> tournaments = tournamentService.getTournamentsByOrganizer(user);
            List<TournamentDto> dtos = tournaments.stream()
                    .map(tournamentServiceCache::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse("Moje turnieje", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania turniejów: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "get tournament by id")
    public ResponseEntity<ApiResponse> getTournamentById(@PathVariable Long id) {
        try {
            TournamentDto dto = tournamentServiceCache.getTournamentById(id);
            return ResponseEntity.ok(new ApiResponse("Szczegóły turnieju", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania turnieju: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "create a new tournament (moder/admin)")
    public ResponseEntity<ApiResponse> createTournament(
            @RequestBody TournamentDto dto,
            Authentication authentication) {
        try {
            User creator = userService.findUserEntityByEmail(authentication.getName());
            TournamentDto tournament = tournamentService.createTournament(dto, creator);
            return ResponseEntity.ok(new ApiResponse("Turniej utworzony", tournament));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas tworzenia turnieju: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "update tournament data (moder/admin)")
    public ResponseEntity<ApiResponse> updateTournament(
            @PathVariable Long id,
            @RequestBody TournamentDto dto,
            Authentication authentication) {
        try {


            TournamentDto updated = tournamentService.updateTournament(id, dto, authentication.getName());

            log.info("convertToDto completed!");
            return ResponseEntity.ok(new ApiResponse("Turniej zaktualizowany", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas aktualizacji turnieju: " + e.getMessage()));
        }
    }



    @GetMapping("/search")
    @Operation(summary = "search tournament by query")
    public ResponseEntity<ApiResponse> searchTournaments(
            @RequestParam(required = false, defaultValue = "") String query) {
        try {
            List<Tournament> tournaments = tournamentService.searchTournaments(query);
            List<TournamentDto> dtos = tournaments.stream()
                    .map(tournamentServiceCache::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse("Wyniki wyszukiwania", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas wyszukiwania: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/export-data")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "get tournament data")
    public ResponseEntity<ApiResponse> getExportData(@PathVariable Long id) {
        try {

            ExportDataDto exportData = collectDataService.getExportData(id);

            return ResponseEntity.ok(new ApiResponse("Dane do eksportu", exportData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas przygotowania danych: " + e.getMessage()));
        }
    }
}