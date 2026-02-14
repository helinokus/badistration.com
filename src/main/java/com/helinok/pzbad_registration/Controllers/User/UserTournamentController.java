package com.helinok.pzbad_registration.Controllers.User;

import com.helinok.pzbad_registration.Dtos.*;
import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Exceptions.PartnerAlreadyPlayCategoryException;
import com.helinok.pzbad_registration.Facades.TournamentRegistrationFacade.ITournamentRegistrationFacade;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.UserTournament;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Services.PartnershipService.IPartnershipService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-tournaments")
public class UserTournamentController {

    private final IUserTournamentService userTournamentService;
    private final IUserService userService;
    private final ITournamentService tournamentService;
    private final IPartnershipService partnershipService;
    private final ITournamentRegistrationFacade tournamentRegistrationFacade;

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Register for tournament")
    public ResponseEntity<ApiResponse> registerForTournament(
            @RequestBody TournamentRegistrationDto request,
            Authentication authentication) {
        try {
            log.info("=== REGISTRATION STEP 1: Received request ===");
            log.info("Request data: tournamentId={}, categories={}, doubles={}",
                    request.getTournamentId(), request.getCategories(), request.getDoubles());

            log.info("=== REGISTRATION STEP 2: Finding user by email ===");
            User user = userService.findUserEntityByEmail(authentication.getName());
            log.info("Found user: id={}, email={}", user.getId(), user.getEmail());

            log.info("=== REGISTRATION STEP 3: Finding tournament by id ===");
            Tournament tournament = tournamentService.findEntityById(request.getTournamentId());
            log.info("Found tournament: id={}, name={}", tournament.getId(), tournament.getNameTournament());

            log.info("=== REGISTRATION STEP 4: Registering user for tournament ===");
            log.info("Categories to register: {}", request.getCategories());

            tournamentRegistrationFacade.createRegistrationWithPartnerships(user, tournament, request);
            return ResponseEntity.ok(new ApiResponse(
                    "Rejestracja zakończona pomyślnie"
            ));

        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }catch (ConflictException | PartnerAlreadyPlayCategoryException ce){
            log.error(ce.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(ce.getMessage()));
        }
    }

    @PostMapping("/add-categories")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "register for one more category")
    public ResponseEntity<ApiResponse> addOneNewCategory(@RequestBody AddNewCategoryDto dto,
                                                         Authentication authentication) {
        try {
            List<String> list = tournamentRegistrationFacade.addNewListCategories(
                    dto.getCategories(),
                    dto.getMapDoubledCategoriesToUsers(),
                    dto.getInitiatorUserTournamentId(),
                    authentication.getName());
            return ResponseEntity.ok(new ApiResponse("Dodano nowe kategorie", list));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/remove-category")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "remove category by player")
    public ResponseEntity<ApiResponse> removeOneCategory(@RequestBody RemoveCategoryDto dto, Authentication authentication){
        try {
            userTournamentService.removeCategoryByPlayer(
                    dto.getUserTournamentId(),
                    dto.getCategory(),
                    authentication.getName());
            return ResponseEntity.ok(new ApiResponse("Usunięto jedną kategorię"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/remove-category-by-moderator")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "remove category for player by moderator/admin")
    public ResponseEntity<ApiResponse> removeCategoryByModerator(
            @RequestBody RemoveCategoryDto dto,
            Authentication authentication) {
        try {
            userTournamentService.removeCategoryForPlayerByModerator(
                    dto.getUserTournamentId(),
                    dto.getCategory(),
                    authentication.getName());
            return ResponseEntity.ok(new ApiResponse("Kategoria została usunięta przez moderatora"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        }
    }




    // Pobierz moje rejestracje
    @GetMapping("/my-registrations")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "get my registrations on tournament")
    public ResponseEntity<ApiResponse> getMyRegistrations(Authentication authentication) {
        try {
            User user = userService.findUserEntityByEmail(authentication.getName());
            List<UserTournamentProfileDto> registrations = userTournamentService.getUserRegistrations(user.getId());
            return ResponseEntity.ok(new ApiResponse("Moje rejestracje", registrations));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania rejestracji: " + e.getMessage()));
        }
    }

    // Anuluj rejestrację
    @DeleteMapping("/{registrationId}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "cancel registration for tournament by id")
    public ResponseEntity<ApiResponse> cancelRegistration(
            @PathVariable Long registrationId,
            Authentication authentication) {
        try {
            User user = userService.findUserEntityByEmail(authentication.getName());
            userTournamentService.cancelRegistration(registrationId, user.getId());

            return ResponseEntity.ok(new ApiResponse("Rejestracja została anulowana"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas anulowania rejestracji: " + e.getMessage()));
        }
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "get all registrations for tournament only by moderator/admin")
    public ResponseEntity<ApiResponse> getTournamentRegistrations(@PathVariable Long tournamentId) {
        try {
            List<RecentRegistrationDto> dtos = tournamentRegistrationFacade.getAllRegistrations(tournamentId);
            if (dtos.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("Jeszcze nikt nie zarejestrował sie na turniej"));
            }

            return ResponseEntity.ok(new ApiResponse("Rejestracje na turniej", dtos));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{registrationId}/status")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "update status for registration on tournament")
    public ResponseEntity<ApiResponse> updateRegistrationStatus(
            @PathVariable Long registrationId,
            @RequestParam RegistrationStatus status) {
        try {
            UserTournament updated = userTournamentService.updateRegistrationStatus(registrationId, status);

            return ResponseEntity.ok(new ApiResponse(
                    "Status rejestracji został zaktualizowany",
                    convertToDto(updated)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas aktualizacji statusu: " + e.getMessage()));
        }
    }

    // Potwierdź płatność
    @PatchMapping("/{registrationId}/payment")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "confirm payment (useless)")
    public ResponseEntity<ApiResponse> confirmPayment(@PathVariable Long registrationId) {
        try {
            UserTournament updated = userTournamentService.confirmPayment(registrationId);

            return ResponseEntity.ok(new ApiResponse(
                    "Płatność została potwierdzona",
                    convertToDto(updated)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas potwierdzania płatności: " + e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/user-registered")
    @Operation(summary = "bool is user already registered for the tournament")
    public ResponseEntity<ApiResponse> isUserRegisteredForTournament(Authentication authentication, @PathVariable Long tournamentId) {
        try {
            User user = userService.findUserEntityByEmail(authentication.getName());
            Tournament tournament = tournamentService.findEntityById(tournamentId);
            boolean userRegisteredForTournament = userTournamentService.isUserRegisteredForTournament(user, tournament);
            return ResponseEntity.ok(new ApiResponse("Result of is registered for tournament",  userRegisteredForTournament));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/user-available")
    @Operation(summary = "bool is user is free for this category on this tournament")
    public ResponseEntity<ApiResponse> isUserAvailableForCategory(Authentication authentication,
                                                                  @RequestParam Long tournamentId,
                                                                  @RequestParam GameCategories category) {
        try {
            User user = userService.findUserEntityByEmail(authentication.getName());
            Tournament tournament = tournamentService.findEntityById(tournamentId);
            boolean userAvailableForCategory = userTournamentService.isUserAvailableForCategory(user, tournament, category);
            return ResponseEntity.ok(new ApiResponse("Result of is registered for tournament", userAvailableForCategory));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    // Helper method do konwersji na DTO
    private UserTournamentProfileDto convertToDto(UserTournament registration) {
        return UserTournamentProfileDto.builder()
                .userName(registration.getUser().getFullName())
                .tournamentName(registration.getTournament().getNameTournament())
                .categories(registration.getGameCategories() != null ? registration.getGameCategories() : null)
                .build();
    }


}