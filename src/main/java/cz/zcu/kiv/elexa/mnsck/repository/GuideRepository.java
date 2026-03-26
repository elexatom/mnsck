package cz.zcu.kiv.elexa.mnsck.repository;

import cz.zcu.kiv.elexa.mnsck.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
}
