package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // sums
    @Query("SELECT COALESCE(SUM(b.persons_qty + b.persons_reduced_qty), 0) FROM Booking b WHERE b.tour = :tour AND b.status_string <> :status")
    long sumOccupiedCapacity(@Param("tour") Tour tour, @Param("status") String status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status_string = :status")
    long countByStatus(@Param("status") String status);
}
