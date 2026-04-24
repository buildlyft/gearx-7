package com.gearx7.app.web.rest;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.repository.BookingRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.BookingService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.FailedValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gearx7.app.domain.Booking}.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingResource {

    private final Logger log = LoggerFactory.getLogger(BookingResource.class);

    private static final String ENTITY_NAME = "booking";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookingService bookingService;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    public BookingResource(BookingService bookingService, BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /bookings} : Create a new booking.
     *
     * @param booking the booking to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new booking, or with status {@code 400 (Bad Request)} if the booking has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody Booking booking) throws URISyntaxException {
        log.info(
            "REST REQUEST for Creating booking | machineId={}, userId={}, startDateTime={}, endDateTime={}",
            booking.getMachine() != null ? booking.getMachine().getId() : null,
            booking.getUser() != null ? booking.getUser().getId() : null,
            booking.getStartDateTime(),
            booking.getEndDateTime()
        );

        if (booking.getId() != null) {
            log.warn("Attempt to create Booking with existing ID: {}", booking.getId());
            throw new BadRequestAlertException("A new booking cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            // log.info("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin().orElseThrow());
            String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccessDeniedException("User not authenticated"));
            log.info("Creating booking | login={}", username);
            booking.setUser(
                userRepository
                    .findOneByLogin(username)
                    .orElseThrow(() ->
                        new BadRequestAlertException("User not found with login : " + username, ENTITY_NAME, "User Not Found")
                    )
            );
        } else {
            if (booking.getUser() == null || booking.getUser().getId() == null) {
                throw new BadRequestAlertException("User must be provided for admin booking", ENTITY_NAME, "UserMissing");
            }

            Long userId = booking.getUser().getId();

            booking.setUser(
                userRepository
                    .findById(userId)
                    .orElseThrow(() -> new BadRequestAlertException("User not found with id " + userId, ENTITY_NAME, "UserNotFound"))
            );
            log.info("Admin user creating booking for userId={}", userId);
        }

        FailedValidator.validateInputParameters(booking);

        Booking result = bookingService.save(booking);
        log.info("Booking created successfully with id={}", result.getId());

        Optional<Booking> bookingWithRelations = bookingRepository.findOneWithToOneRelationships(result.getId());

        Booking finalResult = bookingWithRelations.orElse(result);

        return ResponseEntity.created(new URI("/api/bookings/" + finalResult.getId())).body(finalResult);
    }

    /**
     * {@code PUT  /bookings/:id} : Updates an existing booking.
     *
     * @param id      the id of the booking to save.
     * @param booking the booking to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated booking,
     * or with status {@code 400 (Bad Request)} if the booking is not valid,
     * or with status {@code 500 (Internal Server Error)} if the booking couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Booking booking
    ) throws URISyntaxException {
        log.debug("REST request to update Booking : {}, {}", id, booking);
        if (booking.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, booking.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Booking result = bookingService.update(booking);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, booking.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bookings/:id} : Partial updates given fields of an existing booking, field will ignore if it is null
     *
     * @param id      the id of the booking to save.
     * @param booking the booking to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated booking,
     * or with status {@code 400 (Bad Request)} if the booking is not valid,
     * or with status {@code 404 (Not Found)} if the booking is not found,
     * or with status {@code 500 (Internal Server Error)} if the booking couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Booking> partialUpdateBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Booking booking
    ) throws URISyntaxException {
        log.debug("REST request to partial update Booking partially : {}, {}", id, booking);
        if (booking.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, booking.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Booking> result = bookingService.partialUpdate(booking);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, booking.getId().toString())
        );
    }

    /**
     * {@code GET  /bookings} : get all the bookings.
     *
     * @param pageable  the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Booking>> getAllBookings(
        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Bookings");
        Page<Booking> page;
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            if (eagerload) {
                page = bookingService.findAllWithEagerRelationships(pageable);
            } else {
                page = bookingService.findAll(pageable);
            }
        } else {
            page = bookingRepository.findByUserIsCurrentUser(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bookings/:id} : get the "id" booking.
     *
     * @param id the id of the booking to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the booking, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable("id") Long id) {
        log.debug("REST request to get Booking : {}", id);
        Optional<Booking> booking = bookingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(booking);
    }

    /**
     * {@code DELETE  /bookings/:id} : delete the "id" booking.
     *
     * @param id the id of the booking to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long id) {
        log.debug("REST request to delete Booking : {}", id);
        bookingService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     *If login as a admin can get all bookings or filter by ownerId,
     * if login as a partner can only get own bookings
     *
     * @param ownerId    the id of the machine owner to filter bookings (optional, only for admin)
     * @param pageable   the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body the list of bookings
     */
    @GetMapping("/by-owner")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<List<Booking>> getBookingsByOwner(
        @RequestParam(required = false) Long ownerId,
        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info(
            "REST GET Bookings START | requestOwnerId={} | page={} | size={}",
            ownerId,
            pageable.getPageNumber(),
            pageable.getPageSize()
        );

        Page<Booking> page = bookingService.getBookingsByOwner(ownerId, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.info("REST GET Bookings SUCCESS | totalElements={} | totalPages={}", page.getTotalElements(), page.getTotalPages());

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
