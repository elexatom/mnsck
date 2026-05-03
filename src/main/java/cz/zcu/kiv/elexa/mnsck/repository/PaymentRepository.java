package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozitář pro entitu Payment, poskytuje základní CRUD operace a další metody pro práci s databází.
 * 
 * @author Tomáš Elexa
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * Metoda pro získání příjmu podle data platby.
     * @return Seznam objektů obsahujících datum platby a součet částek.
     */
    @Query("SELECT p.paymentDate, SUM(p.amount) FROM Payment p GROUP BY p.paymentDate ORDER BY p.paymentDate ASC")
    List<Object[]> findRevenueByDate();
}
