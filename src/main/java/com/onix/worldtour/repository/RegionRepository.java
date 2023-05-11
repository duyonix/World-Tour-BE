package com.onix.worldtour.repository;

import com.onix.worldtour.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByName(String name);

    @Query(value = """
        SELECT s FROM Region s\s
        WHERE s.name LIKE %:name% AND\s
        (:categoryId IS NULL OR s.category.id = :categoryId)\s
        """)
    Page<Region> findByNameContainingAndCategoryId(@Param("name") String name, @Param("categoryId") Integer categoryId, Pageable pageable);

}
