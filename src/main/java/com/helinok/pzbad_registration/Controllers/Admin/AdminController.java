package com.helinok.pzbad_registration.Controllers.Admin;

import com.helinok.pzbad_registration.Dtos.GetAllUsersAdminDto;
import com.helinok.pzbad_registration.Dtos.GetAllUsersDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Models.Role;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.UserTournament;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentServiceCache;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final IUserService userService;
    private final ITournamentService tournamentService;


    @GetMapping("/dashboard")
    @Operation(summary = "get dashboard statistics")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            List<User> allUsers = userService.getAllActiveUsers();
            List<Tournament> allTournaments = tournamentService.getAllAdminTournaments();

            stats.put("totalUsers", allUsers.size());
            stats.put("totalTournaments", allTournaments.size());
            stats.put("activeTournaments", allTournaments.stream()
                    .filter(t -> t.getDateOfTournament().isAfter(LocalDateTime.now()))
                    .count());

            Map<String, Long> roleStats = allUsers.stream()
                    .flatMap(user -> user.getRoles().stream())
                    .collect(Collectors.groupingBy(
                            Role::getName,
                            Collectors.counting()
                    ));
            stats.put("roleStats", roleStats);

            long totalRegistrations = allTournaments.stream()
                    .mapToLong(t -> t.getUserTournaments() != null
                            ? t.getUserTournaments().stream().filter(ut -> !ut.getGameCategories().isEmpty()).count()
                            : 0)
                    .sum();
            stats.put("totalRegistrations", totalRegistrations);

            log.info("stats {}", stats.size());

            List<Map<String, Object>> topTournaments = allTournaments.stream()
                    .filter(t -> t.getUserTournaments() != null && !t.getUserTournaments().isEmpty())
                    .map(t -> {
                        Map<String, Object> tournamentStats = new HashMap<>();
                        tournamentStats.put("name", t.getNameTournament());
                        long activeRegistrations = t.getUserTournaments().stream()
                                .filter(ut -> !ut.getGameCategories().isEmpty())
                                .count();
                        tournamentStats.put("registrations", activeRegistrations);
                        tournamentStats.put("date", t.getDateOfTournament());
                        return tournamentStats;
                    })
                    .sorted((a, b) -> Long.compare((Long) b.get("registrations"), (Long) a.get("registrations")))
                    .limit(5)
                    .collect(Collectors.toList());
            stats.put("topTournaments", topTournaments);

            log.info("stats {}", stats.size());

            return ResponseEntity.ok(new ApiResponse("Dashboard statistics", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania statystyk: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    @Operation(summary = "get all users info")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        try {
            List<GetAllUsersAdminDto> users;

            if (search != null && !search.isEmpty()) {
                users = userService.searchUsersAdmin(search);
            } else {
                users = userService.getAllUsersAdminDtos();
            }

            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, users.size());
            List<GetAllUsersAdminDto> pagedUsers = users.subList(startIndex, endIndex);

            Map<String, Object> result = new HashMap<>();
            result.put("users", pagedUsers);
            result.put("totalElements", users.size());
            result.put("totalPages", (int) Math.ceil((double) users.size() / size));
            result.put("currentPage", page);

            return ResponseEntity.ok(new ApiResponse("Lista użytkowników", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania użytkowników: " + e.getMessage()));
        }
    }

    @GetMapping("/tournaments")
    @Operation(summary = "get all tournament info")
    public ResponseEntity<ApiResponse> getAllTournamentsForAdmin() {
        try {
            List<TournamentDto> tournaments = tournamentService.getAllTournaments();
            return ResponseEntity.ok(new ApiResponse("Lista wszystkich turniejów", tournaments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania turniejów: " + e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/toggle-status")
    @Operation(summary = "change status to the user")
    public ResponseEntity<ApiResponse> toggleUserStatus(@PathVariable Long userId) {
        try {
            User user = userService.findUserEntityById(userId);
            userService.toggleUserStatus(userId);
            return ResponseEntity.ok(new ApiResponse("Status użytkownika został zmieniony"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas zmiany statusu: " + e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/roles/{roleName}")
    @Operation(summary = "assign new role to the user")
    public ResponseEntity<ApiResponse> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        try {
            userService.assignRole(userId, roleName.toUpperCase());
            return ResponseEntity.ok(new ApiResponse("Rola została przypisana"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas przypisywania roli: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    @Operation(summary = "assign new role to the user")
    public ResponseEntity<ApiResponse> deleteRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        try {
            userService.deleteRole(userId, roleName.toUpperCase());
            return ResponseEntity.ok(new ApiResponse("Rola została przypisana"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas przypisywania roli: " + e.getMessage()));
        }
    }
}