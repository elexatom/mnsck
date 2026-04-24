package cz.zcu.kiv.elexa.mnsck.strategy;

public class NoDiscountStrategy implements DiscountStrategy {
    /**
     * No discount is applied.
     *
     * @param basePrice Original price of booking
     * @return Original price of booking
     */
    @Override
    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        return baseQty * basePrice + reducedQty * reducedPrice;
    }
}
