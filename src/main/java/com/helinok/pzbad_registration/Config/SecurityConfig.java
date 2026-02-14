package com.helinok.pzbad_registration.Config;

import com.helinok.pzbad_registration.Services.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // ALL PUBLIC
                        .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/mail-sender/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()


                        .requestMatchers("/api/partnerships/**").authenticated()

                        // PUBLIC TO TOURNAMENTS
                        .requestMatchers(HttpMethod.GET, "/api/tournaments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tournaments/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tournaments/upcoming").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tournaments/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tournaments/{id}").permitAll()

                        // PUBLIC TO USERS
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}/tournaments").permitAll()

                        // ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // MODERATORS AND ADMINS
                        .requestMatchers(HttpMethod.POST, "/api/tournaments").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tournaments/**").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tournaments/**").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers("/api/tournaments/my-tournaments").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers("/api/tournaments/{id}/export-data").hasAnyRole("MODERATOR", "ADMIN")

                        // INTERACT USER WITH TOURNAMENTS
                        .requestMatchers("/api/user-tournaments/tournament/**").permitAll()

                        // INTERACT USER WITH PROFILE
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                        .requestMatchers("/api/users/change-password").authenticated()

                        // UPDATE USERS BY ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers("/api/users/{id}/assign-role").hasRole("ADMIN")

                        // SWAGGER
                        .requestMatchers("/api/exportData/**").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()



                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8888",
                "https://badistration.com", "https://www.badistration.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
