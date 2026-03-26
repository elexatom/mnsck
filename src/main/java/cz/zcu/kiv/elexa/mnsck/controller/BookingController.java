package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.repository.BookingRepository;
import cz.zcu.kiv.elexa.mnsck.repository.CustomerRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import cz.zcu.kiv.elexa.mnsck.strategy.LastMinuteStrategy;
import cz.zcu.kiv.elexa.mnsck.strategy.NoDiscountStrategy;
import cz.zcu.kiv.elexa.mnsck.strategy.PriceCalculator;
import cz.zcu.kiv.elexa.mnsck.strategy.VipCustomerStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public String listBookings(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        model.addAttribute("bookings", bookingRepository.findAll());
        model.addAttribute("tours", tourRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());

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

            // strategy pattern (price calculation)
            double basePrice = tour.getPrice_person();
            PriceCalculator calc = new PriceCalculator(new NoDiscountStrategy()); // default state

            long daysUntilTour = ChronoUnit.DAYS.between(LocalDate.now(), tour.getStart_date());

            if (daysUntilTour <= 14) calc.setDiscountStrategy(new LastMinuteStrategy());
            else if (customer.getIs_vip()) calc.setDiscountStrategy(new VipCustomerStrategy());

            booking.setTotal_price(calc.calculateFinal(basePrice));
            booking.setDiscount_strategy(calc.getDiscountStrategy());
            booking.setOrder_date(LocalDate.now());

            // save booking
            bookingRepository.save(booking);
        }

        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list :: booking-table";
    }

    @PostMapping("/{id}/pay")
    public String payBooking(@PathVariable Long id, @RequestParam Double amount, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.pay(amount);
        bookingRepository.save(booking);

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
