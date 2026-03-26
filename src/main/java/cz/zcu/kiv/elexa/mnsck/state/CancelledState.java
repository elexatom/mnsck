package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

public class CancelledState implements BookingState {

    @Override
    public void pay(Booking booking, double amount) {
        throw new IllegalStateException("Chyba: Nelze platit stornovanou rezervaci.");
    }

    @Override
    public void cancel(Booking booking) {
        throw new IllegalStateException("Chyba: Rezervace je již stornována.");
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}
