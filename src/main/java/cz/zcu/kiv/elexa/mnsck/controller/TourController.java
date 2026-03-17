package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tours")
public class TourController {
    @Autowired
    private TourRepository repository;

    @PostMapping
    public String add(@ModelAttribute Tour newTour, Model model) {
        // save to database
        Tour saved = repository.save(newTour);

        // pass saved object to model for view rendering
        model.addAttribute("tour", saved);

        // return fragment
        return "tours-list :: tour-row";
    }

    @GetMapping
    public String listTours(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        // add tours to model
        model.addAttribute("tours", repository.findAll());

        // return appropriate view based on request type
        if (isHtmxRequest) {
            return "tours-list :: content";
        }

        model.addAttribute("contentTemplate", "tours-list");
        return "layout";
    }
}
