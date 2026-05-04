package cz.zcu.kiv.elexa.mnsck.state;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Payment;

import java.time.LocalDate;

/**
 * Stav rezervace, který reprezentuje částečně uhrazenou rezervaci. Implementuje rozhraní BookingState a definuje chování pro akce platby a zrušení.
 * 
 * @author Tomáš Elexa
 */
public class PartiallyPaidState implements BookingState {

    /**
     * Metoda pro provedení platby za rezervaci. V tomto stavu je možné provést další platbu, ale rezervace zůstane v tomto stavu, dokud není uhrazena celková částka.
     */
    @Override
    public void pay(Booking booking, double amount) {
        System.out.println("Processing next payment: " + amount);

        Payment newPayment = new Payment();
        newPayment.setAmount(amount);
        newPayment.setPaymentDate(LocalDate.now());
        newPayment.setBooking(booking);
        booking.getPayments().add(newPayment);

        if (booking.getAmountPaid() >= booking.getTotal_price()) {
            booking.setStatus(new PaidState());
        }
    }

    /**
     * Metoda pro zrušení rezervace. V tomto stavu je možné zrušit rezervaci, ale bude nutné vrátit peníze klientovi, proto se stav rezervace změní na CancelledState a vypíše se informace o vrácení peněz.
     */
    @Override
    public void cancel(Booking booking) {
        System.out.println("Cancelled partially paid reservation");
        booking.setStatus(new CancelledState());
    }

    /**
     * Vrací název stavu rezervace.
     *
     * @return Název stavu rezervace "PARTIALLY_PAID".
     */
    @Override
    public String getStateName() {
        return "PARTIALLY_PAID";
    }
}
