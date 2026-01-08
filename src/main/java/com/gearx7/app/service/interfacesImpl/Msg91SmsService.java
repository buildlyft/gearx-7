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

    @Value("${sms.msg91.booking-user-template-id}")
    private String bookingUserTemplateId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send Booking alert SMS to User
     *
     * @param booking
     */

    @Override
    public void sendBookingCreatedSmsToUser(Booking booking) {
        User user = booking.getUser();

        if (user == null || user.getPhone() == null || user.getPhone().isEmpty()) {
            log.warn("SMS skipped : User phone missing | bookingId={}", booking.getId());
            return;
        }

        log.info("Sending booking-created SMS to USER | bookingId={} | phone={}", booking.getId(), user.getPhone());

        try {
            // Instant ==> 2026-12-19T00:00:00Z
            // Format to SMS friendly format ==> 2025-12-19 05:30:00
            String startDate = DateTimeUtil.formatInstantForSms(booking.getStartDateTime());

            String endDate = DateTimeUtil.formatInstantForSms(booking.getEndDateTime());

            String message =
                "Hello " +
                user.getFirstName() +
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authkey", authKey);

            Map<String, Object> sms = new HashMap<>();
            sms.put("message", message);
            sms.put("to", List.of("91" + user.getPhone()));

            Map<String, Object> body = new HashMap<>();
            body.put("sender", senderId); // VEHTRK
            body.put("route", "4"); // Transactional
            body.put("country", "91");
            body.put("sms", List.of(sms));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // 5️⃣ Send SMS
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.msg91.com/api/v2/sendsms", request, String.class);

            log.info("Booking SMS sent | bookingId={} | phone={} | response={}", booking.getId(), user.getPhone(), response.getBody());
        } catch (Exception ex) {
            log.error("SMS sending failed (ignored) | bookingId={}", booking.getId(), ex);
        }
    }
}
