package com.helinok.pzbad_registration.Controllers.Moderator;

import com.helinok.pzbad_registration.Dtos.CollectFullTournamentDataDto;
import com.helinok.pzbad_registration.Dtos.CollectRangedTournamentDataDto;
import com.helinok.pzbad_registration.Dtos.CollectSelectedTournamentDataDto;
import com.helinok.pzbad_registration.Services.TournamentDataExport.ITournamentDataExportService;
import com.helinok.pzbad_registration.Services.TournamentService.TournamentService;
import com.helinok.pzbad_registration.Services.UserService.UserService;
import com.helinok.pzbad_registration.Services.UserTournamentService.UserTournamentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exportData")
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
public class ExportDataController {
    private final ITournamentDataExportService tournamentDataExportService;
    private final UserTournamentService userTournamentService;
    private final UserService userService;
    private final TournamentService tournamentService;

    @PostMapping("/full/{tournamentId}")
    @Operation(summary = "export all tournament data to excel file")
    public ResponseEntity<Resource> exportAllTournamentData(@PathVariable Long tournamentId,
                                                            Authentication authentication) {
        try {
            log.info("Exporting data to tournament {}", tournamentId);
            byte[] fullExcelResult = tournamentDataExportService.createFullExcelResult(tournamentId, authentication.getName());
            ByteArrayResource resource = new ByteArrayResource(fullExcelResult);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"tournament_export.xlsx\"")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                    .contentLength(fullExcelResult.length)
                    .body(resource);
        }catch (Exception e){
            log.error("Failed to export tournament data for tournament {}", tournamentId, e);
            return ResponseEntity.badRequest()
                    .build();
        }
    }
}
