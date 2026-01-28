package com.gearx7.app.service.interfaces;

import com.gearx7.app.service.dto.VehicleDocumentDTO;
import com.gearx7.app.service.dto.VehicleDocumentResponseDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface VehicleDocService {
    /**
     * Bulk upload documents for a machine
     */
    VehicleDocumentResponseDTO uploadDocuments(Long machineId, Long uploadedBy, MultipartFile[] files);

    /**
     * Get documents of a specific machine
     */
    VehicleDocumentResponseDTO getMachineDocuments(Long machineId);

    /**
     * Get all machine documents (admin/global view)
     */
    List<VehicleDocumentResponseDTO> getAllDocuments();
}
