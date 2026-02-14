package com.helinok.pzbad_registration.Controllers.User;

import com.helinok.pzbad_registration.Dtos.GetAllUsersDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import com.helinok.pzbad_registration.Dtos.UserTournamentProfileDto;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import com.helinok.pzbad_registration.Services.UserTournamentService.IUserTournamentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    IUserService userService;

    @InjectMocks
    UserController userController;

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        // Arrange
        var user1 = new GetAllUsersDto();
        user1.setId(1L);
        user1.setEmail("user1@test.com");

        var user2 = new GetAllUsersDto();
        user2.setId(2L);
        user2.setEmail("user2@test.com");

        List<GetAllUsersDto> mockUsers = List.of(user1, user2);

        // Mock service
        when(userService.getAllUsersAsDto()).thenReturn(mockUsers);

        // Act
        var response = userController.getAllUsers();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Lista użytkowników", response.getBody().getMessage());

        assertNotNull(response.getBody().getData());
        @SuppressWarnings("unchecked")
        List<GetAllUsersDto> returnedUsers = (List<GetAllUsersDto>) response.getBody().getData();
        assertEquals(2, returnedUsers.size());
        assertEquals("user1@test.com", returnedUsers.get(0).getEmail());
        assertEquals("user2@test.com", returnedUsers.get(1).getEmail());

        verify(userService).getAllUsersAsDto();
    }

    @Test
    void getAllUsers_ServiceThrowsException_Returns400() {
        // Arrange
        String errorMessage = "Database connection failed";

        // Mock service
        when(userService.getAllUsersAsDto())
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        var response = userController.getAllUsers();

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());

        verify(userService).getAllUsersAsDto();
    }

    @Test
    void getUserById_ReturnsUser() {
        //Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user1@test.com");

        //Mock service
        when(userService.getUserToDto(1L)).thenReturn(userDto);

        //Act
        var response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Dane użytkownika", response.getBody().getMessage());
        UserDto user = (UserDto) response.getBody().getData();
        assertEquals("user1@test.com", user.getEmail());
        verify(userService).getUserToDto(1L);

    }



}