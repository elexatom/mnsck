package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

/**
 * Stav rezervace, který definuje chování pro různé akce (platba, zrušení) v závislosti na aktuálním stavu rezervace. Implementuje state pattern.
 * 
 * @author Tomáš Elexa
 */
public interface BookingState {
    /**
     * Metoda pro provedení platby za rezervaci. Implementace této metody bude záviset na konkrétním stavu rezervace.
     *
     * @param booking Booking, pro kterou se provede platba
     * @param amount Částka, která má být zaplacena
     */
    void pay(Booking booking, double amount);

    /**
     * Metoda pro zrušení rezervace. Implementace této metody bude záviset na konkrétním stavu rezervace.
     *
     * @param booking Booking, který má být stornován
     */
    void cancel(Booking booking);

    /**
     * Vrací název stavu rezervace.
     *
     * @return Název stavu rezervace.
     */
    String getStateName();
}
