package com.emsi.mh.mangmenthotel.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "gestionnaire")
@EqualsAndHashCode(callSuper = true)
public class Gestionnaire extends Personne {

    @OneToMany(mappedBy = "gestionnaire")
    @ToString.Exclude
    private List<Plainte> plaintesTraitees = new ArrayList<>();
}
