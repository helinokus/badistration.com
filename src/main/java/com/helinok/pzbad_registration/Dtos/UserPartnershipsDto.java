package com.helinok.pzbad_registration.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPartnershipsDto {
    private UserDto user;
    private PartnershipsInfoDto partnershipsInfo;
}
