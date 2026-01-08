package com.gearx7.app.service.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    private static final DateTimeFormatter SMS_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Prevent instantiation
    private DateTimeUtil() {}

    /**
     * Formats an Instant to IST in SMS-friendly format.
     *
     * @param instant Instant to format
     * @return formatted date-time string or "N/A" if null
     */
    public static String formatInstantForSms(Instant instant) {
        if (instant == null) {
            return "N/A";
        }
        return instant.atZone(IST_ZONE).format(SMS_DATE_FORMATTER);
    }
}
