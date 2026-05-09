package com.gearx7.app.service.interfaces;

public interface OtpService {
    String sendOtpToUserWhileLogin(String phoneNumber);
    boolean verifyOtpToLogin(String phoneNumber, String otp);
}
