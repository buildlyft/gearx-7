package com.gearx7.app.service;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.User;
import com.gearx7.app.domain.enumeration.BookingStatus;
import com.gearx7.app.repository.BookingRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.interfaces.SmsService;
import com.gearx7.app.service.mapper.BookingMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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

    private final SmsService smsService;

    private final LocationIQService locationIQService;

    private final BookingMapper bookingMapper;

    //    private final MachineOperatorRepository machineOperatorRepository;
    //
    //    private final VehicleDocumentRepository vehicleDocumentRepository;

    public BookingService(
        BookingRepository bookingRepository,
        MachineRepository machineRepository,
        UserRepository userRepository,
        SmsService smsService,
        LocationIQService locationIQService,
        BookingMapper bookingMapper
        //  MachineOperatorRepository machineOperatorRepository,
        // VehicleDocumentRepository vehicleDocumentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.smsService = smsService;
        this.locationIQService = locationIQService;
        this.bookingMapper = bookingMapper;
        //this.machineOperatorRepository=machineOperatorRepository;
        // this.vehicleDocumentRepository=vehicleDocumentRepository;
    }

    /**
     * Save a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    @Transactional
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

        // Don't allow save if status is BOOKED or ACCEPTED for overlapping booking
        if (booking.getMachine() != null && booking.getStartDateTime() != null && booking.getEndDateTime() != null) {
            boolean overlap = isOverlappingBookingExists(
                booking.getMachine().getId(),
                booking.getStartDateTime(),
                booking.getEndDateTime()
            );
            if (overlap) {
                throw new BadRequestAlertException(
                    "Machine is already booked for the selected time range",
                    "booking",
                    "machineUnavailable"
                );
            }
        }
        if (booking.getCustomerLat() != null && booking.getCustomerLong() != null) {
            String address = locationIQService.getAddress(booking.getCustomerLat(), booking.getCustomerLong());

            booking.setCustomerAddress(address);
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking saved successfully | bookingId={}", savedBooking.getId());

        // SEND SMS ONLY AFTER TRANSACTION COMMIT
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        smsService.sendBookingCreatedSmsToUser(savedBooking);
                        smsService.sendBookingCreatedSmsToPartner(savedBooking);
                        log.info("Booking-created SMS sent AFTER COMMIT | bookingId={}", savedBooking.getId());
                    } catch (Exception ex) {
                        log.error("SMS failed AFTER COMMIT (ignored) | bookingId={}", savedBooking.getId(), ex);
                    }
                }
            }
        );

        return savedBooking;
    }

    /**
     * Update a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    public Booking update(Booking booking) {
        log.debug("Request to update Booking : {}", booking);
        Booking existingBooking = bookingRepository
            .findById(booking.getId())
            .orElseThrow(() -> new NotFoundAlertException("Booking not found with id " + booking.getId(), "booking", "bookingNotFound"));

        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccessDeniedException("Partner authentication required"));

        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);

        boolean isPartnerOwner = existingBooking.getMachine().getUser().getLogin().equals(login);

        boolean isBookingOwner = existingBooking.getUser().getLogin().equals(login);

        if (!isAdmin && !isPartnerOwner && !isBookingOwner) {
            log.info("Unauthorized booking update attempt | bookingId={} | login={}", existingBooking.getId(), login);
            throw new AccessDeniedException("You are not allowed to update this booking");
        }

        if (booking.getCustomerLat() != null && booking.getCustomerLong() != null) {
            String address = locationIQService.getAddress(booking.getCustomerLat(), booking.getCustomerLong());

            booking.setCustomerAddress(address);
        }
        return bookingRepository.save(booking);
    }

    /**
     * Partially update a booking.
     *
     * @param booking the entity to update partially.
     * @return the persisted entity.
     */
    @Transactional
    public Optional<Booking> partialUpdate(Booking booking) {
        log.info("Request to partially update Booking | bookingId={}", booking.getId());

        return bookingRepository
            .findById(booking.getId())
            .map(existingBooking -> {
                BookingStatus oldStatus = existingBooking.getStatus();
                String login = SecurityUtils
                    .getCurrentUserLogin()
                    .orElseThrow(() -> new AccessDeniedException("User authentication required"));

                boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);

                boolean isMachineOwner = existingBooking.getMachine().getUser().getLogin().equals(login);

                boolean isBookingUser = existingBooking.getUser().getLogin().equals(login);

                if (!isAdmin) {
                    // PARTNER CAN UPDATE OWN MACHINE BOOKINGS
                    if (isMachineOwner) {
                        log.info("Partner updating own machine booking | bookingId={} | login={}", existingBooking.getId(), login);
                    }
                    // USER CAN ONLY CANCEL OWN BOOKING
                    else if (isBookingUser) {
                        // USER CAN ONLY CANCEL OWN BOOKING
                        if (booking.getStatus() == null || booking.getStatus() != BookingStatus.CANCELLED) {
                            log.warn(
                                "User attempted invalid booking update | bookingId={} | login={} | status={}",
                                existingBooking.getId(),
                                login,
                                booking.getStatus()
                            );
                            throw new AccessDeniedException("You can only cancel your booking");
                        }
                        // USER CAN CANCEL ONLY WHEN STATUS IS PENDING
                        if (existingBooking.getStatus() != BookingStatus.PENDING) {
                            log.warn(
                                "User attempted to cancel non-pending booking | bookingId={} | currentStatus={}",
                                existingBooking.getId(),
                                existingBooking.getStatus()
                            );
                            throw new AccessDeniedException("Only pending bookings can be cancelled");
                        }
                        log.info("User cancelling own pending booking | bookingId={} | login={}", existingBooking.getId(), login);
                    }
                    // OTHER USERS NOT ALLOWED
                    else {
                        log.warn("Unauthorized booking update attempt | bookingId={} | login={}", existingBooking.getId(), login);
                        throw new AccessDeniedException("You are not allowed to update this booking");
                    }
                }

                log.info("Existing booking found | bookingId={} | oldStatus={}", existingBooking.getId(), oldStatus);

                if (booking.getStartDateTime() != null) {
                    log.info("Updating startDateTime | bookingId={} | value={}", existingBooking.getId(), booking.getStartDateTime());

                    existingBooking.setStartDateTime(booking.getStartDateTime());
                }

                if (booking.getEndDateTime() != null) {
                    log.info("Updating endDateTime | bookingId={} | value={}", existingBooking.getId(), booking.getEndDateTime());

                    existingBooking.setEndDateTime(booking.getEndDateTime());
                }

                if (booking.getStatus() != null) {
                    log.info(
                        "Updating booking status | bookingId={} | oldStatus={} | newStatus={}",
                        existingBooking.getId(),
                        oldStatus,
                        booking.getStatus()
                    );

                    existingBooking.setStatus(booking.getStatus());
                    // SET CANCELLED DATE
                    if (booking.getStatus() == BookingStatus.CANCELLED) {
                        existingBooking.setCancelledDate(Instant.now());
                        log.info(
                            "Booking cancelled date updated | bookingId={} | cancelledDate={}",
                            existingBooking.getId(),
                            existingBooking.getCancelledDate()
                        );
                    }
                }

                if (booking.getAdditionalDetails() != null) {
                    log.info("Updating additionalDetails | bookingId={}", existingBooking.getId());

                    existingBooking.setAdditionalDetails(booking.getAdditionalDetails());
                }

                if (booking.getWorksiteImageUrl() != null) {
                    log.info("Updating worksiteImageUrl | bookingId={}", existingBooking.getId());

                    existingBooking.setWorksiteImageUrl(booking.getWorksiteImageUrl());
                }

                if (booking.getCustomerLat() != null) {
                    log.info("Updating customerLat | bookingId={} | value={}", existingBooking.getId(), booking.getCustomerLat());

                    existingBooking.setCustomerLat(booking.getCustomerLat());
                }

                if (booking.getCustomerLong() != null) {
                    log.info("Updating customerLong | bookingId={} | value={}", existingBooking.getId(), booking.getCustomerLong());

                    existingBooking.setCustomerLong(booking.getCustomerLong());
                }

                if (existingBooking.getCustomerLat() != null && existingBooking.getCustomerLong() != null) {
                    String address = locationIQService.getAddress(existingBooking.getCustomerLat(), existingBooking.getCustomerLong());

                    existingBooking.setCustomerAddress(address);
                }

                if (booking.getCreatedDate() != null) {
                    log.info("Updating createdDate | bookingId={} | value={}", existingBooking.getId(), booking.getCreatedDate());

                    existingBooking.setCreatedDate(booking.getCreatedDate());
                }

                Booking savedBooking = bookingRepository.save(existingBooking);

                log.info("Booking updated successfully | bookingId={} | currentStatus={}", savedBooking.getId(), savedBooking.getStatus());

                // SEND SMS ONLY WHEN STATUS CHANGED
                if (booking.getStatus() != null && oldStatus != booking.getStatus()) {
                    log.info(
                        "Booking status changed | bookingId={} | oldStatus={} | newStatus={}",
                        savedBooking.getId(),
                        oldStatus,
                        savedBooking.getStatus()
                    );

                    TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                try {
                                    if (savedBooking.getStatus() == BookingStatus.ACCEPTED) {
                                        log.info("Sending ACCEPTED SMS to customer | bookingId={}", savedBooking.getId());

                                        smsService.sendBookingAcceptedSmsToUser(savedBooking);
                                    }

                                    if (savedBooking.getStatus() == BookingStatus.REJECTED) {
                                        log.info("Sending REJECTED SMS to customer | bookingId={}", savedBooking.getId());

                                        smsService.sendBookingRejectedSmsToUser(savedBooking);
                                    }

                                    if (savedBooking.getStatus() == BookingStatus.CANCELLED) {
                                        log.info("Sending CANCELLED SMS to customer | bookingId={}", savedBooking.getId());

                                        smsService.sendBookingCancelledSmsToUser(savedBooking);

                                        log.info("Sending CANCELLED SMS to partner | bookingId={}", savedBooking.getId());

                                        smsService.sendBookingCancelledSmsToPartner(savedBooking);
                                    }
                                } catch (Exception ex) {
                                    log.error("Booking status SMS sending failed | bookingId={}", savedBooking.getId(), ex);
                                }
                            }
                        }
                    );
                } else {
                    log.info("Booking status unchanged, SMS skipped | bookingId={}", savedBooking.getId());
                }

                return savedBooking;
            });
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
        bookingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundAlertException("Booking not found with id " + id, "booking", "bookingNotFound"));
        bookingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByOwner(Long ownerId, Pageable pageable) {
        log.debug("SERVICE GET Bookings START | requestOwnerId={}", ownerId);

        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");

        try {
            if (isAdmin) {
                //  ADMIN FLOW
                if (ownerId == null) {
                    log.warn("ADMIN FLOW | Missing ownerId");
                    throw new BadRequestAlertException("Owner ID must be provided for admin", "Booking", "ownerIdMissing");
                }

                if (!userRepository.existsById(ownerId)) {
                    log.warn("ADMIN FLOW | User not found | ownerId={}", ownerId);
                    throw new NotFoundAlertException("User not found with id " + ownerId, "User", "UserNotFound");
                }

                log.debug("ADMIN FLOW | Fetch bookings by ownerId={}", ownerId);

                return bookingRepository.findByMachineOwnerId(ownerId, pageable);
            } else {
                //  PARTNER FLOW
                String login = SecurityUtils
                    .getCurrentUserLogin()
                    .orElseThrow(() -> {
                        return new NotFoundAlertException("User not found", "User", "UserNotFound");
                    });

                log.debug("PARTNER FLOW | login={}", login);

                return bookingRepository.findByMachineOwnerLogin(login, pageable);
            }
        } catch (Exception ex) {
            log.error("SERVICE GET Bookings FAILED | requestOwnerId={} | reason={}", ownerId, ex.getMessage(), ex);
            throw ex;
        }
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
