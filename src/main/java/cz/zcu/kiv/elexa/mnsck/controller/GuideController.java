package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Guide;
import cz.zcu.kiv.elexa.mnsck.entity.Language;
import cz.zcu.kiv.elexa.mnsck.repository.GuideRepository;
import cz.zcu.kiv.elexa.mnsck.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

/**
 * Kontroler pro správu průvodců. Zpracovává požadavky pro zobrazení seznamu průvodců, přidání nového průvodce a aktualizaci jazykového profilu průvodce.
 *
 * @author Tomáš Elexa
 */
@Controller
@RequestMapping("/guides")
@RequiredArgsConstructor
public class GuideController {
    private final GuideRepository guideRepository;
    private final LanguageRepository languageRepository;

    /**
     * Zpracování požadavku pro vypsání senzamu všech průvodců. Pokud je požadavek HTMX, vrací pouze část stránky s tabulkou průvodců, jinak vrací celou stránku s layoutem.
     *
     * @param model model pro frontend data
     * @param isHtmxRequest true, pokud se jedná o HTMX požadavek
     * @return render stránky se seznamem průvodců, buď jako část stránky pro HTMX, nebo jako celá stránka s layoutem
     */
    @GetMapping
    public String listGuides(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        model.addAttribute("guides", guideRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());

        if (isHtmxRequest) return "guides-list :: content";

        model.addAttribute("contentTemplate", "guides-list");
        return "layout";
    }

    /**
     * Zpracování požadavku pro přidání nového průvodce.
     *
     * @param model model pro frontend data
     * @param name jméno průvodce
     * @param surname příjmení průvodce
     * @param language1Id ID primárního jazyka
     * @param language2Id ID sekundárního jazyka
     * @param response HTTP odpověď pro nastavení statusu v případě chyby
     * @return render stránky
     */
    @PostMapping
    public String addGuide(Model model, @RequestParam String name, @RequestParam String surname,
                           @RequestParam Long language1Id, @RequestParam(required = false) Long language2Id,
                           HttpServletResponse response) {
        Guide guide = new Guide();
        guide.setName(name);
        guide.setSurname(surname);
        Language lang1 = languageRepository.findById(language1Id).orElseThrow();
        guide.setLanguage1(lang1);

        if (language2Id != null) {
            if (Objects.equals(language1Id, language2Id)) {
                model.addAttribute("errorMessage", "Primární a sekundární jazyk nesmí být stejný.");
                model.addAttribute("guides", guideRepository.findAll());
                model.addAttribute("languages", languageRepository.findAll());
                response.setStatus(422);
                return "guides-list :: guide-table";
            }

            Language lang2 = languageRepository.findById(language2Id).orElseThrow();
            guide.setLanguage2(lang2);
        }

        guideRepository.save(guide);
        model.addAttribute("guides", guideRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("successMessage", "Průvodce byl úspěšně přidán.");
        return "guides-list :: guide-table";
    }

    /**
     * Zpracování požadavku pro zobrazení detailu o průvodci.
     *
     * @param id ID průvodce, jehož detail se má zobrazit
     * @param model model pro frontend data
     * @return render části stránky s obsahem modálního okna pro detail průvodce
     */
    @GetMapping("/{id}/detail")
    public String guideDetail(@PathVariable Long id, Model model) {
        Guide guide = guideRepository.findById(id).orElseThrow();
        model.addAttribute("guide", guide);
        model.addAttribute("selectedLanguage1Id", guide.getLanguage1() != null ? guide.getLanguage1().getLanguage_id() : null);
        model.addAttribute("selectedLanguage2Id", guide.getLanguage2() != null ? guide.getLanguage2().getLanguage_id() : null);
        model.addAttribute("guides", guideRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        return "guides-list :: guide-modal-content";
    }

    /**
     * Zpracování požadavku pro aktualizaci jazyků průvodce.
     *
     * @param id ID průvodce, kterému se mění jazyky
     * @param language1Id ID primárního jazyka
     * @param language2Id ID sekundárního jazyka
     * @param model model pro frontend data
     * @param response HTTP odpověď pro nastavení statusu v případě chyby
     * @return render stránky s aktualizovaným jazykovým profilem průvodce, buď jako část stránky pro HTMX, nebo jako celá stránka s layoutem
     */
    @PostMapping("/{id}/languages")
    public String updateLanguages(@PathVariable Long id,
                                  @RequestParam Long language1Id,
                                  @RequestParam(required = false) Long language2Id,
                                  Model model,
                                  HttpServletResponse response) {
        Guide guide = guideRepository.findById(id).orElseThrow();
        model.addAttribute("guide", guide);
        model.addAttribute("guides", guideRepository.findAll());
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("selectedLanguage1Id", language1Id);
        model.addAttribute("selectedLanguage2Id", language2Id);

        if (Objects.equals(language1Id, language2Id)) {
            model.addAttribute("errorMessage", "Primární a sekundární jazyk nesmí být stejný.");
            response.setStatus(422);
            return "guides-list :: guide-modal-content";
        }

        Language language1 = languageRepository.findById(language1Id).orElseThrow();
        guide.setLanguage1(language1);
        guide.setLanguage2(language2Id == null ? null : languageRepository.findById(language2Id).orElseThrow());
        guideRepository.save(guide);

        model.addAttribute("successMessage", "Jazykový profil průvodce byl aktualizován.");
        model.addAttribute("guide", guide);
        model.addAttribute("selectedLanguage1Id", guide.getLanguage1() != null ? guide.getLanguage1().getLanguage_id() : null);
        model.addAttribute("selectedLanguage2Id", guide.getLanguage2() != null ? guide.getLanguage2().getLanguage_id() : null);
        return "guides-list :: guide-modal-content";
    }
}
