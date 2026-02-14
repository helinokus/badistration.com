package com.helinok.pzbad_registration.Controllers.Public;

import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentServiceCache;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Slf4j
public class PublicController {
    private final IUserService userService;
    private final ITournamentServiceCache tournamentServiceCache;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            List<User> allUsers = userService.getAllActiveUsers();
            List<TournamentDto> allTournaments = tournamentServiceCache.getAllTournaments();

            stats.put("totalUsers", allUsers.size());
            stats.put("totalTournaments", allTournaments.size());
            stats.put("activeTournaments", allTournaments.stream()
                    .filter(t -> t.getDateOfTournament().isAfter(LocalDateTime.now()))
                    .count());
            long countClubsAmount = allUsers.stream()
                    .map(e -> e.getTeamName() != null)
                    .count();
            stats.put("totalClubs", countClubsAmount);

            return ResponseEntity.ok(new ApiResponse("Dashboard statistics", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Błąd podczas pobierania statystyk: " + e.getMessage()));
        }
    }
}
