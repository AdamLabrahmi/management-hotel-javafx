package com.emsi.mh.mangmenthotel.model;

import com.emsi.mh.mangmenthotel.enums.StatutReservation;
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
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Separate business ID if specific format needed
    private String idReservation;

    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private int nombrePersonnes;
    private Double montantTotal;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chambre_id")
    private Chambre chambre;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facture_id", referencedColumnName = "id")
    private Facture facture;
}
