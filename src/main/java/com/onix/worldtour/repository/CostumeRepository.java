package com.onix.worldtour.repository;

import com.onix.worldtour.model.Costume;
import com.onix.worldtour.model.CostumeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostumeRepository extends JpaRepository<Costume, Integer> {
    Optional<Costume> findByName(String name);

    @Query(value = """
        SELECT c FROM Costume c
        WHERE lower(c.name) LIKE lower(concat('%', :name, '%')) AND
        (:regionId IS NULL OR c.region.id = :regionId) AND
        (:type IS NULL OR c.type = :type)
        """)
    Page<Costume> findByNameContainingAndRegionIdAndType(@Param("name") String name,
                                                         @Param("regionId") Integer regionId,
                                                         @Param("type") CostumeType type,
                                                         Pageable pageable);

    List<Costume> findByRegionIdIn(List<Integer> regionIds);
}
