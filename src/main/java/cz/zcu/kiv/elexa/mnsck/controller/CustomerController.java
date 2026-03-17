package cz.zcu.kiv.elexa.mnsck.controller;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Handle request for listing all customers.
     * @param model model for frontend data
     * @param isHtmxRequest if the request is from HTMX
     * @return render page
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
     * Handle request for adding new customer.
     * @param customer new customer to be added
     * @param model model for frontend data
     * @return render page
     */
    @PostMapping
    public String addCustomer(@ModelAttribute Customer customer, Model model) {
        customerRepository.save(customer);
        model.addAttribute("customers", customerRepository.findAll());
        return "customers-list :: customer-table";
    }
}
