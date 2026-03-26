package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

public class PaidState implements BookingState {

    @Override
    public void pay(Booking booking, double amount) {
        throw new IllegalStateException("Chyba: Tato rezervace je již uhrazená.");
    }

    @Override
    public void cancel(Booking booking) {
        System.out.println("Stornuji zaplacenou rezervaci. Bude nutné vrátit peníze klientovi!");
        booking.setStatus(new CancelledState());
    }

    @Override
    public String getStateName() {
        return "PAID";
    }
}
