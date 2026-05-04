package cz.zcu.kiv.elexa.mnsck.entity;

import cz.zcu.kiv.elexa.mnsck.state.*;
import cz.zcu.kiv.elexa.mnsck.strategy.DiscountStrategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entita pro rezervaci (booking) v systému. Obsahuje informace o zákazníkovi, zájezdu, datu objednávky, počtu osob, celkové ceně a stavu rezervace. Rezervace může být spojena s více platbami, které jsou reprezentovány entitou Payment. Stav rezervace je řízen pomocí State Pattern,  který  umožňuje flexibilní přechody mezi různými stavy (např. čekající na platbu, částečně zaplacená, zaplacená, zrušená) a deleguje logiku pro platby a zrušení na jednotlivé stavy. Celková cena rezervace může být ovlivněna různými slevami, které jsou implementovány pomocí Strategy Pattern. Rezervace je propojena s entitami Customer a Tour pomocí vztahů ManyToOne.
 *
 * @author Tomáš Elexa
 */
@Entity
@Table(name = "BOOKING")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    /**
     * ID rezervace
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Zákazník, propojeno s entitou Customer. Rezervace musí být vždy spojena s existujícím zákazníkem.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * Zájezd, propojeno s entitou Tour. Rezervace musí být vždy spojena s existujícím zájezdem.
     */
    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    /**
     * Datum rezervace.
     */
    private LocalDate order_date;
    /**
     * Počet osob za plnou cenu.
     */
    private Integer persons_qty;
    /**
     * Počet osob za sníženou cenu.
     */
    private Integer persons_reduced_qty;
    /**
     * Celková cena rezervace.
     */
    private Double total_price;

    /**
     * Seznam entit Payment (plateb) spojených s touto rezervací. Jedna rezervace může mít více plateb.
     */
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    /**
     * Stav rezervace (reprezentován jako řetězec).
     */
    @Column(name = "status")
    private String status_string;

    /**
     * Aktuální stav rezervace reprezentovaný pomocí BookingState. Tento atribut není persistován do databáze, ale je odvozen z status_string.
     */
    @Transient
    private BookingState current_status;

    /**
     * Inicializace aktuálního stavu rezervace po načtení z databáze na základě hodnoty status_string. Tento metoda se spouští automaticky po načtení entity z databáze a nastavuje current_status podle uloženého řetězce stavu.
     */
    @PostLoad
    private void init() {
        switch (status_string) {
            case "PAID" -> this.current_status = new PaidState();
            case "CANCELLED" -> this.current_status = new CancelledState();
            case "PARTIALLY_PAID" -> this.current_status = new PartiallyPaidState();
            case null, default -> this.current_status = new WaitingForPaymentState();
        }
    }

    /**
     * Synchronizace status_string s current_status před uložením nebo aktualizací entity v databázi. Tato metoda se spouští automaticky před persistencí nebo aktualizací entity a zajišťuje, že status_string vždy odpovídá aktuálnímu stavu reprezentovanému current_status.
     */
    @PrePersist
    @PreUpdate
    private void syncStatus() {
        if (this.current_status == null) {
            this.current_status = new WaitingForPaymentState();
        }

        this.status_string = this.current_status.getStateName();
    }

    /**
     * Metoda pro zpracování platby za rezervaci.
     *
     * @param amount částka, kterou zákazník platí. Tato částka je přidána k existujícím platbám a aktualizuje stav rezervace podle logiky definované v jednotlivých stavech.
     */
    public void pay(double amount) {
        if (this.current_status == null) this.current_status = new WaitingForPaymentState();
        this.current_status.pay(this, amount);
    }

    /**
     * Metoda pro zrušení rezervace. Tato metoda aktualizuje stav rezervace na "CANCELLED" a provádí další logiku spojenou se zrušením, která je definována v jednotlivých stavech.
     */
    public void cancel() {
        if (this.current_status == null) this.current_status = new WaitingForPaymentState();
        this.current_status.cancel(this);
    }

    /**
     * Metoda pro nastavení stavu rezervace. Tato metoda aktualizuje current_status a synchronizuje status_string s názvem stavu.
     *
     * @param status nový stav rezervace, který bude nastaven.
     */
    public void setStatus(BookingState status) {
        this.current_status = status;
        this.status_string = status.getStateName();
    }

    /**
     * Metoda pro získání celkové částky zaplacené za rezervaci.
     *
     * @return celková částka zaplacená za rezervaci.
     */
    public double getAmountPaid() {
        if (this.payments == null) return 0.0;
        return this.payments.stream().mapToDouble(Payment::getAmount).sum();
    }
}
