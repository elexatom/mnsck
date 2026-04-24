package cz.zcu.kiv.elexa.mnsck.strategy;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DiscountStrategyResolver {
    public DiscountStrategy resolve(Customer customer, Tour tour) {
        long daysUntilTour = ChronoUnit.DAYS.between(LocalDate.now(), tour.getStart_date());
        if (daysUntilTour <= 14) return new LastMinuteStrategy();
        else if (customer.getIs_vip()) return new VipCustomerStrategy();
        else return new NoDiscountStrategy();
    }
}
