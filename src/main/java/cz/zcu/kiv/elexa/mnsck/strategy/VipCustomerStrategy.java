package cz.zcu.kiv.elexa.mnsck.strategy;

public class VipCustomerStrategy implements DiscountStrategy {
    /**
     * Applies 10% discount for VIP customers.
     *
     * @param basePrice Original price of booking
     * @return Discounted price of booking
     */
    @Override
    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        return baseQty * basePrice * 0.9 + reducedQty * reducedPrice * 0.9;
    }
}
