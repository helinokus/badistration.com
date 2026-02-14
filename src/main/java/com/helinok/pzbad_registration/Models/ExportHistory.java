package com.helinok.pzbad_registration.Models;

import com.helinok.pzbad_registration.Enums.ExportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "export_history")
@Builder
public class ExportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stamp_of_export")
    private LocalDateTime stampOfExport = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "export_type", nullable = false)
    private ExportType exportType;

    @Column(name = "export_count")
    private Long exportCount;

    @Column(name = "notes")
    private String notes;


}
