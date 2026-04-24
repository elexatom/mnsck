package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.repository.BookingRepository;
import cz.zcu.kiv.elexa.mnsck.repository.DestinationRepository;
import cz.zcu.kiv.elexa.mnsck.repository.GuideRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourController {
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;
    private final DestinationRepository destinationRepository;
    private final GuideRepository guideRepository;
    private final TransportRepository transportRepository;

    /**
     * Builds map of occupied capacity by tour id.
     * @param tours list of tours
     * @return map of occupied capacity by tour id
     */
    private Map<Long, Long> buildOccupiedByTourId(List<Tour> tours) {
        Map<Long, Long> occupiedByTourId = new HashMap<>();
        for (Tour tour : tours) {
            occupiedByTourId.put(tour.getTour_id(), bookingRepository.sumOccupiedCapacity(tour, "CANCELLED"));
        }
        return occupiedByTourId;
    }

    /**
     * Handle request for adding new tour.
     *
     * @param newTour new tour to be added
     * @param model   data for frontend
     * @return render page
     */
    @PostMapping
    public String addTour(@ModelAttribute Tour newTour, Model model) {
        // save to database
        tourRepository.save(newTour);

        List<Tour> tours = tourRepository.findAll();
        model.addAttribute("tours", tours);
        model.addAttribute("occupiedByTourId", buildOccupiedByTourId(tours));

        // return fragment
        return "tours-list :: tour-table";
    }

    /**
     * Handle request for listing all tours.
     *
     * @param model         data for frontend
     * @param isHtmxRequest if the request is from HTMX
     * @return render page
     */
    @GetMapping
    public String listTours(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        // add tours to model
        List<Tour> tours = tourRepository.findAll();
        model.addAttribute("tours", tours);
        model.addAttribute("occupiedByTourId", buildOccupiedByTourId(tours));
        model.addAttribute("destinations", destinationRepository.findAll());
        model.addAttribute("guides", guideRepository.findAll());
        model.addAttribute("transports", transportRepository.findAll());

        // return appropriate view based on request type
        if (isHtmxRequest) {
            return "tours-list :: content";
        }

        model.addAttribute("contentTemplate", "tours-list");
        return "layout";
    }
}
