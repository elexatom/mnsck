package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.repository.BookingRepository;
import cz.zcu.kiv.elexa.mnsck.repository.PaymentRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import cz.zcu.kiv.elexa.mnsck.service.TourStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler pro hlavní stránku dashboardu. Zpracovává požadavky pro zobrazení přehledu statistik a grafů. Používá HTMX pro dynamické aktualizace části stránky bez nutnosti reloadu celé stránky. Načítá data z různých repozitářů a služeb pro zobrazení klíčových metrik a grafů na dashboardu.
 *
 * @author Tomáš Elexa
 */
@Controller
@RequiredArgsConstructor
public class IndexController {
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final TourStatisticsService tourStatisticsService;

    /**
     * Zpracování požadavku pro vypsání úvodní stránky dashboardu.
     *
     * @param model model pro frontend data
     * @param isHtmxRequest true, pokud se jedná o HTMX požadavek
     * @return render stránky s přehledem statistik a grafů, buď jako část stránky pro HTMX, nebo jako celá stránka s layoutem
     */
    @GetMapping("/")
    public String index(Model model,
                        @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {

        long tourCount = tourRepository.count();
        long bookingCount = bookingRepository.count();
        long waitingPayment = bookingRepository.countByStatus("WAITING_FOR_PAYMENT");
        long averageOccupancy = tourStatisticsService.getAverageOccupancy();

        // fetch payment data for chart
        List<Object[]> revenueData = paymentRepository.findRevenueByDate();
        List<String> labels = revenueData.stream()
                .map(row -> row[0].toString())
                .collect(Collectors.toList());
        List<Double> data = revenueData.stream()
                .map(row -> (Double) row[1])
                .collect(Collectors.toList());

        model.addAttribute("waitingPaymentCount", waitingPayment);
        model.addAttribute("bookingCount", bookingCount);
        model.addAttribute("tourCount", tourCount);
        model.addAttribute("averageOccupancy", averageOccupancy + "%");
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);

        if (isHtmxRequest) {
            return "index :: content";
        }

        model.addAttribute("contentTemplate", "index");
        return "layout";
    }
}
