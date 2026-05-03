package cz.zcu.kiv.elexa.mnsck.event;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Třída pro poslouchání událostí změny stavu rezervace a odesílání emailových notifikací zákazníkům.
 * Poslouchá na události typu BookingStatusChangedEvent a reaguje na změnu stavu rezervace.
 * 
 * @author Tomáš Elexa
 */
@Component
public class EmailNotificationListener {

    /**
     * Metoda pro zpracování události změny stavu rezervace. Odesílá emailové notifikace zákazníkům na základě nového stavu rezervace.
     * 
     * @param event Událost změny stavu rezervace, obsahující informace o zákazníkovi, tour a novém stavu rezervace.
     */
    @EventListener
    public void handleBookingStatusChange(BookingStatusChangedEvent event) {
        Customer customer = event.getBooking().getCustomer();
        String tourName = event.getBooking().getTour().getTour_name();

        if ("PAID".equals(event.getNewStatus())) {
            // Odeslat email o zaplaceni..
            System.out.println("Potvrzeni platby odeslano pro zakaznika: " + customer.getEmail() + " a tour: " + tourName);
        } else if ("CANCELLED".equals(event.getNewStatus())) {
            // Odeslat email o zruseni..
            System.out.println("Zruseni rezervace odeslano pro zakaznika: " + customer.getEmail() + " a tour: " + tourName);
        } else {
            // zmena stavu
            System.out.println("Stav rezervace zmenen na: " + event.getNewStatus() + " pro zakaznika: " + customer.getEmail() + " a tour: " + tourName);
        }
    }
}
