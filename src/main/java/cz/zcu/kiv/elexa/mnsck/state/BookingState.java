package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

public interface BookingState {
    void pay(Booking booking, double amount);

    void cancel(Booking booking);

    String getStateName();
}
