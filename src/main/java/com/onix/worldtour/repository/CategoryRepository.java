package com.onix.worldtour.repository;

import com.onix.worldtour.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    Page<Category> findByNameContaining(String name, Pageable pageable);
}