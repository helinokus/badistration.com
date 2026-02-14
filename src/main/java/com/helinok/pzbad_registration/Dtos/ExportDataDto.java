package com.helinok.pzbad_registration.Dtos;


import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportDataDto {
    private TournamentData tournament;
    private List<PlayerData> players;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TournamentData {
        private String name;
        private LocalDateTime date;
        private String location;
        private Set<GameCategories> categories;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerData {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String club;
        private LocalDate birthDate;
        private Set<GameCategories> categories;
    }
}
