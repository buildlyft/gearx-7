package com.gearx7.app.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

public class MachineOperatorDetailsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /* ===== IDs ===== */

    private Long operatorId; // response
    private Long partnerId; // response
    private Long machineId; // response
    private boolean active; // response

    /* ===== Operator Info ===== */
    @NotBlank(message = "Please Enter Driver name")
    @Size(min = 3, max = 100, message = "Driver name must be between 3 and 100 characters")
    private String driverName;

    @NotBlank(message = "Please Enter Operator contact")
    @Pattern(regexp = "^[0-9]\\d{9}$", message = "Invalid mobile number")
    private String operatorContact;

    @NotBlank(message = "Please Enter Address")
    @Size(min = 3, max = 1000, message = "Address must be between 3 and 1000 characters")
    private String address;

    @NotNull(message = "Please Select License issue date")
    private LocalDate licenseIssueDate;

    private String docUrl;

    private String imageUrl;

    /* ===== Metadata ===== */

    private Instant createdAt;

    // getters & setters

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

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
            ", machineId=" +
            machineId +
            ", active=" +
            active +
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
