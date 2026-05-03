package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

/**
 * Stav rezervace, který reprezentuje zrušenou rezervaci. Implementuje rozhraní BookingState a definuje chování pro akce platby a zrušení.
 * 
 * @author Tomáš Elexa
 */
public class CancelledState implements BookingState {

    /**
     * Metoda pro provedení platby za rezervaci. V tomto stavu není možné provést platbu, proto je vyhozena výjimka IllegalStateException.
     */
    @Override
    public void pay(Booking booking, double amount) {
        throw new IllegalStateException("Chyba: Nelze platit stornovanou rezervaci.");
    }

    /**
     * Metoda pro zrušení rezervace. V tomto stavu není možné zrušit rezervaci, protože již je stornována, proto je vyhozena výjimka IllegalStateException.
     */
    @Override
    public void cancel(Booking booking) {
        throw new IllegalStateException("Chyba: Rezervace je již stornována.");
    }

    /**
     * Vrací název stavu rezervace.
     * 
     * @return Název stavu rezervace "CANCELLED".
     */
    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}
