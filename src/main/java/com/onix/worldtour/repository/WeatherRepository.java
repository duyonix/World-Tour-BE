package com.onix.worldtour.repository;

import com.onix.worldtour.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Integer>  {
}
