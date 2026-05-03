package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozitář pro entitu Tour, poskytuje základní CRUD operace a další metody pro práci s databází.
 * 
 * @author Tomáš Elexa
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}
