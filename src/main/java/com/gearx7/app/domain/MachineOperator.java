package com.gearx7.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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

    @OneToOne
    @JoinColumn(name = "machine_id", nullable = false, unique = true)
    @JsonIgnoreProperties(value = { "document", "operator", "subcategory", "category", "user" }, allowSetters = true)
    private Machine machine;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "operator_contact")
    private String operatorContact;

    @Column(name = "license_issue_date")
    private LocalDate licenseIssueDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_document_id", unique = true)
    @JsonIgnoreProperties(value = { "machine" }, allowSetters = true)
    private VehicleDocument vehicleDocument;

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOperatorContact(String operatorContact) {
        this.operatorContact = operatorContact;
    }

    public void setLicenseIssueDate(LocalDate licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public void setVehicleDocument(VehicleDocument vehicleDocument) {
        this.vehicleDocument = vehicleDocument;
    }

    public Long getId() {
        return id;
    }

    public Machine getMachine() {
        return machine;
    }

    public User getUser() {
        return user;
    }

    public String getOperatorContact() {
        return operatorContact;
    }

    public LocalDate getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public VehicleDocument getVehicleDocument() {
        return vehicleDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MachineOperator that = (MachineOperator) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(machine, that.machine) &&
            Objects.equals(user, that.user) &&
            Objects.equals(operatorContact, that.operatorContact) &&
            Objects.equals(licenseIssueDate, that.licenseIssueDate) &&
            Objects.equals(vehicleDocument, that.vehicleDocument)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, machine, user, operatorContact, licenseIssueDate, vehicleDocument);
    }

    @Override
    public String toString() {
        return (
            "MachineOperator{" +
            "id=" +
            id +
            ", operatorContact='" +
            operatorContact +
            '\'' +
            ", licenseIssueDate=" +
            licenseIssueDate +
            '\'' +
            ",driverName='" +
            driverName +
            '}'
        );
    }
}
