package cz.zcu.kiv.elexa.mnsck.strategy;

/**
 * Rozhraní pro strategii slevy, které umožňuje výpočet konečné ceny po aplikaci slevy.
 * 
 * @author Tomáš Elexa
 */
public interface DiscountStrategy {
    /**
     * Vypočítá konečnou cenu po aplikaci slevy.
     * 
     * @param basePrice Cena za jednotku bez slevy.
     * @param reducedPrice Cena za jednotku se slevou.
     * @param baseQty Množství bez slevy.
     * @param reducedQty Množství se slevou.
     * @return Konečná cena po aplikaci slevy.
     */
    double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty);
}
