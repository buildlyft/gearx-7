package com.gearx7.app.repository;

import com.gearx7.app.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    boolean existsByTypeNameIgnoreCase(String typeName);
}
