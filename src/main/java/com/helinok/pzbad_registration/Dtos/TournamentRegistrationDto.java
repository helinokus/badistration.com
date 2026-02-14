package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentRegistrationDto {
    private Long tournamentId;
    private Set<GameCategories> categories;
    private List<DoublesRegistrationDto> doubles = new ArrayList<>();
}