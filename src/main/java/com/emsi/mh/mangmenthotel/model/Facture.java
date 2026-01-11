package com.emsi.mh.mangmenthotel.model;

import com.emsi.mh.mangmenthotel.enums.StatutFacture;
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
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numFacture;

    private LocalDate dateEmission;

    private Double montantTotal;

    @Enumerated(EnumType.STRING)
    private StatutFacture statut;

    @OneToOne(mappedBy = "facture")
    private Reservation reservation;
}
