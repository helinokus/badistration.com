// src/main/java/com/helinok/pzbad_registration/Models/Tournament.java
package com.helinok.pzbad_registration.Models;

import com.helinok.pzbad_registration.Enums.GameCategories;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_tournament", nullable = false)
    private String nameTournament;

    @Column(name = "date_of_tournament", nullable = false)
    private LocalDateTime dateOfTournament;

    @Column(name = "registration_expired", nullable = false)
    private LocalDateTime registrationExpired;

    @Column(name = "url_tournament")
    private String urlTournament;

    @Column(name = "max_categories_to_play")
    private int maxCategoriesToPlay;

    @Column(name = "location")
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_players")
    private Integer maxPlayers;

    @Column(name = "entry_fee")
    private String entryFee;

    @Column(name = "registration_closed")
    private boolean isClosed;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentCategoryPrice> categoryPrices = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "tournament_categories",
            joinColumns = @JoinColumn(name = "tournament_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<GameCategories> categories = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<UserTournament> userTournaments = new ArrayList<>();

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creatorOfTournament;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private Set<Partnership> partnerships = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tournament_moderators",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )


    private Set<User> moderators = new HashSet<>();

}