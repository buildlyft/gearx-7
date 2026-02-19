package com.gearx7.app.repository;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Machine entity.
 */
@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    @Query("select machine from Machine machine where machine.user.login = ?#{authentication.name}")
    Page<Machine> findByUserIsCurrentUser(Pageable pageable);

    default Optional<Machine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Machine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Machine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select machine from Machine machine left join fetch machine.user",
        countQuery = "select count(machine) from Machine machine"
    )
    Page<Machine> findAllWithToOneRelationships(Pageable pageable);

    @Query("select machine from Machine machine left join fetch machine.user")
    List<Machine> findAllWithToOneRelationships();

    @Query("select machine from Machine machine left join fetch machine.user where machine.id =:id")
    Optional<Machine> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = """
            SELECT *
            FROM machine m
            WHERE m.status = 'AVAILABLE'
            AND m.subcategory_id = :subcategoryId
            AND m.latitude BETWEEN :minLat AND :maxLat
            AND m.longitude BETWEEN :minLon AND :maxLon
            AND (
                6371 * acos(
                    cos(radians(:userLat)) *
                    cos(radians(m.latitude)) *
                    cos(radians(m.longitude) - radians(:userLon)) +
                    sin(radians(:userLat)) *
                    sin(radians(m.latitude))
                )
            ) <= :radiusKm
        """,
        nativeQuery = true
    )
    List<Machine> searchWithinRadius(
        @Param("subcategoryId") Long subcategoryId,
        @Param("userLat") double userLat,
        @Param("userLon") double userLon,
        @Param("minLat") double minLat,
        @Param("maxLat") double maxLat,
        @Param("minLon") double minLon,
        @Param("maxLon") double maxLon,
        @Param("radiusKm") double radiusKm
    );

    Optional<Machine> findById(Long id);

    /*
    Optional<MachineOperator> findByIdAndActiveTrue(Long machineId);

    boolean existsByIdAndActiveTrue(Long machineId);
*/
    @Query(
        """
            select distinct m
            from Machine m
            left join fetch m.user
            left join fetch m.category
            left join fetch m.subcategory
            left join fetch m.operators o
            where m.id = :id
        """
    )
    Optional<Machine> findOneWithAllRelationships(@Param("id") Long id);

    @Query(
        """
        select m from Machine m
        where not exists (
            select mo.id from MachineOperator mo
            where mo.machine = m
            and mo.active = true
        )
        """
    )
    List<Machine> findMachinesWithoutOperator();
}
