package cz.zcu.kiv.elexa.mnsck.strategy;

/**
 * Třída pro výpočet konečné ceny podle vybrané strategie slevy.
 * 
 * @author Tomáš Elexa
 */
public class PriceCalculator {

    /**
     * Strategie slevy, která bude použita pro výpočet konečné ceny.
     */
    private DiscountStrategy discountStrategy;

    /**
     * Vytvoří PriceCalculator s danou strategií slevy.
     * 
     * @param discountStrategy Strategie slevy, která bude použita pro výpočet konečné ceny
     */
    public PriceCalculator(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    /**
     * Vypočítá konečnou cenu po aplikaci slevy pomocí aktuální strategie slevy.
     * 
     * @param basePrice Cena za jednotku bez slevy.
     * @param reducedPrice Cena za jednotku se slevou.
     * @param baseQty Množství bez slevy.
     * @param reducedQty Množství se slevou.
     * @return Konečná cena po aplikaci slevy.
     */
    public double calculateFinal(double basePrice, double reducedPrice, int baseQty, int reducedQty) {
        if (this.discountStrategy == null) {
            throw new IllegalStateException("Strategie nebyla nastavena!");
        }

        return this.discountStrategy.calculateFinal(basePrice, reducedPrice, baseQty, reducedQty);
    }
}
