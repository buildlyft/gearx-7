package com.gearx7.app.service;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.User;
import com.gearx7.app.domain.enumeration.BookingStatus;
import com.gearx7.app.repository.*;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Booking}.
 */
@Service
@Transactional
public class BookingService {

    private final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;

    private final MachineRepository machineRepository;

    private final UserRepository userRepository;

    //    private final MachineOperatorRepository machineOperatorRepository;
    //
    //    private final VehicleDocumentRepository vehicleDocumentRepository;

    public BookingService(
        BookingRepository bookingRepository,
        MachineRepository machineRepository,
        UserRepository userRepository
        //  MachineOperatorRepository machineOperatorRepository,
        // VehicleDocumentRepository vehicleDocumentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        //        this.machineOperatorRepository=machineOperatorRepository;
        //        this.vehicleDocumentRepository=vehicleDocumentRepository;
    }

    /**
     * Save a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    public Booking save(Booking booking) {
        log.info("Request to save Booking : {}", booking);
        log.debug("Request to save Booking : {}", booking);

        Machine machine = findByMachineId(booking.getMachine().getId()); // this one check given machine id exists from DB or not
        /*                                                                  // this method executed from the MachineRepo
        // Check Machine Operator

        machineOperatorRepository.findByMachineId(machine.getId()) // check MachineOperator details based on the existing machine
            .orElseThrow(() ->                                     // this method returns MachineOperator(Driver) Details from MachineOperator Table
                new BadRequestAlertException(
                    "Machine operator details are missing for this machine",
                    "booking",
                    "machineOperatorMissing"
                )
            );

        //Check Vehicle Documents Exist

        boolean hasDocs =
            vehicleDocumentRepository.existsByMachineId(machine.getId());

        if (!hasDocs) {
            throw new BadRequestAlertException(
                "Vehicle documents are missing for this machine",
                "booking",
                "vehicleDocumentsMissing"
            );
        }
*/
        booking.setMachine(machine);
        User user = findByUserId(booking.getUser().getId());
        booking.setUser(user);

        // Don't allow save if status is BOOKED or ACCEPTED for overlapping booking
        if (booking.getMachine() != null && booking.getStartDateTime() != null && booking.getEndDateTime() != null) {
            boolean overlap = isOverlappingBookingExists(
                booking.getMachine().getId(),
                booking.getStartDateTime(),
                booking.getEndDateTime()
            );
            if (overlap) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Machine is already booked for the selected time period.");
            }
        }

        return bookingRepository.save(booking);
    }

    /**
     * Update a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    public Booking update(Booking booking) {
        log.debug("Request to update Booking : {}", booking);
        return bookingRepository.save(booking);
    }

    /**
     * Partially update a booking.
     *
     * @param booking the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Booking> partialUpdate(Booking booking) {
        log.debug("Request to partially update Booking : {}", booking);

        return bookingRepository
            .findById(booking.getId())
            .map(existingBooking -> {
                if (booking.getStartDateTime() != null) {
                    existingBooking.setStartDateTime(booking.getStartDateTime());
                }
                if (booking.getEndDateTime() != null) {
                    existingBooking.setEndDateTime(booking.getEndDateTime());
                }
                if (booking.getStatus() != null) {
                    existingBooking.setStatus(booking.getStatus());
                }
                if (booking.getAdditionalDetails() != null) {
                    existingBooking.setAdditionalDetails(booking.getAdditionalDetails());
                }
                if (booking.getWorksiteImageUrl() != null) {
                    existingBooking.setWorksiteImageUrl(booking.getWorksiteImageUrl());
                }
                if (booking.getCustomerLat() != null) {
                    existingBooking.setCustomerLat(booking.getCustomerLat());
                }
                if (booking.getCustomerLong() != null) {
                    existingBooking.setCustomerLong(booking.getCustomerLong());
                }
                if (booking.getCreatedDate() != null) {
                    existingBooking.setCreatedDate(booking.getCreatedDate());
                }

                return existingBooking;
            })
            .map(bookingRepository::save);
    }

    /**
     * Get all the bookings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Booking> findAll(Pageable pageable) {
        log.debug("Request to get all Bookings");
        return bookingRepository.findAll(pageable);
    }

    /**
     * Get all the bookings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Booking> findAllWithEagerRelationships(Pageable pageable) {
        return bookingRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one booking by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Booking> findOne(Long id) {
        log.debug("Request to get Booking : {}", id);
        return bookingRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the booking by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Booking : {}", id);
        bookingRepository.deleteById(id);
    }

    public boolean isOverlappingBookingExists(Long machineId, Instant start, Instant end) {
        return bookingRepository.existsByMachineIdAndStatusInAndDateRangeOverlap(
            machineId,
            Arrays.asList(BookingStatus.PENDING, BookingStatus.ACCEPTED),
            start,
            end
        );
    }

    public Machine findByMachineId(Long machineId) {
        return machineRepository
            .findById(machineId)
            .orElseThrow(() -> new BadRequestAlertException("Machine not found with id " + machineId, "Booking", "machineIdNotFound"));
    }

    public User findByUserId(Long userId) {
        return userRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestAlertException("User not found with id " + userId, "Booking", "UserIdNotFound"));
    }
}
