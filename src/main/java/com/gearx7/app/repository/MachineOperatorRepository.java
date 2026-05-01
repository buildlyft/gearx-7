package com.gearx7.app.repository;

import com.gearx7.app.domain.MachineOperator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineOperatorRepository extends JpaRepository<MachineOperator, Long> {
    List<MachineOperator> findByPartnerId(Long partnerId);

    @Query(
        """
        select mo
        from MachineOperator mo
        left join fetch mo.machine
        left join fetch mo.partner
        """
    )
    List<MachineOperator> findAllWithRelations();

    @Query(
        """
        select mo
        from MachineOperator mo
        left join fetch mo.machine
        left join fetch mo.partner
        where mo.partner.id = :partnerId
        """
    )
    List<MachineOperator> findByPartnerIdWithRelations(@Param("partnerId") Long partnerId);

    @Query(
        """
        select mo
        from MachineOperator mo
        left join fetch mo.machine
        left join fetch mo.partner
        where mo.id = :id
        """
    )
    Optional<MachineOperator> findOneWithRelations(@Param("id") Long id);
}
