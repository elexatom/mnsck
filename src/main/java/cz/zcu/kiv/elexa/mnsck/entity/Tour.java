package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entita reprezentující zájezd (Tour) v systému.
 * 
 * @author Tomáš Elexa
 */
@Entity
@Table(name = "TOURS")
@Getter
@Setter
@NoArgsConstructor
public class Tour {
    /**
     * ID zájezdu, generované automaticky a slouží jako primární klíč v databázi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tour_id;

    /**
     * Název zájezdu.
     */
    private String tour_name;

    /**
     * Datum zahájení zájezdu.
     */
    private LocalDate start_date;
    
    /**
     * Datum ukončení zájezdu.
     */
    private LocalDate end_date;

    /**
     * Cena za osobu bez slevy.
     */
    private Double price_person;
    
    /**
     * Cena za osobu se slevou.
     */
    private Double price_person_reduced;
    
    /**
     * Kapacita zájezdu.
     */
    private Integer capacity;

    /**
     * Transport spojený se zájezdem.
     */
    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;

    /**
     * Cíl zájezdu.
     */
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    /**
     * Průvodce zájezdu.
     */
    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
