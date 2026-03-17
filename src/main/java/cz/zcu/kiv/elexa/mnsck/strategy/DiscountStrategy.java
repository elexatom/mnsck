package cz.zcu.kiv.elexa.mnsck.strategy;

public interface DiscountStrategy {
    /**
     * Calculates final price after discount is applied.
     * @param basePrice Original price of booking
     * @return Final price of booking
     */
    double calculateFinal(double basePrice);
}
