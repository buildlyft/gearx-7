package com.gearx7.app.repository;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.domain.enumeration.BookingStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(
        value = "select booking from Booking booking " +
        "left join fetch booking.machine " +
        "where booking.user.login = ?#{authentication.name}",
        countQuery = "select count(booking) from Booking booking where booking.user.login = ?#{authentication.name}"
    )
    Page<Booking> findByUserIsCurrentUser(Pageable pageable);

    default Optional<Booking> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Booking> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Booking> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct booking from Booking booking " + "left join fetch booking.machine",
        countQuery = "select count(booking) from Booking booking"
    )
    Page<Booking> findAllWithToOneRelationships(Pageable pageable);

    @Query("select booking from Booking booking " + "left join fetch booking.machine")
    List<Booking> findAllWithToOneRelationships();

    @Query(
        "select booking from Booking booking " +
        "left join fetch booking.machine " +
        "left join fetch booking.user " +
        "where booking.id = :id"
    )
    Optional<Booking> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "select b from Booking b " + "join b.machine m " + "join m.user u " + "where u.id = :ownerId",
        countQuery = "select count(b) from Booking b where b.machine.user.id = :ownerId"
    )
    Page<Booking> findByMachineOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query(
        value = "select b from Booking b join b.machine m where m.user.login = :login",
        countQuery = "select count(b) from Booking b where b.machine.user.login = :login"
    )
    Page<Booking> findByMachineOwnerLogin(@Param("login") String login, Pageable pageable);

    @Query(
        "SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
        "WHERE b.machine.id = :machineId " +
        "AND b.status IN :statuses " +
        "AND ((b.startDateTime < :end AND b.endDateTime > :start))"
    )
    boolean existsByMachineIdAndStatusInAndDateRangeOverlap(
        @Param("machineId") Long machineId,
        @Param("statuses") List<BookingStatus> statuses,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
}
