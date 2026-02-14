package com.helinok.pzbad_registration.Controllers.User;

import com.helinok.pzbad_registration.Dtos.ChangePasswordDto;
import com.helinok.pzbad_registration.Dtos.GetAllUsersDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import com.helinok.pzbad_registration.Dtos.UserTournamentProfileDto;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import com.helinok.pzbad_registration.Services.UserService.IUserServiceCache;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;
    private final IUserTournamentService userTournamentService;
    private final IUserServiceCache userServiceCache;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<GetAllUsersDto> users = userService.getAllUsersAsDto();
            return ResponseEntity.ok(new ApiResponse("Lista użytkowników", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }



    @GetMapping("/{id}")
//    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "get by id")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        try {
            UserDto userDto = userService.getUserToDto(id);
            return ResponseEntity.ok(new ApiResponse("Dane użytkownika", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/tournaments")
    @Operation(summary = "get tournaments user registered at")
    public ResponseEntity<ApiResponse> getUserTournaments(@PathVariable Long id) {
        try {
            List<UserTournamentProfileDto> userRegistrations = userTournamentService.getUserRegistrations(id);
            return ResponseEntity.ok(new ApiResponse("Wyniki wyszukiwania", userRegistrations));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "get user profile after auth")
    public ResponseEntity<ApiResponse> getCurrentUserProfile(Authentication authentication) {
        try {
            User user = userService.findUserEntityByEmail(authentication.getName());
            UserDto userDto = userServiceCache.convertToDto(user);
            return ResponseEntity.ok(new ApiResponse("Profil użytkownika", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/profile/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update user profile information")
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestBody User updateData,
            Authentication authentication) {
        try {
            User currentUser = userService.findUserEntityByEmail(authentication.getName());
            User updatedUser = userService.updateUser(currentUser.getId(), updateData);
            UserDto userDto = userServiceCache.convertToDto(updatedUser);
            return ResponseEntity.ok(new ApiResponse("Profil zaktualizowany", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change user password after auth")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestBody ChangePasswordDto passwordDto,
            Authentication authentication) {
        try {

            userService.updatePassword(authentication.getName(), passwordDto);
            return ResponseEntity.ok(new ApiResponse("Hasło zostało zmienione"));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(new ApiResponse(e.getMessage()));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "deactivate user acc")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(new ApiResponse("Użytkownik został dezaktywowany"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }



    @PostMapping("/{id}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get a new role to user")
    public ResponseEntity<ApiResponse> assignRole(
            @PathVariable Long id,
            @RequestParam String roleName) {
        try {
            userService.assignRole(id, roleName);
            return ResponseEntity.ok(new ApiResponse("Rola została przypisana"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }
}