package com.emsi.mh.mangmenthotel.model;

import com.emsi.mh.mangmenthotel.enums.StatutPlainte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plainte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business ID
    private String idPlainte;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private StatutPlainte statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "gestionnaire_id")
    private Gestionnaire gestionnaire;
}
