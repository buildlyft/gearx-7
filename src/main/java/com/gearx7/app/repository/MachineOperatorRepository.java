package com.gearx7.app.repository;

import com.gearx7.app.domain.MachineOperator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineOperatorRepository extends JpaRepository<MachineOperator, Long> {
    Optional<MachineOperator> findByMachineId(Long machineId);

    boolean existsByMachineId(Long machineId);
}
