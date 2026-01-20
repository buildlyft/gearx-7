package com.gearx7.app.repository;

import com.gearx7.app.domain.Type;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    boolean existsByTypeNameIgnoreCase(String typeName);

    @Query(
        """
        select t from Type t
        left join fetch t.categories
        where t.id = :id
        """
    )
    Optional<Type> findByIdWithCategories(@Param("id") Long id);

    @Query(
        """
        select distinct t
        from Type t
        left join fetch t.categories
        """
    )
    List<Type> findAllWithCategories();
}
