package cz.zcu.kiv.elexa.mnsck.strategy;

public class VipCustomerStrategy implements DiscountStrategy{
    /**
     * Applies 10% discount for VIP customers.
     * @param basePrice Original price of booking
     * @return Discounted price of booking
     */
    @Override
    public double calculateFinal(double basePrice) {
        return basePrice * 0.9;
    }
}
