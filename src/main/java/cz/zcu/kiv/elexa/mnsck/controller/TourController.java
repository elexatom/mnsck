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


/**
 * Kontroler pro zájezdy. Zpracovává požadavky pro zobrazení seznamu zájezdů a přidání nového zájezdu. Používá HTMX pro dynamické aktualizace části stránky bez nutnosti reloadu celé stránky. Načítá data z různých repozitářů pro zobrazení informací o zájezdech, jako jsou obsazenost, destinace, průvodci a doprava.
 * @author Tomáš Elexa
 */
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
     * Sestaví mapu obsazenosti zájezdů podle ID zájezdu. Pro každý zájezd získá součet obsazené kapacity z rezervací, které nejsou zrušené.
     *
     * @param tours seznam zájezdů, pro které se má sestavit mapa obsazenosti
     * @return mapa, kde klíčem je ID zájezdu a hodnotou je obsazenost (součet obsazené kapacity) pro daný zájezd
     */
    private Map<Long, Long> buildOccupiedByTourId(List<Tour> tours) {
        Map<Long, Long> occupiedByTourId = new HashMap<>();
        for (Tour tour : tours) {
            occupiedByTourId.put(tour.getTour_id(), bookingRepository.sumOccupiedCapacity(tour, "CANCELLED"));
        }
        return occupiedByTourId;
    }

    /**
     * Zpracování požadavku pro přidání nového zájezdu. Uloží nový zájezd do databáze a vrátí aktualizovanou část stránky se seznamem zájezdů pro HTMX, aby se zobrazil nový zájezd bez nutnosti reloadu celé stránky.
     *
     * @param newTour data pro vytvoření nového zájezdu, získaná z formuláře
     * @param model model pro frontend data
     * @return render aktualizované části stránky se seznamem zájezdů pro HTMX, aby se zobrazil nový zájezd bez nutnosti reloadu celé stránky
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
     * Zpracování požadavku pro vypsání zájezdů.
     *
     * @param model model pro frontend data
     * @param isHtmxRequest true pokud se jedná o HTMX požadavek
     * @return render stránky se seznamem zájezdů, buď jako část stránky pro HTMX, nebo jako celá stránka s layoutem
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
