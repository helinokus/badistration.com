package com.helinok.pzbad_registration.Models;

import com.helinok.pzbad_registration.Enums.GameCategories;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registered_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_tournament_id", "game_category"}))
public class RegisteredCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserTournament userTournament;

    @Column(name = "game_category")
    @Enumerated(EnumType.STRING)
    private GameCategories category;

    private boolean is_downloaded = false;

}
