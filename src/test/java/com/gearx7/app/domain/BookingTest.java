package com.gearx7.app.domain;

import static com.gearx7.app.domain.BookingTestSamples.*;
import static com.gearx7.app.domain.MachineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gearx7.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Booking.class);
        Booking booking1 = getBookingSample1();
        Booking booking2 = new Booking();
        assertThat(booking1).isNotEqualTo(booking2);

        booking2.setId(booking1.getId());
        assertThat(booking1).isEqualTo(booking2);

        booking2 = getBookingSample2();
        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void machineTest() throws Exception {
        Booking booking = getBookingRandomSampleGenerator();
        Machine machineBack = getMachineRandomSampleGenerator();

        booking.setMachine(machineBack);
        assertThat(booking.getMachine()).isEqualTo(machineBack);

        booking.machine(null);
        assertThat(booking.getMachine()).isNull();
    }
}
