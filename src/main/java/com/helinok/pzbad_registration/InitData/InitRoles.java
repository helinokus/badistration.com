package com.helinok.pzbad_registration.InitData;

import com.helinok.pzbad_registration.Models.Role;
import com.helinok.pzbad_registration.Repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitRoles implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        initializeRoles();
    }

    private void initializeRoles() {
        List<String> roles = List.of("ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN");
        log.info("WE ARE HERE!");

        for (String roleName : roles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            } else {
                log.debug("Role already exists: {}", roleName);
            }
        }
    }
}
