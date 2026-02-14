package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnershipInitiatorDto {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private GameCategories category;
    private PartnershipStatus status;

    // Информация о партнере (не о текущем пользователе)
    private Long partnerId;
    private String partnerName;
    private String partnerEmail;

    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    // Для UI
    private boolean canAccept;    // может ли текущий пользователь принять
    private boolean canDecline;   // может ли отклонить
    private boolean canCancel;    // может ли отменить
}
