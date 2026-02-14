package com.helinok.pzbad_registration.Models;

import com.helinok.pzbad_registration.Enums.GameCategories;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tournament_category_prices")
public class TournamentCategoryPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GameCategories category;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}