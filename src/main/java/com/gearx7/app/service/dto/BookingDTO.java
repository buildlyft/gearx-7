package com.gearx7.app.service.dto;

import com.gearx7.app.domain.User;
import com.gearx7.app.domain.enumeration.BookingStatus;
import java.io.Serializable;
import java.time.Instant;

public class BookingDTO implements Serializable {

    private Long id;

    private Instant startDateTime;

    private Instant endDateTime;

    private BookingStatus status;

    private String additionalDetails;

    private String worksiteImageUrl;

    private Double customerLat;

    private Double customerLong;

    private Double rateContractRate;

    private Double rateContractDriverBatta;

    private Double rateContractTransport;

    private Double listedRate;

    private Double listedDriverBatta;

    private Double listedTransport;

    private Instant createdDate;

    private String notes;

    private Instant cancelledDate;

    private String customerAddress;

    //    // customer info
    //    private Long userId;
    //
    //    private String customerName;
    //
    //    private String customerPhone;

    private User user;

    // machine info
    private MachineDTO machine;

    // getters setters

    public Double getRateContractRate() {
        return rateContractRate;
    }

    public void setRateContractRate(Double rateContractRate) {
        this.rateContractRate = rateContractRate;
    }

    public Double getRateContractDriverBatta() {
        return rateContractDriverBatta;
    }

    public void setRateContractDriverBatta(Double rateContractDriverBatta) {
        this.rateContractDriverBatta = rateContractDriverBatta;
    }

    public Double getRateContractTransport() {
        return rateContractTransport;
    }

    public void setRateContractTransport(Double rateContractTransport) {
        this.rateContractTransport = rateContractTransport;
    }

    public Double getListedRate() {
        return listedRate;
    }

    public void setListedRate(Double listedRate) {
        this.listedRate = listedRate;
    }

    public Double getListedDriverBatta() {
        return listedDriverBatta;
    }

    public void setListedDriverBatta(Double listedDriverBatta) {
        this.listedDriverBatta = listedDriverBatta;
    }

    public Double getListedTransport() {
        return listedTransport;
    }

    public void setListedTransport(Double listedTransport) {
        this.listedTransport = listedTransport;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public String getWorksiteImageUrl() {
        return worksiteImageUrl;
    }

    public void setWorksiteImageUrl(String worksiteImageUrl) {
        this.worksiteImageUrl = worksiteImageUrl;
    }

    public Double getCustomerLat() {
        return customerLat;
    }

    public void setCustomerLat(Double customerLat) {
        this.customerLat = customerLat;
    }

    public Double getCustomerLong() {
        return customerLong;
    }

    public void setCustomerLong(Double customerLong) {
        this.customerLong = customerLong;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Instant cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    //    public Long getUserId() {
    //        return userId;
    //    }
    //
    //    public void setUserId(Long userId) {
    //        this.userId = userId;
    //    }
    //
    //    public String getCustomerName() {
    //        return customerName;
    //    }
    //
    //    public void setCustomerName(String customerName) {
    //        this.customerName = customerName;
    //    }
    //
    //    public String getCustomerPhone() {
    //        return customerPhone;
    //    }
    //
    //    public void setCustomerPhone(String customerPhone) {
    //        this.customerPhone = customerPhone;
    //    }

    public MachineDTO getMachine() {
        return machine;
    }

    public void setMachine(MachineDTO machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        return (
            "BookingDTO{" +
            "id=" +
            id +
            ", startDateTime=" +
            startDateTime +
            ", endDateTime=" +
            endDateTime +
            ", status=" +
            status +
            ", additionalDetails='" +
            additionalDetails +
            '\'' +
            ", worksiteImageUrl='" +
            worksiteImageUrl +
            '\'' +
            ", customerLat=" +
            customerLat +
            ", customerLong=" +
            customerLong +
            ", rateContractRate=" +
            rateContractRate +
            ", rateContractDriverBatta=" +
            rateContractDriverBatta +
            ", rateContractTransport=" +
            rateContractTransport +
            ", listedRate=" +
            listedRate +
            ", listedDriverBatta=" +
            listedDriverBatta +
            ", listedTransport=" +
            listedTransport +
            ", createdDate=" +
            createdDate +
            ", notes='" +
            notes +
            '\'' +
            ", cancelledDate=" +
            cancelledDate +
            ", customerAddress='" +
            customerAddress +
            '\'' +
            ", user=" +
            user +
            ", machine=" +
            machine +
            '}'
        );
    }
}
