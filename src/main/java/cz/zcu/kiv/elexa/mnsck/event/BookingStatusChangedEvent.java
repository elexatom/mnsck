package cz.zcu.kiv.elexa.mnsck.event;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Událost, která se spustí při změně stavu rezervace. Obsahuje informace o rezervaci, starém stavu a novém stavu. Implementuje Observer pattern.
 * 
 * @author Tomáš Elexa
 */
@Getter
public class BookingStatusChangedEvent extends ApplicationEvent {
    /**
     * Rezervace, jejíž stav se změnil.
     */
    private final Booking booking;

    /**
     * Starý stav rezervace.
     */
    private final String oldStatus;

    /**
     * Nový stav rezervace.
     */
    private final String newStatus;

    /**
     * Vytvoří novou událost změny stavu rezervace.
     *
     * @param source    zdroj události
     * @param booking   rezervace, jejíž stav se změnil
     * @param oldStatus starý stav rezervace
     * @param newStatus nový stav rezervace
     */
    public BookingStatusChangedEvent(Object source, Booking booking, String oldStatus, String newStatus) {
        super(source);
        this.booking = booking;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

}
