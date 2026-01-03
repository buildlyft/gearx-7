package com.gearx7.app.repository;

import com.gearx7.app.domain.VehicleDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {
    boolean existsByMachineId(Long machineId);

    Optional<VehicleDocument> findByMachineId(Long machineId);
}
