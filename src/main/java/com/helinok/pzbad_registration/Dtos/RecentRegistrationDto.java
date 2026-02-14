package com.helinok.pzbad_registration.Dtos;


import com.helinok.pzbad_registration.Enums.GameCategories;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentRegistrationDto {
    private String id;
    private String userName;
    private String partnerName;
    private GameCategories category;
    private Long userId;
    private LocalDateTime registrationTime;
    private Long userTournamentId;

    @Override
    public String toString() {
        return "RecentRegistrationDto{" +
                "id='" + id + '\'' +
                "userName='" + userName + '\'' +
                ", partnerName='" + partnerName + '\'' +
                ", category=" + category +
                ", userId=" + userId +
                ", registrationTime=" + registrationTime +
                ", userTournamentId=" + userTournamentId +
                "} \n";
    }
}
