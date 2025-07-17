package com.gearx7.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer()
            .id(1L)
            .name("name1")
            .email("email1")
            .phone("phone1")
            .address("address1")
            .pincode("pincode1")
            .location("location1");
    }

    public static Customer getCustomerSample2() {
        return new Customer()
            .id(2L)
            .name("name2")
            .email("email2")
            .phone("phone2")
            .address("address2")
            .pincode("pincode2")
            .location("location2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .pincode(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString());
    }
}
