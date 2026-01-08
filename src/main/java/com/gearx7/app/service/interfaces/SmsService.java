package com.gearx7.app.service.interfaces;

import com.gearx7.app.domain.Booking;

public interface SmsService {
    void sendBookingCreatedSmsToUser(Booking booking);
}
