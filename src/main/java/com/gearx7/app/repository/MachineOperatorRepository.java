package com.gearx7.app.repository;

import com.gearx7.app.domain.MachineOperator;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineOperatorRepository extends JpaRepository<MachineOperator, Long> {
    List<MachineOperator> findByPartnerId(Long partnerId);
}
