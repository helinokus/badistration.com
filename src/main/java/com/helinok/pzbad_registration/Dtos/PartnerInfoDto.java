package com.helinok.pzbad_registration.Dtos;

import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerInfoDto {
    private Long partnerId;
    private String partnerFirstName;
    private String partnerLastName;
    private String partnerTeamName;
    private PartnershipStatus partnershipStatus;

    public String getPartnerFullName() {
        return partnerFirstName + " " + partnerLastName;
    }
}