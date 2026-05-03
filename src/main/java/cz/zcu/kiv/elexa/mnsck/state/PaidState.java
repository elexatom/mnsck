package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

/**
 * Stav rezervace, který reprezentuje uhrazenou rezervaci. Implementuje rozhraní BookingState a definuje chování pro akce platby a zrušení.
 * 
 * @author Tomáš Elexa
 */
public class PaidState implements BookingState {

    /**
     * Metoda pro provedení platby za rezervaci. V tomto stavu není možné provést platbu, proto je vyhozena výjimka IllegalStateException.
     */
    @Override
    public void pay(Booking booking, double amount) {
        throw new IllegalStateException("Chyba: Tato rezervace je již uhrazená.");
    }

    /**
     * Metoda pro zrušení rezervace. V tomto stavu je možné zrušit rezervaci, ale bude nutné vrátit peníze klientovi, proto se stav rezervace změní na CancelledState a vypíše se informace o vrácení peněz.
     */
    @Override
    public void cancel(Booking booking) {
        System.out.println("Stornuji zaplacenou rezervaci. Bude nutné vrátit peníze klientovi!");
        booking.setStatus(new CancelledState());
    }

    /**
     * Vrací název stavu rezervace.
     *
     * @return Název stavu rezervace "PAID".
     */
    @Override
    public String getStateName() {
        return "PAID";
    }
}
