package com.helinok.pzbad_registration.Services.TournamentDataExport;


import com.helinok.pzbad_registration.Enums.ExportType;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public interface ITournamentDataExportService {

    @Transactional
    byte[] createFullExcelResult(Long tournamentId, String userEmail);
}
