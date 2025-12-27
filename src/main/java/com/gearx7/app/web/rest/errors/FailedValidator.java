package com.gearx7.app.web.rest.errors;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.User;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailedValidator {

    private static final Logger log = LoggerFactory.getLogger(FailedValidator.class);

    public static void validateInputParameters(Booking booking) {
        // check booking, machine, user, machineId, userId

        if (booking == null) {
            log.error("Booking validation failed: booking is null");
            throw new BadRequestAlertException("Booking cannot be null", "Booking", "bookingNull");
        }

        Machine machine = booking.getMachine();
        User user = booking.getUser();

        if (machine == null) {
            log.error("Booking validation failed: machine is null");
            throw new BadRequestAlertException("Machine cannot be null", "Booking", "machineNull");
        }

        if (user == null) {
            log.error("Booking validation failed: user is null");
            throw new BadRequestAlertException("User cannot be null", "Booking", "userNull");
        }

        if (machine.getId() == null) {
            log.error("Booking validation failed: machineId is null");
            throw new BadRequestAlertException("Machine ID cannot be null or empty", "Booking", "machineIdNull");
        }

        if (user.getId() == null) {
            log.error("Booking validation failed: userId is null");
            throw new BadRequestAlertException("User ID cannot be null or empty", "Booking", "userIdNull");
        }

        // check startDateTime and endDateTime

        /*  Instant start = booking.getStartDateTime();
        Instant end = booking.getEndDateTime();
        Instant now = Instant.now();
        if (start == null) {
            log.error("Booking validation failed: startDateTime is null");
            throw new BadRequestAlertException("Start date and time is required", "Booking", "startDateNull");
        }

        if (end == null) {
            log.error("Booking validation failed: endDateTime is null");
            throw new BadRequestAlertException("End date and time is required", "Booking", "endDateNull");
        }

        if (start.isBefore(now)) {
            log.error("Booking validation failed: startDateTime is in the past ({})", start);
            throw new BadRequestAlertException("Start date and time must be present or future", "Booking", "startDatePast");
        }

        if (end.isBefore(now)) {
            log.error("Booking validation failed: endDateTime is in the past ({})", end);
            throw new BadRequestAlertException("End date and time must be present or future", "Booking", "endDatePast");
        }

        if (!end.isAfter(start)) {
            log.error("Booking validation failed: endDateTime ({}) is not after startDateTime ({})", end, start);
            throw new BadRequestAlertException("End date and time must be after start date and time", "Booking", "endBeforeStart");
        }*/

        log.debug("Booking validation successful (userId={}, machineId={})", user.getId(), machine.getId());
    }
}
