package com.gearx7.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gearx7.app.domain.enumeration.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Booking.
 */
@Entity
@Table(name = "booking")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_date_time", nullable = false)
    private Instant startDateTime;

    @NotNull
    @Column(name = "end_date_time", nullable = false)
    private Instant endDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "additional_details")
    private String additionalDetails;

    @Column(name = "worksite_image_url")
    private String worksiteImageUrl;

    @Column(name = "customer_lat")
    private Double customerLat;

    @Column(name = "customer_long")
    private Double customerLong;

    @Column(name = "created_date")
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Machine machine;

    @Column(name = "expected_price")
    private Double expectedPrice;

    @Column(name = "notes")
    private String notes;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Booking id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDateTime() {
        return this.startDateTime;
    }

    public Booking startDateTime(Instant startDateTime) {
        this.setStartDateTime(startDateTime);
        return this;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Instant getEndDateTime() {
        return this.endDateTime;
    }

    public Booking endDateTime(Instant endDateTime) {
        this.setEndDateTime(endDateTime);
        return this;
    }

    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BookingStatus getStatus() {
        return this.status;
    }

    public Booking status(BookingStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getAdditionalDetails() {
        return this.additionalDetails;
    }

    public Booking additionalDetails(String additionalDetails) {
        this.setAdditionalDetails(additionalDetails);
        return this;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public String getWorksiteImageUrl() {
        return this.worksiteImageUrl;
    }

    public Booking worksiteImageUrl(String worksiteImageUrl) {
        this.setWorksiteImageUrl(worksiteImageUrl);
        return this;
    }

    public void setWorksiteImageUrl(String worksiteImageUrl) {
        this.worksiteImageUrl = worksiteImageUrl;
    }

    public Double getCustomerLat() {
        return this.customerLat;
    }

    public Booking customerLat(Double customerLat) {
        this.setCustomerLat(customerLat);
        return this;
    }

    public void setCustomerLat(Double customerLat) {
        this.customerLat = customerLat;
    }

    public Double getCustomerLong() {
        return this.customerLong;
    }

    public Booking customerLong(Double customerLong) {
        this.setCustomerLong(customerLong);
        return this;
    }

    public void setCustomerLong(Double customerLong) {
        this.customerLong = customerLong;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Booking createdDate(Instant createdDate) {
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

    public Booking user(User user) {
        this.setUser(user);
        return this;
    }

    public Machine getMachine() {
        return this.machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Booking machine(Machine machine) {
        this.setMachine(machine);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return getId() != null && getId().equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booking{" +
            "id=" + getId() +
            ", startDateTime='" + getStartDateTime() + "'" +
            ", endDateTime='" + getEndDateTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", additionalDetails='" + getAdditionalDetails() + "'" +
            ", worksiteImageUrl='" + getWorksiteImageUrl() + "'" +
            ", customerLat=" + getCustomerLat() +
            ", customerLong=" + getCustomerLong() +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
