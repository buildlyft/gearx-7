package com.gearx7.app.repository;

import com.gearx7.app.domain.Machine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Machine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {}
