package com.gearx7.app.repository;

import com.gearx7.app.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(
        """
            select distinct c
            from Category c
            left join fetch c.subcategories
            where c.id = :id
        """
    )
    Optional<Category> findByIdWithSubcategories(@Param("id") Long id);
}
