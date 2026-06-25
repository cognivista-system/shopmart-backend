package com.shopmart.module.user.repository;

import com.shopmart.module.user.entity.Role;
import com.shopmart.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByCreatedAtAfter(java.time.Instant since);

    @Query("select count(u) from User u join u.roles r where r = :role")
    long countByRole(@Param("role") Role role);
}
