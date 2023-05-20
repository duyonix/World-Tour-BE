package com.onix.worldtour.repository;

import com.onix.worldtour.model.SceneSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SceneSpotRepository extends JpaRepository<SceneSpot, Integer> {
    List<SceneSpot> findByRegionId(Integer id);
}
