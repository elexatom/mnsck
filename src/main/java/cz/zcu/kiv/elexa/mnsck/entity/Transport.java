package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entita reprezentující dopravu (Transport) v systému.
 */
@Getter
@Setter
@Entity
@Table(name = "TRANSPORT")
@NoArgsConstructor
public class Transport {
    /**
    * ID dopravy, generované automaticky a slouží jako primární klíč v databázi.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transport_id;

    /**
     * Typ dopravy (např. letadlo, autobus, vlak).
     */
    private String type;

    /**
     * Detaily o dopravě, které mohou obsahovat delší text s informacemi o dopravě.
     */
    private String details;
}