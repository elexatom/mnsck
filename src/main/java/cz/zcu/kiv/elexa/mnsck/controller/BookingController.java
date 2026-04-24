package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.repository.BookingRepository;
import cz.zcu.kiv.elexa.mnsck.repository.CustomerRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import cz.zcu.kiv.elexa.mnsck.service.BookingPricingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final BookingPricingStrategy strategy;

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
            booking.setTotal_price(strategy.calculatePrice(customer, tour, persons_qty, persons_reduced_qty));
            booking.setOrder_date(LocalDate.now());

            // save booking
            bookingRepository.save(booking);
        }

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }

    @GetMapping("/{id}/payment-modal")
    public String paymentModal(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        model.addAttribute("booking", booking);
        return "bookings-list :: payment-modal-content";
    }

    @PostMapping("/{id}/pay")
    public String payBooking(@PathVariable Long id, @RequestParam Double amount, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();

        if (amount == null || amount <= 0) {
            model.addAttribute("errorMessage", "Chyba: Částka platby musí být větší než 0.");
        } else {
            booking.pay(amount);
            bookingRepository.save(booking);
            model.addAttribute("successMessage", "Platba byla úspěšně přidána.");
        }

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.cancel();
        bookingRepository.save(booking);

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }
}
