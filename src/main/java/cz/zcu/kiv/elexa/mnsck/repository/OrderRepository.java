package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Booking;
import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Booking, Long> {
    long countByTour(Tour tour); // aktualni pocet rezervaci zajezdu
}
