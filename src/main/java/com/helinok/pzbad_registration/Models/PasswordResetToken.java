package com.helinok.pzbad_registration.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String code;
    private String email;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
    private boolean used  = false;
    private int attempts = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}


