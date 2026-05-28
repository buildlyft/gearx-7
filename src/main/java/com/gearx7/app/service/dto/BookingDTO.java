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

    private Instant createdDate;

    private Double expectedPrice;

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

    public Double getExpectedPrice() {
        return expectedPrice;
    }

    public void setExpectedPrice(Double expectedPrice) {
        this.expectedPrice = expectedPrice;
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
            ", createdDate=" +
            createdDate +
            ", expectedPrice=" +
            expectedPrice +
            ", notes='" +
            notes +
            '\'' +
            ", cancelledDate=" +
            cancelledDate +
            ", user = " +
            user +
            ", customerAddress='" +
            customerAddress +
            '\'' +
            //            ", userId=" + userId +
            //            ", customerName='" + customerName + '\'' +
            //            ", customerPhone='" + customerPhone + '\'' +
            ", machine=" +
            machine +
            '}'
        );
    }
}
