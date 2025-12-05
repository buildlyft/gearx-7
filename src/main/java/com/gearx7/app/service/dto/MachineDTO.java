package com.gearx7.app.service.dto;

import com.gearx7.app.domain.enumeration.MachineStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gearx7.app.domain.Machine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MachineDTO implements Serializable {

    private Long id;

    @NotNull
    private String brand;

    @NotNull
    private String type;

    private String tag;

    private String model;

    private String vinNumber;

    private String chassisNumber;

    private String description;

    private Integer capacityTon;

    @NotNull
    private BigDecimal ratePerHour;

    private Integer minimumUsageHours;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private BigDecimal transportationCharge;

    private BigDecimal driverBatta;

    private Integer serviceabilityRangeKm;

    @NotNull
    private MachineStatus status;

    @NotNull
    private Instant createdDate;

    private UserDTO user;

    private String companyName;

    private String partnerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacityTon() {
        return capacityTon;
    }

    public void setCapacityTon(Integer capacityTon) {
        this.capacityTon = capacityTon;
    }

    public BigDecimal getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public Integer getMinimumUsageHours() {
        return minimumUsageHours;
    }

    public void setMinimumUsageHours(Integer minimumUsageHours) {
        this.minimumUsageHours = minimumUsageHours;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getTransportationCharge() {
        return transportationCharge;
    }

    public void setTransportationCharge(BigDecimal transportationCharge) {
        this.transportationCharge = transportationCharge;
    }

    public BigDecimal getDriverBatta() {
        return driverBatta;
    }

    public void setDriverBatta(BigDecimal driverBatta) {
        this.driverBatta = driverBatta;
    }

    public Integer getServiceabilityRangeKm() {
        return serviceabilityRangeKm;
    }

    public void setServiceabilityRangeKm(Integer serviceabilityRangeKm) {
        this.serviceabilityRangeKm = serviceabilityRangeKm;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MachineDTO)) {
            return false;
        }

        MachineDTO machineDTO = (MachineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, machineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MachineDTO{" +
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
            ", minimumUsageHours=" + getMinimumUsageHours() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", transportationCharge=" + getTransportationCharge() +
            ", driverBatta=" + getDriverBatta() +
            ", serviceabilityRangeKm=" + getServiceabilityRangeKm() +
            ", status='" + getStatus() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", user=" + getUser() +
            ", companyName='" + getCompanyName() + "'" +
            ", partnerName='" + getPartnerName() + "'" +
            "}";
    }
}
