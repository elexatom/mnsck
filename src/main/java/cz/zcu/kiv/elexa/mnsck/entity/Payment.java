package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entita reprezentující platbu (Payment) v systému. Platba je spojena s rezervací (Booking) a obsahuje informace o částce a datu platby.
 *
 * @author Tomáš Elexa
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    /**
     * ID platby, generované automaticky a slouží jako primární klíč v databázi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Částka platby.
     */
    private Double amount;
    /**
     * Datum platby.
     */
    private LocalDate paymentDate;

    /**
     * Rezervace, ke které platba patří. Propojeno s entitou Booking pomocí vztahu ManyToOne. Platba musí být vždy spojena s existující rezervací.
     */
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
