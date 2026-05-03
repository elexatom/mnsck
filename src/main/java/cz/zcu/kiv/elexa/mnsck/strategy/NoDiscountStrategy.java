package cz.zcu.kiv.elexa.mnsck.strategy;

/**
 * Strategie pro zákazníky, kteří nemají nárok na žádnou slevu.
 * 
 * @author Tomáš Elexa
 */
public class NoDiscountStrategy implements DiscountStrategy {
    /**
     * Vypočítá konečnou cenu bez aplikace slevy.
     * 
     * @param basePrice Cena za jednotku bez slevy.
     * @param reducedPrice Cena za jednotku se slevou (nepoužívá se).
     * @param baseQty Množství bez slevy.
     * @param reducedQty Množství se slevou (nepoužívá se).
     * @return Konečná cena bez aplikace slevy.
     */
    @Override
    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        return baseQty * basePrice + reducedQty * reducedPrice;
    }
}
