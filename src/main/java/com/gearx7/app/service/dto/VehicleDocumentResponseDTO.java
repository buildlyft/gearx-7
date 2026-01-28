package com.gearx7.app.service.dto;

import java.util.List;

public class VehicleDocumentResponseDTO {

    private Long machineId;

    List<VehicleDocumentDTO> documents;

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public List<VehicleDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<VehicleDocumentDTO> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "VehicleDocumentResponseDTO{" + "machineId=" + machineId + ", documents=" + documents + '}';
    }
}
