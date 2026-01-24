package com.gearx7.app.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class MachineOperatorDetailsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /* ===== IDs ===== */

    private Long operatorId; // response
    private Long machineId; // request + response
    private Long userId; // request

    /* ===== Operator Info ===== */

    private String driverName;
    private String operatorContact;
    private String address;
    private LocalDate licenseIssueDate;

    private List<OperatorDocumentDTO> documents;

    /* ===== Metadata ===== */

    private Instant createdAt;

    // getters & setters

    public List<OperatorDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<OperatorDocumentDTO> documents) {
        this.documents = documents;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getOperatorContact() {
        return operatorContact;
    }

    public void setOperatorContact(String operatorContact) {
        this.operatorContact = operatorContact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(LocalDate licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return (
            "MachineOperatorDetailsDTO{" +
            "operatorId=" +
            operatorId +
            ", machineId=" +
            machineId +
            ", userId=" +
            userId +
            ", driverName='" +
            driverName +
            '\'' +
            ", operatorContact='" +
            operatorContact +
            '\'' +
            ", address='" +
            address +
            '\'' +
            ", licenseIssueDate=" +
            licenseIssueDate +
            ", createdAt=" +
            createdAt +
            '}'
        );
    }
}
