package com.gearx7.app.service.dto;

import com.gearx7.app.domain.enumeration.MachineStatus;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private BigDecimal ratePerDay;

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

    private BigDecimal totalHourlyRate;

    private BigDecimal totalDailyRate;

    @NotNull(message = "Type is required")
    private Long typeId;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Subcategory is required")
    private Long subcategoryId;

    private String warranty;

    private String driverName;

    private String adharNo;

    private Integer age;

    private String licenseNo;

    private String insuranceNo;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

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

    public BigDecimal getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
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

    public BigDecimal getTotalHourlyRate() {
        return totalHourlyRate;
    }

    public void setTotalHourlyRate(BigDecimal totalHourlyRate) {
        this.totalHourlyRate = totalHourlyRate;
    }

    public BigDecimal getTotalDailyRate() {
        return totalDailyRate;
    }

    public void setTotalDailyRate(BigDecimal totalDailyRate) {
        this.totalDailyRate = totalDailyRate;
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

    @Override
    public String toString() {
        return (
            "MachineDTO{" +
            "id=" +
            id +
            ", brand='" +
            brand +
            '\'' +
            ", type='" +
            type +
            '\'' +
            ", tag='" +
            tag +
            '\'' +
            ", model='" +
            model +
            '\'' +
            ", vinNumber='" +
            vinNumber +
            '\'' +
            ", chassisNumber='" +
            chassisNumber +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", capacityTon=" +
            capacityTon +
            ", ratePerHour=" +
            ratePerHour +
            ", ratePerDay=" +
            ratePerDay +
            ", minimumUsageHours=" +
            minimumUsageHours +
            ", latitude=" +
            latitude +
            ", longitude=" +
            longitude +
            ", transportationCharge=" +
            transportationCharge +
            ", driverBatta=" +
            driverBatta +
            ", serviceabilityRangeKm=" +
            serviceabilityRangeKm +
            ", status=" +
            status +
            ", createdDate=" +
            createdDate +
            ", user=" +
            user +
            ", companyName='" +
            companyName +
            '\'' +
            ", partnerName='" +
            partnerName +
            '\'' +
            ", totalHourlyRate=" +
            totalHourlyRate +
            ", totalDailyRate=" +
            totalDailyRate +
            ", categoryId=" +
            categoryId +
            ", subcategoryId=" +
            subcategoryId +
            ", warranty='" +
            warranty +
            '\'' +
            ", driverName='" +
            driverName +
            '\'' +
            ", adharNo='" +
            adharNo +
            '\'' +
            ", age=" +
            age +
            ", licenseNo='" +
            licenseNo +
            '\'' +
            ", insuranceNo='" +
            insuranceNo +
            '\'' +
            '}'
        );
    }
}
