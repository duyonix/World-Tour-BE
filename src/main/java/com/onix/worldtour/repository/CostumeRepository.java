package com.onix.worldtour.repository;

import com.onix.worldtour.model.Costume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CostumeRepository extends JpaRepository<Costume, Integer> {
    Optional<Costume> findByName(String name);

    @Query(value = """
        SELECT s FROM Costume s\s
        WHERE s.name LIKE %:name% AND\s
        (:regionId IS NULL OR s.region.id = :regionId)\s
        """)
    Page<Costume> findByNameContainingAndRegionId(@Param("name") String name, @Param("regionId") Integer regionId, Pageable pageable);
}
