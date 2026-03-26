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

@Entity
@Table(name = "BOOKING")
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    private LocalDate order_date;
    private Integer persons_qty;
    private Integer persons_reduced_qty;
    private Double total_price;

    @Transient
    private DiscountStrategy discount_strategy;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "status")
    private String status_string;

    @Transient
    private BookingState current_status;

    @PostLoad
    private void init() {
        switch (status_string) {
            case "PAID" -> this.current_status = new PaidState();
            case "CANCELLED" -> this.current_status = new CancelledState();
            case "PARTIALLY_PAID" -> this.current_status = new PartiallyPaidState();
            case null, default -> this.current_status = new WaitingForPaymentState();
        }
    }

    @PrePersist
    @PreUpdate
    private void syncStatus() {
        if (this.current_status == null) {
            this.current_status = new WaitingForPaymentState();
        }

        this.status_string = this.current_status.getStateName();
    }

    public void pay(double amount) {
        Payment newPayment = new Payment();
        newPayment.setAmount(amount);
        newPayment.setPayment_date(LocalDate.now());
        newPayment.setBooking(this);
        this.payments.add(newPayment);

        // delegate handling to State Pattern
        if (this.current_status == null) this.current_status = new WaitingForPaymentState();
        this.current_status.pay(this, amount);
    }

    public void cancel() {
        if (this.current_status == null) this.current_status = new WaitingForPaymentState();
        this.current_status.cancel(this);
    }

    public void setStatus(BookingState status) {
        this.current_status = status;
        this.status_string = status.getStateName();
    }

    public double getAmountPaid() {
        if (this.payments == null) return 0.0;
        return this.payments.stream().mapToDouble(Payment::getAmount).sum();
    }
}
