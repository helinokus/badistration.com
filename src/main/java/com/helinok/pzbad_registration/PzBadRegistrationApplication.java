package com.helinok.pzbad_registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableJpaRepositories
public class PzBadRegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PzBadRegistrationApplication.class, args);
    }

}
