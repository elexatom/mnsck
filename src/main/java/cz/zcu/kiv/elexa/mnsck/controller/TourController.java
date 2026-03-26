package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.repository.DestinationRepository;
import cz.zcu.kiv.elexa.mnsck.repository.GuideRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tours")
public class TourController {

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private GuideRepository guideRepository;
    @Autowired
    private TransportRepository transportRepository;

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
        Tour saved = tourRepository.save(newTour);

        // pass saved object to model for view rendering
        model.addAttribute("tour", saved);

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
        model.addAttribute("tours", tourRepository.findAll());
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
