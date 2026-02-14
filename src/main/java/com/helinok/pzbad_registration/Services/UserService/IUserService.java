package com.helinok.pzbad_registration.Services.UserService;

import com.helinok.pzbad_registration.Dtos.*;
import com.helinok.pzbad_registration.Models.User;

import java.util.List;
import java.util.Map;

public interface IUserService {
    UserDto findById(Long id);
    UserDto createUser(RegisterDto dto);
    boolean existsByEmail(String email);
    List<User> getAllActiveUsers();
    User updateUser(Long id, User updatedUser);
    void updatePassword(String userEmail, ChangePasswordDto passwordDto);
    void deactivateUser(Long id);
    void assignRole(Long userId, String roleName);
    void deleteRole(Long userId, String roleName);
    List<GetAllUsersDto> getAllUsersAsDto();

    List<GetAllUsersAdminDto> getAllUsersAdminDtos();

    List<GetAllUsersAdminDto> searchUsersAdmin(String query);

    void toggleUserStatus(Long userId);

    void isPasswordMatch(String password, User user);

    UserDto getUserToDto(Long id);

    User findUserEntityById(Long id);

    User findUserEntityByEmail(String email);
}