package com.gearx7.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubcategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Subcategory getSubcategorySample1() {
        return new Subcategory().id(1L).name("name1").description("description1");
    }

    public static Subcategory getSubcategorySample2() {
        return new Subcategory().id(2L).name("name2").description("description2");
    }

    public static Subcategory getSubcategoryRandomSampleGenerator() {
        return new Subcategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
