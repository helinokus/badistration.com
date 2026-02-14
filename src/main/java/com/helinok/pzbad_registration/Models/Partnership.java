// src/main/java/com/helinok/pzbad_registration/Models/Partnership.java
package com.helinok.pzbad_registration.Models;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "partnership")
public class Partnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GameCategories category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1; // Инициатор приглашения

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id", nullable = false)
    private User player2; // Приглашенный игрок

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PartnershipStatus status = PartnershipStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;


    public User getPartnerOf(User user) {
        if (player1.equals(user)) return player2;
        if (player2.equals(user)) return player1;
        throw new IllegalArgumentException("User is not part of this partnership");
    }

    public boolean involves(User user) {
        return player1.equals(user) || player2.equals(user);
    }

    public User getInitiator() {
        return player1;
    }

    public boolean isConfirmed() {
        return status == PartnershipStatus.CONFIRMED;
    }

    public boolean isPending() {
        return status == PartnershipStatus.PENDING;
    }
}