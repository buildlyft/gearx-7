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

    @Value("${sms.templates.booking-created-user}")
    private String bookingCreatedUserTemplate;

    @Value("${sms.templates.booking-created-partner}")
    private String bookingCreatedPartnerTemplate;

    @Value("${sms.templates.booking-accepted}")
    private String bookingAcceptedTemplate;

    @Value("${sms.templates.booking-rejected}")
    private String bookingRejectedTemplate;

    @Value("${sms.templates.booking-cancelled-user}")
    private String bookingCancelledUserTemplate;

    @Value("${sms.templates.booking-cancelled-partner}")
    private String bookingCancelledPartnerTemplate;

    @Value("${sms.msg91.booking-created-user-template-id}")
    private String bookingCreatedUserTemplateId;

    @Value("${sms.msg91.booking-created-partner-template-id}")
    private String bookingCreatedPartnerTemplateId;

    @Value("${sms.msg91.booking-accepted-template-id}")
    private String bookingAcceptedTemplateId;

    @Value("${sms.msg91.booking-rejected-template-id}")
    private String bookingRejectedTemplateId;

    @Value("${sms.msg91.booking-cancelled-user-template-id}")
    private String bookingCancelledUserTemplateId;

    @Value("${sms.msg91.booking-cancelled-partner-template-id}")
    private String bookingCancelledPartnerTemplateId;

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

        String firstName = user.getFirstName() != null ? user.getFirstName() : "Customer";
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        String machineName = booking.getMachine() != null && booking.getMachine().getName() != null
            ? booking.getMachine().getName()
            : "Equipment";

        Map<String, String> vars = new HashMap<>();

        vars.put("VAR1", firstName);
        vars.put("VAR2", lastName);
        vars.put("VAR3", booking.getId().toString());
        vars.put("VAR4", machineName);
        vars.put("VAR5", startDate);
        vars.put("VAR6", endDate);
        vars.put("VAR7", booking.getStatus().toString());

        sendTemplateSms(bookingCreatedUserTemplateId, user.getPhone(), vars, "BOOKING_CREATED_USER", booking.getId());
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

        String partnerFirstName = partner.getFirstName() != null ? partner.getFirstName() : "Partner";
        String partnerLastName = partner.getLastName() != null ? partner.getLastName() : "";

        String customerFirstName = booking.getUser() != null && booking.getUser().getFirstName() != null
            ? booking.getUser().getFirstName()
            : "Customer";

        String customerLastName = booking.getUser() != null && booking.getUser().getLastName() != null
            ? booking.getUser().getLastName()
            : "";

        String customerName = (customerFirstName + " " + customerLastName).trim();

        String machineName = booking.getMachine() != null && booking.getMachine().getName() != null
            ? booking.getMachine().getName()
            : "Equipment";

        Map<String, String> vars = new HashMap<>();

        vars.put("VAR1", partnerFirstName);
        vars.put("VAR2", partnerLastName);
        vars.put("VAR3", booking.getId().toString());
        vars.put("VAR4", customerName);
        vars.put("VAR5", machineName);
        vars.put("VAR6", startDate);
        vars.put("VAR7", endDate);
        vars.put("VAR8", booking.getStatus().toString());

        sendTemplateSms(bookingCreatedPartnerTemplateId, partner.getPhone(), vars, "BOOKING_CREATED_PARTNER", booking.getId());
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

        String firstName = user.getFirstName() != null ? user.getFirstName() : "Customer";
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        Map<String, String> vars = new HashMap<>();

        vars.put("VAR1", firstName);
        vars.put("VAR2", lastName);
        vars.put("VAR3", booking.getId().toString());

        sendTemplateSms(bookingAcceptedTemplateId, user.getPhone(), vars, "BOOKING_ACCEPTED", booking.getId());
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

        String firstName = user.getFirstName() != null ? user.getFirstName() : "Customer";
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        Map<String, String> vars = new HashMap<>();

        vars.put("VAR1", firstName);
        vars.put("VAR2", lastName);
        vars.put("VAR3", booking.getId().toString());

        sendTemplateSms(bookingRejectedTemplateId, user.getPhone(), vars, "BOOKING_REJECTED", booking.getId());
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

        String firstName = user.getFirstName() != null ? user.getFirstName() : "Customer";
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        Map<String, String> vars = new HashMap<>();

        vars.put("VAR1", firstName);
        vars.put("VAR2", lastName);
        vars.put("VAR3", booking.getId().toString());

        sendTemplateSms(bookingCancelledUserTemplateId, user.getPhone(), vars, "BOOKING_CANCELLED_USER", booking.getId());
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

        String firstName = partner.getFirstName() != null ? partner.getFirstName() : "Partner";
        String lastName = partner.getLastName() != null ? partner.getLastName() : "";

        String machineName = booking.getMachine() != null && booking.getMachine().getName() != null
            ? booking.getMachine().getName()
            : "Equipment";

        Map<String, String> vars = new HashMap<>();

        vars.put("VAR1", firstName);
        vars.put("VAR2", lastName);
        vars.put("VAR3", booking.getId().toString());
        vars.put("VAR4", machineName);

        sendTemplateSms(bookingCancelledPartnerTemplateId, partner.getPhone(), vars, "BOOKING_CANCELLED_PARTNER", booking.getId());
    }

    // =========================================================
    // COMMON SMS METHOD
    // =========================================================

    private void sendTemplateSms(String templateId, String phone, Map<String, String> variables, String smsType, Long bookingId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authkey", authKey);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("mobiles", "91" + phone);

            recipient.putAll(variables);

            Map<String, Object> body = new HashMap<>();
            body.put("template_id", templateId);
            body.put("recipients", List.of(recipient));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity("https://control.msg91.com/api/v5/flow", request, String.class);
            log.info("TemplateId={}", templateId);
            log.info("Phone={}", phone);
            log.info("Variables={}", variables);
            log.info("MSG91 SMS SUCCESS | type={} | bookingId={} | response={}", smsType, bookingId, response.getBody());
        } catch (Exception ex) {
            log.error("MSG91 SMS FAILED | type={} | bookingId={} | reason={}", smsType, bookingId, ex.getMessage(), ex);
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
