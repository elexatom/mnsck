package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

public class PartiallyPaidState implements BookingState {

    @Override
    public void pay(Booking booking, double amount) {
        System.out.println("Processing next payment: " + amount);

        if (booking.getAmountPaid() >= booking.getTotal_price()) {
            booking.setStatus(new PaidState());
        }
        // else stay in this state
    }

    @Override
    public void cancel(Booking booking) {
        System.out.println("Cancelled partially paid reservation");
        booking.setStatus(new CancelledState());
    }

    @Override
    public String getStateName() {
        return "PARTIALLY_PAID";
    }
}
