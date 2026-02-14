// src/main/java/com/helinok/pzbad_registration/Models/UserTournament.java
package com.helinok.pzbad_registration.Models;

import com.helinok.pzbad_registration.Enums.GameCategories;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import com.helinok.pzbad_registration.Enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_tournament")
public class UserTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "registration_time")
    private LocalDateTime registrationTime = LocalDateTime.now();

    @OneToMany(mappedBy = "userTournament",
            cascade = CascadeType.ALL,
            orphanRemoval = true )
    private Set<RegisteredCategory> registeredCategory = new HashSet<>();


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Column(name = "is_paid")
    private boolean isPaid = false;

    @Column(name = "total_fee")
    private Double totalFee;

    @Column(name = "admin_notes")
    private String adminNotes;

    public Set<GameCategories> getPartnershipCategories() {
        return user.getAllPartnerships().stream()
                .filter(p -> p.getTournament().equals(tournament))           // В этом турнире
                .filter(p -> p.getStatus() == PartnershipStatus.CONFIRMED)   // Подтвержденные
                .filter(p -> getGameCategories().contains(p.getCategory()))           // Есть в моих категориях
                .map(Partnership::getCategory)
                .collect(Collectors.toSet());
    }

    public Set<GameCategories> getIndividualCategories() {
        Set<GameCategories> partnershipCategories = getPartnershipCategories();
        return getGameCategories().stream()
                .filter(cat -> !partnershipCategories.contains(cat))
                .collect(Collectors.toSet());
    }

    public Set<GameCategories> getGameCategories() {
        return registeredCategory.stream()
                .map(RegisteredCategory::getCategory)
                .collect(Collectors.toSet());
    }


    public boolean canAddCategory(GameCategories category) {
        return !getGameCategories().contains(category);
    }

    public void removeCategory(GameCategories category) {
        registeredCategory.removeIf(rc -> rc.getCategory() == category);
    }

    public void addCategory(GameCategories category) {
        if (!canAddCategory(category)) {
            return; // Категория уже существует
        }
        RegisteredCategory registeredCat = new RegisteredCategory();
        registeredCat.setUserTournament(this);
        registeredCat.setCategory(category);
        registeredCategory.add(registeredCat);
    }

    public void addCategories(Set<GameCategories> categories) {
        if (categories == null) {
            return;
        }
        categories.forEach(this::addCategory);
    }
}