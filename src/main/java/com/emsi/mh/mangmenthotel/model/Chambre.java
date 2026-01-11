package com.emsi.mh.mangmenthotel.model;

import com.emsi.mh.mangmenthotel.enums.StatutChambre;
import com.emsi.mh.mangmenthotel.enums.TypeChambre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chambre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numChambre;

    @Enumerated(EnumType.STRING)
    private TypeChambre type;

    private String localisation;

    @Enumerated(EnumType.STRING)
    private StatutChambre statut;

    private Double prixParNuit;
}
