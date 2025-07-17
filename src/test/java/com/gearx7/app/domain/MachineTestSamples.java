package com.gearx7.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MachineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Machine getMachineSample1() {
        return new Machine()
            .id(1L)
            .brand("brand1")
            .type("type1")
            .tag("tag1")
            .model("model1")
            .vinNumber("vinNumber1")
            .chassisNumber("chassisNumber1")
            .description("description1")
            .capacityTon(1)
            .minimumUsageHours(1)
            .serviceabilityRangeKm(1);
    }

    public static Machine getMachineSample2() {
        return new Machine()
            .id(2L)
            .brand("brand2")
            .type("type2")
            .tag("tag2")
            .model("model2")
            .vinNumber("vinNumber2")
            .chassisNumber("chassisNumber2")
            .description("description2")
            .capacityTon(2)
            .minimumUsageHours(2)
            .serviceabilityRangeKm(2);
    }

    public static Machine getMachineRandomSampleGenerator() {
        return new Machine()
            .id(longCount.incrementAndGet())
            .brand(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .tag(UUID.randomUUID().toString())
            .model(UUID.randomUUID().toString())
            .vinNumber(UUID.randomUUID().toString())
            .chassisNumber(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .capacityTon(intCount.incrementAndGet())
            .minimumUsageHours(intCount.incrementAndGet())
            .serviceabilityRangeKm(intCount.incrementAndGet());
    }
}
