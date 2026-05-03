package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repozitář pro entitu Booking, poskytuje metody pro práci s rezervacemi.
 * Zahrnuje metody pro získání součtu obsazené kapacity a počtu rezervací podle statusu.
 * 
 * @author Tomáš Elexa
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Vrací součet obsazené kapacity pro danou tour, přičemž nezahrnuje rezervace se specifickým statusem.
     *
     * @param tour Tour, pro kterou se počítá obsazená kapacita.
     * @param status Status rezervace, který se má vyloučit z výpočtu (např. "CANCELED").
     * @return Součet obsazené kapacity pro danou tour.
     */
    @Query("SELECT COALESCE(SUM(b.persons_qty + b.persons_reduced_qty), 0) FROM Booking b WHERE b.tour = :tour AND b.status_string <> :status")
    long sumOccupiedCapacity(@Param("tour") Tour tour, @Param("status") String status);

    /**
     * Vrací počet rezervací pro daný status.
     * 
     * @param status Status rezervace, pro který se počítá počet (např. "CONFIRMED", "CANCELED").
     * @return Počet rezervací pro daný status.
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status_string = :status")
    long countByStatus(@Param("status") String status);
}
