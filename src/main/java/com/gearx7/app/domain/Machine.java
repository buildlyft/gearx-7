package com.gearx7.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gearx7.app.domain.enumeration.MachineStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Machine.
 */
@Entity
@Table(name = "machine")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Machine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "brand", nullable = false)
    private String brand;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "tag")
    private String tag;

    @Column(name = "model")
    private String model;

    @Column(name = "vin_number")
    private String vinNumber;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "capacity_ton")
    private Integer capacityTon;

    @NotNull
    @Column(name = "rate_per_hour", precision = 21, scale = 2, nullable = false)
    private BigDecimal ratePerHour;

    @NotNull
    @Column(name = "rate_per_day", precision = 21, scale = 2, nullable = false)
    private BigDecimal ratePerDay;

    @Column(name = "minimum_usage_hours")
    private Integer minimumUsageHours;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "transportation_charge", precision = 21, scale = 2)
    private BigDecimal transportationCharge;

    @Column(name = "driver_batta", precision = 21, scale = 2)
    private BigDecimal driverBatta;

    @Column(name = "serviceability_range_km")
    private Integer serviceabilityRangeKm;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MachineStatus status;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subcategories" }, allowSetters = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Subcategory subcategory;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "adhar_no")
    private String adharNo;

    @Column(name = "age")
    private Integer age;

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "insurance_no")
    private String insuranceNo;

    @OneToOne(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "machine", "user", "vehicleDocument" }, allowSetters = true)
    private MachineOperator operator;

    @OneToOne(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "machine" }, allowSetters = true)
    private VehicleDocument document;

    public MachineOperator getOperator() {
        return operator;
    }

    public void setOperators(MachineOperator operator) {
        this.operator = operator;
    }

    public VehicleDocument getDocument() {
        return document;
    }

    public void setDocuments(VehicleDocument document) {
        this.document = document;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getAdharNo() {
        return adharNo;
    }

    public void setAdharNo(String adharNo) {
        this.adharNo = adharNo;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public Long getId() {
        return this.id;
    }

    public Machine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return this.brand;
    }

    public Machine brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return this.type;
    }

    public Machine type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return this.tag;
    }

    public Machine tag(String tag) {
        this.setTag(tag);
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getModel() {
        return this.model;
    }

    public Machine model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVinNumber() {
        return this.vinNumber;
    }

    public Machine vinNumber(String vinNumber) {
        this.setVinNumber(vinNumber);
        return this;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getChassisNumber() {
        return this.chassisNumber;
    }

    public Machine chassisNumber(String chassisNumber) {
        this.setChassisNumber(chassisNumber);
        return this;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public Machine description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacityTon() {
        return this.capacityTon;
    }

    public Machine capacityTon(Integer capacityTon) {
        this.setCapacityTon(capacityTon);
        return this;
    }

    public void setCapacityTon(Integer capacityTon) {
        this.capacityTon = capacityTon;
    }

    public BigDecimal getRatePerHour() {
        return this.ratePerHour;
    }

    public Machine ratePerHour(BigDecimal ratePerHour) {
        this.setRatePerHour(ratePerHour);
        return this;
    }

    public void setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public BigDecimal getRatePerDay() {
        return this.ratePerDay;
    }

    public Machine ratePerDay(BigDecimal ratePerDay) {
        this.setRatePerDay(ratePerDay);
        return this;
    }

    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public Integer getMinimumUsageHours() {
        return this.minimumUsageHours;
    }

    public Machine minimumUsageHours(Integer minimumUsageHours) {
        this.setMinimumUsageHours(minimumUsageHours);
        return this;
    }

    public void setMinimumUsageHours(Integer minimumUsageHours) {
        this.minimumUsageHours = minimumUsageHours;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Machine latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Machine longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getTransportationCharge() {
        return this.transportationCharge;
    }

    public Machine transportationCharge(BigDecimal transportationCharge) {
        this.setTransportationCharge(transportationCharge);
        return this;
    }

    public void setTransportationCharge(BigDecimal transportationCharge) {
        this.transportationCharge = transportationCharge;
    }

    public BigDecimal getDriverBatta() {
        return this.driverBatta;
    }

    public Machine driverBatta(BigDecimal driverBatta) {
        this.setDriverBatta(driverBatta);
        return this;
    }

    public void setDriverBatta(BigDecimal driverBatta) {
        this.driverBatta = driverBatta;
    }

    public Integer getServiceabilityRangeKm() {
        return this.serviceabilityRangeKm;
    }

    public Machine serviceabilityRangeKm(Integer serviceabilityRangeKm) {
        this.setServiceabilityRangeKm(serviceabilityRangeKm);
        return this;
    }

    public void setServiceabilityRangeKm(Integer serviceabilityRangeKm) {
        this.serviceabilityRangeKm = serviceabilityRangeKm;
    }

    public MachineStatus getStatus() {
        return this.status;
    }

    public Machine status(MachineStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Machine createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Machine user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Machine)) {
            return false;
        }
        return getId() != null && getId().equals(((Machine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Machine{" +
            "id=" + getId() +
            ", brand='" + getBrand() + "'" +
            ", type='" + getType() + "'" +
            ", tag='" + getTag() + "'" +
            ", model='" + getModel() + "'" +
            ", vinNumber='" + getVinNumber() + "'" +
            ", chassisNumber='" + getChassisNumber() + "'" +
            ", description='" + getDescription() + "'" +
            ", capacityTon=" + getCapacityTon() +
            ", ratePerHour=" + getRatePerHour() +
            ", ratePerDay=" + getRatePerDay() +
            ", minimumUsageHours=" + getMinimumUsageHours() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", transportationCharge=" + getTransportationCharge() +
            ", driverBatta=" + getDriverBatta() +
            ", serviceabilityRangeKm=" + getServiceabilityRangeKm() +
            ", status='" + getStatus() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", user='" + getUser() + "'" +
            ", category='" + getCategory() + "'" +
            ", subcategory='" + getSubcategory() + "'" +
            ", warranty='" + getWarranty() + "'" +
            ", driverName='" + getDriverName() + "'" +
            ", adharNo='" + getAdharNo() + "'" +
            ", age='" + getAge() + "'" +
            ", licenseNo='" + getLicenseNo() + "'" +
            ", insuranceNo='" + getInsuranceNo() + "'" +
            ", operator='" + getOperator() + "'" +
            ", documents='" + getDocument() + "'" +
            "}";
    }
}
