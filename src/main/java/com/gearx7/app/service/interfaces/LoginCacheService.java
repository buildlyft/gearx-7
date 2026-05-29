package com.gearx7.app.service.interfaces;

public interface LoginCacheService {
    void store(String phone, String value);
    String get(String phone);
    void remove(String phone);
    boolean canSendOtp(String phone);
}
