package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AddNewCategoryDto {
    private List<GameCategories> categories;
    private Map<GameCategories, List<Long>> mapDoubledCategoriesToUsers;
    private Long initiatorUserTournamentId;
}
