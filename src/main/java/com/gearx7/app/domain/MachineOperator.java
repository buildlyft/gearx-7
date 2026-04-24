package com.gearx7.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "machine_operator")
public class MachineOperator implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "machine_operator_seq", sequenceName = "machine_operator_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "machine_operator_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "driver_name")
    private String driverName;

    @OneToOne(mappedBy = "operator")
    @JsonIgnoreProperties(value = { "operator" }, allowSetters = true)
    private Machine machine;

    @Column(name = "operator_contact")
    private String operatorContact;

    @Column(name = "license_issue_date")
    private LocalDate licenseIssueDate;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "doc_url")
    @Size(min = 5, max = 1000)
    private String docUrl;

    @Column(name = "image_url")
    @Size(min = 5, max = 1000)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id")
    @JsonIgnoreProperties(value = { "password", "authorities" }, allowSetters = true)
    private User partner;

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner = partner;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public String getOperatorContact() {
        return operatorContact;
    }

    public void setOperatorContact(String operatorContact) {
        this.operatorContact = operatorContact;
    }

    public LocalDate getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(LocalDate licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineOperator)) return false;
        return id != null && id.equals(((MachineOperator) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "MachineOperator{" +
            "id=" +
            id +
            ", driverName='" +
            driverName +
            '\'' +
            ", operatorContact='" +
            operatorContact +
            '\'' +
            ", licenseIssueDate=" +
            licenseIssueDate +
            ", address='" +
            address +
            '\'' +
            ", createdAt=" +
            createdAt +
            ", docUrl='" +
            docUrl +
            '\'' +
            ", imageUrl='" +
            imageUrl +
            '\'' +
            '}'
        );
    }
}
