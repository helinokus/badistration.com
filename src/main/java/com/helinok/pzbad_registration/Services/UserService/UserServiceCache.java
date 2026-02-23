package com.helinok.pzbad_registration.Services.UserService;

import com.helinok.pzbad_registration.Dtos.GetAllUsersAdminDto;
import com.helinok.pzbad_registration.Dtos.GetAllUsersDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.Role;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceCache implements IUserServiceCache {

    private final UserRepository userRepository;

    @Cacheable(value = "users:dto", key = "#id")
    @Override
    public UserDto getUserDtoById(Long id) {
        log.info("getUserDtoById {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
       return convertToDto(user);
    }

    @CacheEvict(value = "users:dto", key = "#id")
    @Override
    public void evictUserDtoById(Long id){
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "users:dto", key = "#id"),
                    @CacheEvict(value = "users:dto", key = "#email")
            }
    )
    @Override
    public void evictUserCache(Long id, String email) {
    }


    @CacheEvict(value = {"users:listAll", "users:listAllAdmin"})
    @Override
    public void evictAllUsersCaches(){

    }

    @Cacheable(value = "users:listAll", key = "'all-users'")
    @Override
    public List<GetAllUsersDto> getCacheAllUsersDto(){
        return userRepository.findAll().stream()
                .map(this::convertToAllUsersDto)
                .collect(Collectors.toList());
    }

    @CachePut(value = "users:dto", key = "#userDto.id")
    @Override
    public UserDto createUserCache(UserDto userDto) {
        return userDto;
    }

    @Cacheable(value = "users:listAllAdmin", key = "'all-users-admin'")
    @Override
    public List<GetAllUsersAdminDto> getCacheAllUsersAdminDtos() {
        return userRepository.findAll().stream()
                .map(this::convertToAllUsersAdminDto)
                .collect(Collectors.toList());
    }

    private GetAllUsersAdminDto convertToAllUsersAdminDto(User user) {
        return GetAllUsersAdminDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .teamName(user.getTeamName())
                .birthDate(user.getBirthDate())
                .registrationTime(user.getRegistrationDate())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .status(user.isActive())
                .build();
    }



    private GetAllUsersDto convertToAllUsersDto(User user) {
        return GetAllUsersDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .teamName(user.getTeamName())
                .birthDate(user.getBirthDate())
                .build();
    }


    public UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .teamName(user.getTeamName())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .isActive(user.isActive())
                .registrationDate(user.getRegistrationDate())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

}
