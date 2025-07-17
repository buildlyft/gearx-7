package com.gearx7.app.repository;

import com.gearx7.app.domain.Partner;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Partner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {}
