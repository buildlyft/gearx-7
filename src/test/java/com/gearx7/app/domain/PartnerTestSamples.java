package com.gearx7.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartnerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Partner getPartnerSample1() {
        return new Partner()
            .id(1L)
            .name("name1")
            .companyName("companyName1")
            .email("email1")
            .phone("phone1")
            .address("address1")
            .preferredContactTime("preferredContactTime1")
            .gstNumber("gstNumber1")
            .panNumber("panNumber1");
    }

    public static Partner getPartnerSample2() {
        return new Partner()
            .id(2L)
            .name("name2")
            .companyName("companyName2")
            .email("email2")
            .phone("phone2")
            .address("address2")
            .preferredContactTime("preferredContactTime2")
            .gstNumber("gstNumber2")
            .panNumber("panNumber2");
    }

    public static Partner getPartnerRandomSampleGenerator() {
        return new Partner()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .companyName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .preferredContactTime(UUID.randomUUID().toString())
            .gstNumber(UUID.randomUUID().toString())
            .panNumber(UUID.randomUUID().toString());
    }
}
