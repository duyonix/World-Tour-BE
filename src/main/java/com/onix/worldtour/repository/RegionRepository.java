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

    @Query(value = """
            SELECT s FROM Region s\s
            WHERE (lower(s.name) LIKE lower(concat('%', :search, '%')) OR
                lower(s.commonName) LIKE lower(concat('%', :search, '%'))) AND
            (:categoryId IS NULL OR s.category.id = :categoryId) AND
            (:parentId IS NULL OR s.parent.id = :parentId)
            """)
    Page<Region> findBySearchAndCategoryIdAndParentId(@Param("search") String search,
                                                      @Param("categoryId") Integer categoryId,
                                                      @Param("parentId") Integer parentId,
                                                      Pageable pageable);

    @Query(value = """
            SELECT s FROM Region s\s
            WHERE (lower(s.name) LIKE lower(concat('%', :search, '%')) OR
                lower(s.commonName) LIKE lower(concat('%', :search, '%'))) AND
            (:level IS NULL OR s.category.level = :level)
            """)
    List<Region> findBySearchAndCategoryLevel(@Param("search") String search, @Param("level") Integer level);

    @Query("""
            SELECT r FROM Region r
            WHERE (LOWER(r.name) LIKE LOWER(concat('%', :search, '%')) OR LOWER(r.commonName) LIKE LOWER(concat('%', :search, '%')))
            AND (:level IS NULL OR r.category.level = :level)
            AND r.coordinate.lattitude >= :minLatitude
            AND r.coordinate.lattitude <= :maxLatitude
            AND r.coordinate.longitude >= :minLongitude
            AND r.coordinate.longitude <= :maxLongitude
            """)
    List<Region> findBySearchAndCategoryLevelAndWithinBounds(
            @Param("search") String search,
            @Param("level") Integer level,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLatitude") Double maxLatitude,
            @Param("minLongitude") Double minLongitude,
            @Param("maxLongitude") Double maxLongitude
    );

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
