package com.gearx7.app.repository;

import com.gearx7.app.domain.VehicleDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {
    // Optional: prevent duplicate doc types per machine
    boolean existsByMachineIdAndDocType(Long machineId, String docType);

    // get All VehicleDocuments by MachineId
    List<VehicleDocument> findAllByMachineId(Long machineId);

    // Get VehicleDocument by MachineId and DocType
    Optional<VehicleDocument> findByMachineIdAndDocType(Long machineId, String docType);
}
