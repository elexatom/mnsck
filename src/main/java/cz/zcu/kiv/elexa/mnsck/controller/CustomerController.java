package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.repository.CustomerRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Kontroler pro správu zákaznických účtů.
 * Zpracovává požadavky pro zobrazení seznamu zákazníků a přidání nového zákazníka.
 * Používá HTMX pro dynamické aktualizace části stránky bez nutnosti reloadu celé stránky.
 *
 * @author Tomáš Elexa
 */
@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    /**
     * Zpracování požadavku pro vypsání seznamu všech zákazníků.
     *
     * @param model model pro frontend data
     * @param isHtmxRequest true pokud se jedná o HTMX požadavek
     * @return render stránky se seznamem zákazníků, buď jako část stránky pro HTMX, nebo jako celá stránka s layoutem
     */
    @GetMapping
    public String listCustomers(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        model.addAttribute("customers", customerRepository.findAll());

        if (isHtmxRequest) {
            return "customers-list :: content";
        }

        model.addAttribute("contentTemplate", "customers-list");
        return "layout";
    }

    /**
     * Zpracování požadavku pro přidání nového zákazníka. Pokud není nastaveno, zda je zákazník VIP, nastaví se na false.
     *
     * @param customer data pro vytvoření nového zákazníka, získaná z formuláře
     * @param model model pro frontend data
     * @return render aktualizované části stránky se seznamem zákazníků pro HTMX, aby se zobrazil nový zákazník bez nutnosti reloadu celé stránky
     */
    @PostMapping
    public String addCustomer(@ModelAttribute Customer customer, Model model) {
        if (customer.getIs_vip() == null) {
            customer.setIs_vip(false);
        }

        customerRepository.save(customer);
        model.addAttribute("customers", customerRepository.findAll());
        return "customers-list :: customer-table";
    }
}
