package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
