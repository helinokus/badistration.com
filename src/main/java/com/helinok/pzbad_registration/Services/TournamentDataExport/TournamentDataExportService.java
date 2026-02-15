package com.helinok.pzbad_registration.Services.TournamentDataExport;

import com.helinok.pzbad_registration.Dtos.RecentRegistrationDto;
import com.helinok.pzbad_registration.Enums.ExportType;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Facades.TournamentRegistrationFacade.ITournamentRegistrationFacade;
import com.helinok.pzbad_registration.Models.ExportHistory;
import com.helinok.pzbad_registration.Models.Tournament;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Repositories.ExportHistoryRepository;
import com.helinok.pzbad_registration.Services.TournamentService.ITournamentService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDataExportService implements ITournamentDataExportService {

    private final ITournamentService tournamentService;
    private final IUserService userService;
    private final ITournamentRegistrationFacade tournamentRegistrationFacade;


    private final ExportHistoryRepository exportHistoryRepository;


    @Transactional
    @Override
    public byte[] createFullExcelResult(Long tournamentId, String userEmail) {
        log.info("Step 1: Starting createFullExcelResult for tournament {} and user {}", tournamentId, userEmail);

        User user = userService.findUserEntityByEmail(userEmail);
        log.info("Step 2: Found user: {}", user.getEmail());

        Tournament tournament = tournamentService.findEntityById(tournamentId);
        log.info("Step 3: Found tournament: {}", tournament.getNameTournament());

        boolean isCreator = tournament.getCreatorOfTournament().equals(user);
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isCreator && !isAdmin) {
            log.error("Step 4: User {} is not the creator of tournament {} and not an admin", userEmail, tournamentId);
            throw new ConflictException("Nie jesteś kreatorem tego turnieju!");
        }
        log.info("Step 4: User has permissions (Creator: {}, Admin: {}) - OK", isCreator, isAdmin);

        log.info("Step 5: Calling getAllRegistrations...");
        List<RecentRegistrationDto> registrations = tournamentRegistrationFacade.getAllRegistrations(tournamentId);
        log.info("Step 6: Got {} registrations", registrations.size());

        List<PlayerTournamentRegistrationDto> playersInfo = tournamentRegistrationFacade.getAllPlayersInfo(tournamentId);


        log.info("Step 7: Generating Excel file...");
        byte[] file = generateExcelFile(tournamentId, registrations, playersInfo);
        log.info("Step 8: Excel file generated, size: {} bytes", file.length);

        log.info("Step 9: Saving export history...");
        saveExportHistory(tournament, user, ExportType.FULL, (long) registrations.size());
        log.info("Step 10: Export history saved - SUCCESS");

        return file;

    }



    private byte[] generateExcelFile(Long tournamentId, List<RecentRegistrationDto> data,  List<PlayerTournamentRegistrationDto> playersInfo) {
        try {
            String fileName = "TournamentDataExport_" + tournamentId + "_" + LocalDateTime.now() + ".xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("TournamentDataExport");
            workbook.setSheetName(0, fileName);

            setColumnWidths(sheet);
            createHeaderRow(sheet, workbook);
            createContentRowFromTournament(sheet, tournamentId);
            createContentRowsFromRecentRegistration(sheet, data);

            Sheet players = workbook.createSheet("Players");
            setColumnWidths(players);
            workbook.setSheetName(1, "players");


            createContentRowsFromAllPlayers(players, playersInfo);

            byte[] result = convertToBytes(workbook);

            workbook.close();
            log.info("Excel file generated for tournament {}: {} records", tournamentId, data.size());
            return result;
        }
        catch (Exception e){
            log.error("Failed to generate Excel for tournament {}", tournamentId, e);
            throw new ConflictException("Nie udało się utworzyć pliku Excel, " + e.getMessage());
        }
    }


    private void setColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 25 * 256);
        sheet.setColumnWidth(2, 25 * 256);
        sheet.setColumnWidth(3, 25 * 256);
        sheet.setColumnWidth(4, 25 * 256);
    }

    private void createHeaderRow(Sheet sheet, Workbook workbook) {


        Row tournamentHeaderRow = sheet.createRow(0);
        Row headerRowParticipants = sheet.createRow(2);

        CellStyle headerStyle = createHeaderStyle(workbook);

        createHeaderCell(tournamentHeaderRow, 0, "Id turnieju", headerStyle);
        createHeaderCell(tournamentHeaderRow, 1, "Nazwa turnieju", headerStyle);
        createHeaderCell(tournamentHeaderRow, 2, "Organizator", headerStyle);
        createHeaderCell(tournamentHeaderRow, 3, "Miejsce", headerStyle);
        createHeaderCell(tournamentHeaderRow, 4, "Contact", headerStyle);

        createHeaderCell(headerRowParticipants, 0, "Imię", headerStyle);
        createHeaderCell(headerRowParticipants, 1, "Nazwisko", headerStyle);
        createHeaderCell(headerRowParticipants, 2, "Kategoria", headerStyle);
        createHeaderCell(headerRowParticipants, 3, "Imię Partnera", headerStyle);
        createHeaderCell(headerRowParticipants, 4, "Nazwisko Partnera", headerStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private void createHeaderCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }


    private void saveExportHistory(Tournament tournament, User user, ExportType exportType, Long exportCount) {
        exportHistoryRepository.save(ExportHistory.builder()
                .tournament(tournament)
                .user(user)
                .exportType(exportType)
                .stampOfExport(LocalDateTime.now())
                .exportCount(exportCount)
                .build());

    }

    private void createContentRowFromTournament(Sheet sheet, Long tournamentId) {
        int rowNum = 1;
        Row tournamentHeaderRow = sheet.createRow(rowNum);
        Tournament tournament = tournamentService.findEntityById(tournamentId);

        tournamentHeaderRow.createCell(0).setCellValue(tournament.getId());
        tournamentHeaderRow.createCell(1).setCellValue(tournament.getNameTournament());
        tournamentHeaderRow.createCell(2).setCellValue(tournament.getCreatorOfTournament().getFullName());
        tournamentHeaderRow.createCell(3).setCellValue(tournament.getLocation());
        tournamentHeaderRow.createCell(4).setCellValue(tournament.getCreatorOfTournament().getEmail());


    }


    private void createContentRowsFromRecentRegistration(Sheet sheet, List<RecentRegistrationDto> data) {
        int rowNum = 3;

        for (RecentRegistrationDto dto : data) {
            Row row = sheet.createRow(rowNum++);

            String[] nameParts = dto.getUserName().split(" ", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            String partnerFirstName = "";
            String partnerLastName = "";
            if (dto.getPartnerName() != null && !dto.getPartnerName().isEmpty()) {
                String[] partnerParts = dto.getPartnerName().split(" ", 2);
                partnerFirstName = partnerParts.length > 0 ? partnerParts[0] : "";
                partnerLastName = partnerParts.length > 1 ? partnerParts[1] : "";
            }

            row.createCell(0).setCellValue(firstName);
            row.createCell(1).setCellValue(lastName);
            row.createCell(2).setCellValue(dto.getCategory().toString());
            row.createCell(3).setCellValue(partnerFirstName);
            row.createCell(4).setCellValue(partnerLastName);
        }
    }

    private void createContentRowsFromAllPlayers(Sheet sheet, List<PlayerTournamentRegistrationDto> data) {
        int rowNum = 3;

        for (PlayerTournamentRegistrationDto dto : data) {
            Row row = sheet.createRow(rowNum++);


            row.createCell(0).setCellValue(dto.getPlayerId());
            row.createCell(1).setCellValue(dto.getFullName());
            row.createCell(2).setCellValue(dto.getEmail());
            row.createCell(3).setCellValue(dto.getCategories().toString());
            row.createCell(4).setCellValue(dto.getAmount().toString());
        }
    }


    private byte[] convertToBytes(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }





}
