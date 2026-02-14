package com.helinok.pzbad_registration.Services.UserService;

import com.helinok.pzbad_registration.Dtos.*;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Models.Role;
import com.helinok.pzbad_registration.Repositories.UserRepository;
import com.helinok.pzbad_registration.Repositories.RoleRepository;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserServiceCache userServiceCache;

    @Override
    public UserDto createUser(RegisterDto dto) {
        boolean existsByEmail = existsByEmail(dto.getEmail());

        if (existsByEmail) {
            throw new ConflictException("User with email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(dto.getBirthDate());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setTeamName(dto.getTeamName());
        user.setActive(true);

        log.info("Created user: {}", user.getEmail());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("Role USER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User save = userRepository.save(user);
        userServiceCache.evictAllUsersCaches();
        userServiceCache.getCacheAllUsersAdminDtos();
        userServiceCache.getCacheAllUsersDto();

        UserDto userDto = userServiceCache.convertToDto(save);
        userServiceCache.createUserCache(userDto);
        return userDto;
    }


    @Override
    public UserDto findById(Long id) {
        return userServiceCache.getUserDtoById(id);
    }


    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }


    @Override
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setTeamName(updatedUser.getTeamName());
        user.setBirthDate(updatedUser.getBirthDate());

        User save = userRepository.save(user);

        //Work with caching
        userServiceCache.evictAllUsersCaches();
        userServiceCache.getCacheAllUsersAdminDtos();
        userServiceCache.getCacheAllUsersDto();
        userServiceCache.evictUserDtoById(id);
        userServiceCache.getUserDtoById(id);

        return save;
    }

    @Override
    public void updatePassword(String userEmail, ChangePasswordDto passwordDto) {
        User user = findUserEntityByEmail(userEmail);

        isPasswordMatch(passwordDto.getCurrentPassword(), user);
        System.out.println("Password: " + passwordDto.getCurrentPassword());

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }

//    @CacheEvict(value = "USER_CACHE_BY_ID", key = "#id")
    @Override
    public void deactivateUser(Long id) {
        User user = findUserEntityById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void assignRole(Long userId, String roleName) {
        User user = findUserEntityById(userId);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));

        if (Objects.equals(roleName, "ROLE_ADMIN")){
            throw new ConflictException("Role administrator cannot be assigned to user");
        }

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void deleteRole(Long userId, String roleName) {
        User user = findUserEntityById(userId);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
        if (!user.getRoles().contains(role)) {
            throw new ConflictException("User dont has this role");
        }
        if (Objects.equals(roleName, "ROLE_ADMIN") || Objects.equals(roleName, "ROLE_USER")) {
            throw new ConflictException("This category cant be removed");
        }

        user.getRoles().remove(role);
        userRepository.save(user);
    }


    @Override
    public List<GetAllUsersDto> getAllUsersAsDto() {
        return userServiceCache.getCacheAllUsersDto();
    }

    @Override
    public List<GetAllUsersAdminDto> getAllUsersAdminDtos() {
        return userServiceCache.getCacheAllUsersAdminDtos();
    }

    @Override
    public List<GetAllUsersAdminDto> searchUsersAdmin(String query) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::convertToAllUsersAdminDto)
                .collect(Collectors.toList());
    }


    @Override
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public void isPasswordMatch(String password, User user){
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ConflictException("Aktualne hasło nie jest prawidłowe");
        }
    }

    @Override
    public UserDto getUserToDto(Long id) {
        return userServiceCache.getUserDtoById(id);
    }

    @Override
    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
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
}