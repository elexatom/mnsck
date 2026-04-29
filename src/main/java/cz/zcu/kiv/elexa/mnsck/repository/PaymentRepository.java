package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("SELECT p.paymentDate, SUM(p.amount) FROM Payment p GROUP BY p.paymentDate ORDER BY p.paymentDate ASC")
    List<Object[]> findRevenueByDate();

    List<Payment> findAllByPaymentDateAfter(LocalDate date);
}
