package cz.zcu.kiv.elexa.mnsck.service;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.strategy.DiscountStrategy;
import cz.zcu.kiv.elexa.mnsck.strategy.DiscountStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Služba pro správu strategií cenování. Implementuje strattegy pattern.
 *
 * @author Tomáš Elexa
 */
@Service
@RequiredArgsConstructor
public class PricingStrategyService {
    /**
     * Factory pro vytváření strategií cenování.
     */
    private final DiscountStrategyFactory strategyFactory;

    /**
     * Vypočítá konečnou cenu pro zákazníka na základě zvolené strategie.
     *
     * @param customer     Zákazník, pro kterého se cena počítá.
     * @param tour         Zájezd, pro který se cena počítá.
     * @param fullPrice    Cena za plnou cenu.
     * @param reducedPrice Cena za zlevněnou cenu.
     * @return Konečná cena.
     */
    public double calculatePrice(Customer customer, Tour tour, int fullPrice, int reducedPrice) {
        DiscountStrategy strategy = strategyFactory.createStrategy(customer, tour);

        double finalPrice = strategy.calculateFinal(
                tour.getPrice_person(),
                tour.getPrice_person_reduced(),
                fullPrice,
                reducedPrice
        );

        return Math.round(finalPrice);
    }
}
