package cz.zcu.kiv.elexa.mnsck.service;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import cz.zcu.kiv.elexa.mnsck.strategy.DiscountStrategy;
import cz.zcu.kiv.elexa.mnsck.strategy.DiscountStrategyResolver;
import cz.zcu.kiv.elexa.mnsck.strategy.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingPricingStrategy {
    private final DiscountStrategyResolver resolver;

    public double calculatePrice(Customer customer, Tour tour, int fullPrice, int reducedPrice) {
        DiscountStrategy strategy = resolver.resolve(customer, tour);
        PriceCalculator calculator = new PriceCalculator(strategy);

        double finalPrice = calculator.calculateFinal(
                tour.getPrice_person(),
                tour.getPrice_person_reduced(),
                fullPrice,
                reducedPrice
        );

        return Math.round(finalPrice);
    }
}
