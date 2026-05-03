package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.event.BookingStatusChangedEvent;
import cz.zcu.kiv.elexa.mnsck.repository.BookingRepository;
import cz.zcu.kiv.elexa.mnsck.repository.CustomerRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import cz.zcu.kiv.elexa.mnsck.service.PricingStrategyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Kontroler pro rezervace zájezdu.
 * Zpracovává požadavky pro zobrazení, přidání, platbu a zrušení rezervace.
 * Používá HTMX pro dynamické aktualizace části stránky bez nutnosti reloadu celé stránky.
 * Publikuje události změny stavu rezervace pro další zpracování (např. notifikace).
 *
 * @author Tomáš Elexa
 */
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final PricingStrategyService strategyService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Zpracovani pozadavku na zobrazeni seznamu rezervac. Pokud je požadavek HTMX, vrací pouze část stránky s tabulkou rezervací, jinak vrací celou stránku s layoutem.
     *
     * @param model model pro frontend data
     * @param selectedTourId ID vybraného zájezdu
     * @param isHtmxRequest true pokud se jedná o HTMX požadavek
     * @return render stránky s rezervacemi
     */
    @GetMapping
    public String listBookings(Model model,
                               @RequestParam(value = "tourId", required = false) Long selectedTourId,
                               @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        model.addAttribute("bookings", bookingRepository.findAll());
        model.addAttribute("tours", tourRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("selectedTourId", selectedTourId);

        if (isHtmxRequest) return "bookings-list :: content";

        model.addAttribute("contentTemplate", "bookings-list");
        return "layout";
    }

    /**
     * Zpracování požadavku pro přidání rezervace.
     *
     * @param tourId ID zájezdu pro který se rezervace vytváří
     * @param customerId ID zákazníka kterému rezervace patří
     * @param persons_qty počet osob za plnou cenu
     * @param persons_reduced_qty počet osob za sníženou cenu
     * @param model model pro frontend data
     * @return render stránky s rezervacemi
     */
    @PostMapping
    public String addBooking(@RequestParam Long tourId, @RequestParam Long customerId,
                             @RequestParam Integer persons_qty, @RequestParam Integer persons_reduced_qty,
                             Model model) {
        Tour tour = tourRepository.findById(tourId).orElseThrow();
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        // check capacity
        long occupiedCapacity = bookingRepository.sumOccupiedCapacity(tour, "CANCELLED");
        int newPersonsTotal = persons_qty + persons_reduced_qty;

        if ((occupiedCapacity + newPersonsTotal) > tour.getCapacity()) {
            model.addAttribute("errorMessage", "Chyba: Kapacita zájezdu je již plně obsazena!");
        } else {
            Booking booking = new Booking();
            booking.setTour(tour);
            booking.setCustomer(customer);
            booking.setPersons_qty(persons_qty);
            booking.setPersons_reduced_qty(persons_reduced_qty);
            booking.setTotal_price(strategyService.calculatePrice(customer, tour, persons_qty, persons_reduced_qty));
            booking.setOrder_date(LocalDate.now());

            // save booking
            bookingRepository.save(booking);
        }

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }

    /**
     * Zpracování požadavku pro zobrazení modálního okna pro platbu rezervace. Vrací pouze část stránky s obsahem modálního okna.
     *
     * @param id ID rezervace pro kterou se zobrazuje modal
     * @param model model pro frontend data
     * @return render modalu
     */
    @GetMapping("/{id}/payment-modal")
    public String paymentModal(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        model.addAttribute("booking", booking);
        return "bookings-list :: payment-modal-content";
    }

    /**
     * Zpracování požadavku pro platbu rezervace. Aktualizuje stav rezervace a publikuje událost změny stavu pro další zpracování (např. notifikace).
     *
     * @param id ID rezervace, která je placena
     * @param amount placená částka
     * @param model model pro frontend data
     * @return render stránky s rezervacemi
     */
    @PostMapping("/{id}/pay")
    public String payBooking(@PathVariable Long id, @RequestParam Double amount, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        String oldStatus = booking.getStatus_string();

        if (amount == null || amount <= 0) {
            model.addAttribute("errorMessage", "Chyba: Částka platby musí být větší než 0.");
        } else {
            booking.pay(amount);
            bookingRepository.save(booking);
            eventPublisher.publishEvent(new BookingStatusChangedEvent(
                    this, booking, oldStatus, booking.getStatus_string())
            );
            model.addAttribute("successMessage", "Platba byla úspěšně přidána.");
        }

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }

    /**
     * Zpracování požadavku pro zrušení rezervace. Aktualizuje stav rezervace a publikuje událost změny stavu pro další zpracování (např. notifikace).
     *
     * @param id ID rezervace, která je stornována
     * @param model model pro frontend data
     * @return render stránky s rezervacemi
     */
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        String oldStatus = booking.getStatus_string();
        booking.cancel();
        bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingStatusChangedEvent(
                this, booking, oldStatus, booking.getStatus_string())
        );

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }
}
