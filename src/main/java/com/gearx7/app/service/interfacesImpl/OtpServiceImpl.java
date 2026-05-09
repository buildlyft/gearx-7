package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.service.interfaces.OtpService;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OtpServiceImpl implements OtpService {

    private final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${otp.msg91.authkey}")
    private String authKey;

    @Value("${otp.msg91.otp-template-id}")
    private String templateId;

    private static final String SEND_OTP_URL = "https://control.msg91.com/api/v5/otp";
    private static final String VERIFY_OTP_URL = "https://control.msg91.com/api/v5/otp/verify";

    public OtpServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
    }

    @Override
    public String sendOtpToUserWhileLogin(String phoneNumber) {
        log.info("OTP_REQUEST_INITIATED | phoneNumber={}", mask(phoneNumber));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authkey", authKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("mobile", "91" + phoneNumber);
            body.put("template_id", templateId);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(SEND_OTP_URL, request, String.class);
            String responseBody = response.getBody();

            log.info("OTP_SENT | phoneNumber={} | status={}", mask(phoneNumber), response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("OTP_SEND_FAILED | phoneNumber={} | response={}", mask(phoneNumber), response.getBody());
                throw new RuntimeException("Failed to send OTP");
            }
            return "OTP_SENT";
        } catch (Exception ex) {
            log.error("OTP_SEND_EXCEPTION | phoneNumber={} | error={}", mask(phoneNumber), ex.getMessage(), ex);
            throw new RuntimeException("OTP service unavailable");
        }
    }

    @Override
    public boolean verifyOtpToLogin(String phoneNumber, String otp) {
        log.info("OTP_VERIFICATION_STARTED | phoneNumber={}", mask(phoneNumber));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authkey", authKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("mobile", "91" + phoneNumber);
            body.put("otp", otp);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(VERIFY_OTP_URL, request, String.class);

            log.info("OTP_VERIFICATION_RESPONSE | phoneNumber={} | status={}", mask(phoneNumber), response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("OTP_INVALID | phoneNumber={} | response={}", mask(phoneNumber), response.getBody());
                return false;
            }

            boolean success = response.getBody() != null && response.getBody().toLowerCase().contains("success");
            if (success) {
                log.info("OTP_VERIFIED_SUCCESS | phoneNumber={}", mask(phoneNumber));
            } else {
                log.warn("OTP_VERIFICATION_FAILED | phoneNumber={} | response={}", mask(phoneNumber), response.getBody());
            }

            return success;
        } catch (Exception ex) {
            log.error("OTP_VERIFY_EXCEPTION | phoneNumber={} | error={}", mask(phoneNumber), ex.getMessage(), ex);
            throw new RuntimeException("OTP verification failed");
        }
    }

    private String mask(String phone) {
        return phone.substring(0, 2) + "******" + phone.substring(phone.length() - 2);
    }
}
