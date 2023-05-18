package com.onix.worldtour.repository;

import com.onix.worldtour.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {}
