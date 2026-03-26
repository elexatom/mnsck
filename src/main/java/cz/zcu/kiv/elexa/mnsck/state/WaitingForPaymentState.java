package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

public class WaitingForPaymentState implements BookingState {

    @Override
    public void pay(Booking booking, double amount) {
        if (booking.getAmountPaid() >= booking.getTotal_price()) {
            booking.setStatus(new PaidState());
        } else {
            booking.setStatus(new PartiallyPaidState());
        }
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
