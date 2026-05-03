package cz.zcu.kiv.elexa.mnsck.strategy;

/**
 * Strategie slevy pro VIP zákazníky.
 * 
 * @author Tomáš Elexa
 */
public class VipCustomerStrategy implements DiscountStrategy {
    /**
     * Vypočítá konečnou cenu po aplikaci slevy pro VIP zákazníky (10% sleva).
     * 
     * @param basePrice Cena za jednotku bez slevy.
     * @param reducedPrice Cena za jednotku se slevou.
     * @param baseQty Množství bez slevy.
     * @param reducedQty Množství se slevou.
     * @return Konečná cena po aplikaci slevy.
     */
    @Override
    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        return baseQty * basePrice * 0.9 + reducedQty * reducedPrice * 0.9;
    }
}
