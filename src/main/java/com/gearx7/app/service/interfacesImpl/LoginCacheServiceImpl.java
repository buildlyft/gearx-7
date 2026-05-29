package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.service.interfaces.LoginCacheService;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoginCacheServiceImpl implements LoginCacheService {

    private static class CacheEntry {

        String login;
        Instant expiry;
        Instant lastOtpSentTime;
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public void store(String phone, String login) {
        CacheEntry entry = new CacheEntry();
        entry.login = login;
        entry.expiry = Instant.now().plusSeconds(300); // 5 min
        entry.lastOtpSentTime = Instant.now();
        cache.put(phone, entry);
    }

    @Override
    public String get(String phone) {
        CacheEntry entry = cache.get(phone);

        if (entry == null || entry.expiry.isBefore(Instant.now())) {
            cache.remove(phone);
            return null;
        }

        return entry.login;
    }

    @Override
    public void remove(String phone) {
        cache.remove(phone);
    }

    @Override
    public boolean canSendOtp(String phone) {
        CacheEntry entry = cache.get(phone);

        if (entry == null) return true;

        if (entry.lastOtpSentTime == null) return true;

        return entry.lastOtpSentTime.plusSeconds(60).isBefore(Instant.now());
    }
}
