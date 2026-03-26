package cz.zcu.kiv.elexa.mnsck.strategy;

public class LastMinuteStrategy implements DiscountStrategy{
    /**
     * Applies 20% discount for last minute customers.
     * @param basePrice Original price of booking
     * @return Discounted price of booking
     */
    @Override
    public double calculateFinal(double basePrice) {
        return basePrice * 0.80;
    }
}
