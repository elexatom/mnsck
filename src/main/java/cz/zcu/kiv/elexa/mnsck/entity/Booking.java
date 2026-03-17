package cz.zcu.kiv.elexa.mnsck.entity;

import cz.zcu.kiv.elexa.mnsck.state.BookingState;
import cz.zcu.kiv.elexa.mnsck.state.CancelledState;
import cz.zcu.kiv.elexa.mnsck.state.PaidState;
import cz.zcu.kiv.elexa.mnsck.state.WaitingForPaymentState;
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

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "status")
    private String status_string;

    @Transient
    private BookingState currentStatus;

    @PostLoad
    private void init() {
        if ("PAID".equals(status_string)) {
            this.currentStatus = new PaidState();
        } else if ("CANCELLED".equals(status_string)) {
            this.currentStatus = new CancelledState();
        } else {
            this.currentStatus = new WaitingForPaymentState();
        }
    }

    @PrePersist
    @PreUpdate
    private void syncStatus() {
        if (this.currentStatus == null) {
            this.currentStatus = new WaitingForPaymentState();
        }

        this.status_string = this.currentStatus.getStateName();
    }

    public void pay(double amount) {
        if (this.currentStatus == null) this.currentStatus = new WaitingForPaymentState();
        this.currentStatus.pay(this, amount);
    }

    public void cancel() {
        if (this.currentStatus == null) this.currentStatus = new WaitingForPaymentState();
        this.currentStatus.cancel(this);
    }

   public void setStatus(BookingState status) {
        this.currentStatus = status;
        this.status_string = status.getStateName();
   }
}
