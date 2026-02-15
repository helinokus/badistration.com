package com.helinok.pzbad_registration.Controllers.Auth;

import com.helinok.pzbad_registration.Dtos.RegisterDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import com.helinok.pzbad_registration.Exceptions.ConflictException;
import com.helinok.pzbad_registration.Exceptions.NotFoundException;
import com.helinok.pzbad_registration.Models.User;
import com.helinok.pzbad_registration.Requests.AuthenticationRequest;
import com.helinok.pzbad_registration.Responses.ApiResponse;
import com.helinok.pzbad_registration.Services.Security.JwtService;
import com.helinok.pzbad_registration.Services.UserService.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserService userService;

    @Value("${server.servlet.session.cookie.secure}")
    private boolean cookieSecure;

    @Value("${server.servlet.session.cookie.same-site}")
    private String cookieSameSite;


    @PostMapping("/login")
    @Operation(summary = "login request")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody AuthenticationRequest request,
                                                               HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        if (authentication.isAuthenticated()) {
            String jwt = jwtService.generateToken(request.getEmail());
            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .path("/")
                    .maxAge(Duration.ofHours(3))
                    .sameSite(cookieSameSite)
                    .domain(".badistration.com")
                    .build();
            response.setHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok(Map.of("message", "Successfully logged in"));
        }
        throw new UsernameNotFoundException("invalid credentials");
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .domain(".badistration.com")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/register")
    @Operation(summary = "register a new account")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterDto request) {
        try {
            log.info("создаю нового юзера");
            UserDto newUser = userService.createUser(request);
            log.info("создал!");
            return ResponseEntity.ok().body(ApiResponse.builder()
                            .message("Successfully registered")
                            .build());
        } catch (ConflictException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Info about me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().build();
        }


        User user = userService.findUserEntityByEmail(authentication.getName());

        Map<String, String> response = new HashMap<>();
        response.put("id", user.getId().toString());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("teamName", user.getTeamName());
        return ResponseEntity.ok(response);
    }

}
