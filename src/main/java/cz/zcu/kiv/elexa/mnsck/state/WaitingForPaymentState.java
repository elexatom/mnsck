package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

public class WaitingForPaymentState implements BookingState {

    @Override
    public void pay(Booking booking, double amount) {
        // TODO: kontrola zda amount >= cena zajezdu
        booking.setStatus(new PaidState());
    }

    @Override
    public void cancel(Booking booking) {
        booking.setStatus(new CancelledState());
    }

    @Override
    public String getStateName() {
        return "WAITING_FOR_PAYMENT";
    }
}
