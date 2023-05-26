package com.onix.worldtour.repository;

import com.onix.worldtour.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByName(String name);

    @Query(value = """
            SELECT s FROM Region s\s
            WHERE (lower(s.name) LIKE lower(concat('%', :name, '%')) OR
                lower(s.commonName) LIKE lower(concat('%', :name, '%'))) AND
            (:categoryId IS NULL OR s.category.id = :categoryId) AND
            (:parentId IS NULL OR s.parent.id = :parentId)
            """)
    Page<Region> findByNameContainingAndCategoryIdAndParentId(@Param("name") String name,
                                                              @Param("categoryId") Integer categoryId,
                                                              @Param("parentId") Integer parentId,
                                                              Pageable pageable);

    List<Region> findByNameContaining(String name);

    @Query(value = """
            SELECT s FROM Region s\s
            WHERE (lower(s.name) LIKE lower(concat('%', :name, '%')) OR
                lower(s.commonName) LIKE lower(concat('%', :name, '%'))) AND
            (:level IS NULL OR s.category.level = :level)
            """)
    List<Region> findByNameContainingAndCategoryLevel(@Param("name") String name, @Param("level") Integer level);

    List<Region> findByCategoryLevel(Integer level);

    List<Region> findByCategoryIdAndParentId(Integer categoryId, Integer parentId);

    @Query(value = """
            SELECT r FROM Region r\s
            WHERE (r.name = :name OR r.commonName = :commonName) AND\s
            r.category.id = :categoryId AND\s
            r.parent.id = :parentId
            """)
    Optional<Region> findByNameOrCommonNameAndCategoryIdAndParentId(
            @Param("name") String name,
            @Param("commonName") String commonName,
            @Param("categoryId") Integer categoryId,
            @Param("parentId") Integer parentId
    );
}
