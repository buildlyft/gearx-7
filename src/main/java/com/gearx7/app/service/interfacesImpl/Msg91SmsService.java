package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.domain.User;
import com.gearx7.app.service.dto.DateTimeUtil;
import com.gearx7.app.service.interfaces.SmsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Msg91SmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(Msg91SmsService.class);

    @Value("${sms.msg91.auth-key}")
    private String authKey;

    @Value("${sms.msg91.sender-id}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();

    // =========================================================
    // BOOKING CREATED - USER
    // =========================================================

    @Override
    public void sendBookingCreatedSmsToUser(Booking booking) {
        User user = booking.getUser();

        if (!isValidUser(user)) {
            log.warn("Booking-created USER SMS skipped | reason=invalid_user_or_phone_missing | bookingId={}", booking.getId());

            return;
        }

        log.info(
            "Preparing booking-created USER SMS | bookingId={} | userId={} | phone={}",
            booking.getId(),
            user.getId(),
            maskPhone(user.getPhone())
        );

        String startDate = DateTimeUtil.formatInstantForSms(booking.getStartDateTime());

        String endDate = DateTimeUtil.formatInstantForSms(booking.getEndDateTime());

        String message =
            "Hello " +
            user.getFirstName() +
            " " +
            user.getLastName() +
            ", your booking " +
            booking.getId() +
            " has been created successfully." +
            " Machine: " +
            booking.getMachine().getBrand() +
            ". From: " +
            startDate +
            ". To: " +
            endDate +
            ". Status: " +
            booking.getStatus() +
            ". Thanks for choosing Gearx7.";

        sendSms(user.getPhone(), message, "BOOKING_CREATED_USER", booking.getId());
    }

    // =========================================================
    // BOOKING CREATED - PARTNER
    // =========================================================

    @Override
    public void sendBookingCreatedSmsToPartner(Booking booking) {
        User partner = getPartner(booking);

        if (!isValidUser(partner)) {
            log.warn("Booking-created PARTNER SMS skipped | reason=invalid_partner_or_phone_missing | bookingId={}", booking.getId());

            return;
        }

        log.info(
            "Preparing booking-created PARTNER SMS | bookingId={} | partnerId={} | phone={}",
            booking.getId(),
            partner.getId(),
            maskPhone(partner.getPhone())
        );

        String startDate = DateTimeUtil.formatInstantForSms(booking.getStartDateTime());

        String endDate = DateTimeUtil.formatInstantForSms(booking.getEndDateTime());

        String message =
            "Hello " +
            partner.getFirstName() +
            " " +
            partner.getLastName() +
            ", your machine has been booked successfully." +
            " Booking ID: " +
            booking.getId() +
            ". Customer: " +
            booking.getUser().getFirstName() +
            ". Machine: " +
            booking.getMachine().getBrand() +
            ". From: " +
            startDate +
            ". To: " +
            endDate +
            ". Status: " +
            booking.getStatus() +
            ". Thanks for choosing Gearx7.";

        sendSms(partner.getPhone(), message, "BOOKING_CREATED_PARTNER", booking.getId());
    }

    // =========================================================
    // BOOKING ACCEPTED
    // =========================================================

    @Override
    public void sendBookingAcceptedSmsToUser(Booking booking) {
        User user = booking.getUser();

        if (!isValidUser(user)) {
            log.warn("Booking-accepted SMS skipped | reason=invalid_user_or_phone_missing | bookingId={}", booking.getId());

            return;
        }

        log.info(
            "Preparing booking-accepted SMS | bookingId={} | userId={} | phone={}",
            booking.getId(),
            user.getId(),
            maskPhone(user.getPhone())
        );

        String message =
            "Hello " +
            user.getFirstName() +
            " " +
            user.getLastName() +
            ", your booking " +
            booking.getId() +
            " has been ACCEPTED." +
            " Thank you for choosing Gearx7.";

        sendSms(user.getPhone(), message, "BOOKING_ACCEPTED", booking.getId());
    }

    // =========================================================
    // BOOKING REJECTED
    // =========================================================

    @Override
    public void sendBookingRejectedSmsToUser(Booking booking) {
        User user = booking.getUser();

        if (!isValidUser(user)) {
            log.warn("Booking-rejected SMS skipped | reason=invalid_user_or_phone_missing | bookingId={}", booking.getId());

            return;
        }

        log.info(
            "Preparing booking-rejected SMS | bookingId={} | userId={} | phone={}",
            booking.getId(),
            user.getId(),
            maskPhone(user.getPhone())
        );

        String message =
            "Hello " +
            user.getFirstName() +
            " " +
            user.getLastName() +
            ", your booking " +
            booking.getId() +
            " has been REJECTED." +
            " Please try another machine." +
            " Thank you for choosing Gearx7.";

        sendSms(user.getPhone(), message, "BOOKING_REJECTED", booking.getId());
    }

    // =========================================================
    // BOOKING CANCELLED - USER
    // =========================================================

    @Override
    public void sendBookingCancelledSmsToUser(Booking booking) {
        User user = booking.getUser();

        if (!isValidUser(user)) {
            log.warn("Booking-cancelled USER SMS skipped | reason=invalid_user_or_phone_missing | bookingId={}", booking.getId());

            return;
        }

        log.info(
            "Preparing booking-cancelled USER SMS | bookingId={} | userId={} | phone={}",
            booking.getId(),
            user.getId(),
            maskPhone(user.getPhone())
        );

        String message =
            "Hello " +
            user.getFirstName() +
            " " +
            user.getLastName() +
            ", your booking " +
            booking.getId() +
            " has been cancelled successfully." +
            " Thank you for choosing Gearx7.";

        sendSms(user.getPhone(), message, "BOOKING_CANCELLED_USER", booking.getId());
    }

    // =========================================================
    // BOOKING CANCELLED - PARTNER
    // =========================================================

    @Override
    public void sendBookingCancelledSmsToPartner(Booking booking) {
        User partner = getPartner(booking);

        if (!isValidUser(partner)) {
            log.warn("Booking-cancelled PARTNER SMS skipped | reason=invalid_partner_or_phone_missing | bookingId={}", booking.getId());

            return;
        }

        log.info(
            "Preparing booking-cancelled PARTNER SMS | bookingId={} | partnerId={} | phone={}",
            booking.getId(),
            partner.getId(),
            maskPhone(partner.getPhone())
        );

        String message =
            "Hello " +
            partner.getFirstName() +
            " " +
            partner.getLastName() +
            ", booking " +
            booking.getId() +
            " has been cancelled by the customer." +
            " Machine: " +
            booking.getMachine().getBrand() +
            ". Thank you for choosing Gearx7.";

        sendSms(partner.getPhone(), message, "BOOKING_CANCELLED_PARTNER", booking.getId());
    }

    // =========================================================
    // COMMON SMS METHOD
    // =========================================================

    private void sendSms(String phone, String message, String smsType, Long bookingId) {
        try {
            log.info("Sending SMS | type={} | bookingId={} | phone={}", smsType, bookingId, maskPhone(phone));

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("authkey", authKey);

            Map<String, Object> sms = new HashMap<>();

            sms.put("message", message);

            sms.put("to", List.of("91" + phone));

            Map<String, Object> body = new HashMap<>();

            body.put("sender", senderId);

            body.put("route", "4");

            body.put("country", "91");

            body.put("sms", List.of(sms));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity("https://api.msg91.com/api/v2/sendsms", request, String.class);

            log.info("SMS sent successfully | type={} | bookingId={} | response={}", smsType, bookingId, response.getBody());
        } catch (Exception ex) {
            log.error("SMS sending failed | type={} | bookingId={} | reason={}", smsType, bookingId, ex.getMessage(), ex);
        }
    }

    // =========================================================
    // USER VALIDATION
    // =========================================================

    private boolean isValidUser(User user) {
        return user != null && user.getPhone() != null && !user.getPhone().trim().isEmpty();
    }

    // =========================================================
    // GET PARTNER
    // =========================================================

    private User getPartner(Booking booking) {
        if (booking == null || booking.getMachine() == null || booking.getMachine().getUser() == null) {
            return null;
        }

        return booking.getMachine().getUser();
    }

    // =========================================================
    // MASK PHONE
    // =========================================================

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }

        return "******" + phone.substring(phone.length() - 4);
    }
}
