package com.helinok.pzbad_registration.Services.UserService;

import com.helinok.pzbad_registration.Dtos.GetAllUsersAdminDto;
import com.helinok.pzbad_registration.Dtos.GetAllUsersDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import com.helinok.pzbad_registration.Models.User;


import java.util.List;

public interface IUserServiceCache {

    UserDto getUserDtoById(Long id);

    void evictUserDtoById(Long id);

    void evictUserCache(Long id, String email);

    void evictAllUsersCaches();

    List<GetAllUsersDto> getCacheAllUsersDto();
    void createUserCache(UserDto userDto);

    List<GetAllUsersAdminDto> getCacheAllUsersAdminDtos();

    UserDto convertToDto(User user);

}
