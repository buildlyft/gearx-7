package com.gearx7.app.service.interfaces;

import com.gearx7.app.domain.Booking;

public interface SmsService {
    void sendBookingCreatedSmsToUser(Booking booking);

    void sendBookingCreatedSmsToPartner(Booking booking);

    void sendBookingAcceptedSmsToUser(Booking booking);

    void sendBookingRejectedSmsToUser(Booking booking);

    void sendBookingCancelledSmsToUser(Booking booking);

    void sendBookingCancelledSmsToPartner(Booking booking);
}
