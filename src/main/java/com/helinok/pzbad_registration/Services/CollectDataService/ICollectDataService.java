package com.helinok.pzbad_registration.Services.CollectDataService;

import com.helinok.pzbad_registration.Dtos.ExportDataDto;
import com.helinok.pzbad_registration.Dtos.ExportTournamentDataDto;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ICollectDataService {
    ExportDataDto getExportData(Long tournamentId);
}
