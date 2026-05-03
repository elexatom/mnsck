package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozitář pro entitu Destination, poskytuje základní CRUD operace a další metody pro práci s databází.
 * 
 * @author Tomáš Elexa
 */
@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
}
