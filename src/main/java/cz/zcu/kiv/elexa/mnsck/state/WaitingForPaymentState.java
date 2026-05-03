package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;

/**
 * Stav rezervace, který reprezentuje rezervaci čekající na platbu. Implementuje rozhraní BookingState a definuje chování pro akce platby a zrušení.
 * 
 * @author Tomáš Elexa
 */
public class WaitingForPaymentState implements BookingState {

    /**
     * Metoda pro provedení platby za rezervaci. V tomto stavu je možné provést platbu, ale rezervace zůstane v tomto stavu, dokud není uhrazena celková částka.
     */
    @Override
    public void pay(Booking booking, double amount) {
        if (booking.getAmountPaid() >= booking.getTotal_price()) {
            booking.setStatus(new PaidState());
        } else {
            booking.setStatus(new PartiallyPaidState());
        }
    }

    /**
     * Metoda pro zrušení rezervace. V tomto stavu je možné zrušit rezervaci, proto se stav rezervace změní na CancelledState.
     */
    @Override
    public void cancel(Booking booking) {
        booking.setStatus(new CancelledState());
    }

    /**
     * Vrací název stavu rezervace.
     *
     * @return Název stavu rezervace "WAITING_FOR_PAYMENT".
     */
    @Override
    public String getStateName() {
        return "WAITING_FOR_PAYMENT";
    }
}
