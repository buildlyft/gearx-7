package com.gearx7.app.repository;

import com.gearx7.app.domain.MachineOperator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineOperatorRepository extends JpaRepository<MachineOperator, Long> {
    //  Get only current(active) operator for machine
    Optional<MachineOperator> findByMachineIdAndActiveTrue(Long machineId);

    //  Check if machine already has an active operator
    boolean existsByMachineIdAndActiveTrue(Long machineId);

    List<MachineOperator> findAllByActiveTrue();

    @Modifying
    @Query("update MachineOperator mo set mo.active = false where mo.machine.id = :machineId and mo.active = true")
    void deactivateAllActiveByMachineId(@Param("machineId") Long machineId);
}
