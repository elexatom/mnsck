package cz.zcu.kiv.elexa.mnsck.strategy;

public class PriceCalculator {

    private DiscountStrategy discountStrategy;

    public PriceCalculator(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public DiscountStrategy getDiscountStrategy() {
        return this.discountStrategy;
    }

    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        if (this.discountStrategy == null) {
            throw new IllegalStateException("Strategie nebyla nastavena!");
        }

        return this.discountStrategy.calculateFinal(basePrice, reducedPrice, baseQty, reducedQty);
    }
}
