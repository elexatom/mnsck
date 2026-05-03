package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozitář pro entitu Booking, poskytuje základní CRUD operace a další metody pro práci s databází.
 * 
 * @author Tomáš Elexa
 */
@Repository
public interface OrderRepository extends JpaRepository<Booking, Long> {
    /**
     * Metoda pro získání počtu rezervací pro daný zajezd (Tour).
     * @param tour Zajezd, pro který chceme získat počet rezervací.
     * @return Počet rezervací pro daný zajezd.
     */
    long countByTour(Tour tour); // aktualni pocet rezervaci zajezdu
}
