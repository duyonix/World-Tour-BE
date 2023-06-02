package com.onix.worldtour.repository;

import com.onix.worldtour.model.Role;
import com.onix.worldtour.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query(value = """
        SELECT u FROM User u
        WHERE lower(u.email) LIKE lower(concat('%', :search, '%')) AND
        (:role IS NULL OR u.role = :role)
        """)
    Page<User> findByEmailSearchAndRole(@Param("search") String search, @Param("role") Role role, Pageable pageable);
}
