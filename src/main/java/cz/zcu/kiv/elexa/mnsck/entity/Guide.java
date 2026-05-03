package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entita reprezentující průvodce (Guide) v systému.
 * 
 * @author Tomáš Elexa
 */
@Entity
@Table(name = "GUIDE")
@Getter
@Setter
@NoArgsConstructor
public class Guide {
    /**
     * ID průvodce, generované automaticky a slouží jako primární klíč v databázi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guide_id;

    /**
     * Jméno průvodce.
     */
    private String name;

    /**
     * Příjmení průvodce.
     */
    private String surname;

    /**
     * Jazyk, kterým průvodce mluví (první jazyk). Propojeno s entitou Language pomocí vztahu ManyToOne.
     */
    @ManyToOne
    @JoinColumn(name = "language_id_1")
    private Language language1;

    /**
     * Jazyk, kterým průvodce mluví (druhý jazyk). Propojeno s entitou Language pomocí vztahu ManyToOne. Tento jazyk je volitelný, průvodce může mít pouze jeden jazyk.
     */
    @ManyToOne
    @JoinColumn(name = "language_id_2")
    private Language language2;
}