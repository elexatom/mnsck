package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entita reprezentující destinaci (Destination) v systému.
 * 
 * @author Tomáš Elexa
 */
@Entity
@Table(name = "DESTINATION")
@Getter
@Setter
@NoArgsConstructor
public class Destination {
    /**
     * ID destinace, generované automaticky a slouží jako primární klíč v databázi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destination_id;

    /**
    * Název destinace.
    */
    private String destination_name;
    
    /**
     * Země destinace.
     */
    private String destination_country;

    /**
     * Popis destinace, který může obsahovat delší text s informacemi. 
     */
    @Column(columnDefinition = "TEXT")
    private String destination_description;
}