package com.gearx7.app.repository;

import com.gearx7.app.domain.Machine;
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
    List<Machine> findByUserIsCurrentUser();

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
}
