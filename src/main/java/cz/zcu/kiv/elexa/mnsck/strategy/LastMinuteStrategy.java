package cz.zcu.kiv.elexa.mnsck.strategy;

/**
 * Strategie pro zákazníky, kteří rezervují zájezd na poslední chvíli (méně než 14 dní před začátkem zájezdu).
 * 
 * @author Tomáš Elexa
 */
public class LastMinuteStrategy implements DiscountStrategy {
    /**
     * Vypočítá konečnou cenu po aplikaci slevy pro last minute rezervace (20% sleva).
     * 
     * @param basePrice Cena za jednotku bez slevy.
     * @param reducedPrice Cena za jednotku se slevou.
     * @param baseQty Množství bez slevy.
     * @param reducedQty Množství se slevou.
     * @return Konečná cena po aplikaci slevy.
     */
    @Override
    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        return baseQty * basePrice * 0.80 + reducedQty * reducedPrice * 0.80;
    }
}
