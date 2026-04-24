package com.gearx7.app.service.dto;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class MachineOperatorDetailsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /* ===== IDs ===== */

    private Long operatorId; // response
    private Long partnerId; // request

    /* ===== Operator Info ===== */

    private String driverName;
    private String operatorContact;
    private String address;
    private LocalDate licenseIssueDate;

    private String docUrl;

    private String imageUrl;

    /* ===== Metadata ===== */

    private Instant createdAt;

    // getters & setters
    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
            ", partnerId=" +
            partnerId +
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
            ", docUrl='" +
            docUrl +
            '\'' +
            ", imageUrl='" +
            imageUrl +
            '\'' +
            ", createdAt=" +
            createdAt +
            '}'
        );
    }
}
