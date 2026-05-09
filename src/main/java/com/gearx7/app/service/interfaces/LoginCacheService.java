package com.gearx7.app.service.interfaces;

import org.springframework.security.core.Authentication;

public interface LoginCacheService {
    void store(String phone, Authentication auth);
    Authentication get(String phone);
    void remove(String phone);
    boolean canSendOtp(String phone);
}
