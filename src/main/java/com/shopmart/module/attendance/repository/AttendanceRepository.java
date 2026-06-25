package com.shopmart.module.attendance.repository;

import com.shopmart.module.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Most recent open session (no logout yet) for a user
    Optional<Attendance> findFirstByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(Long userId);

    List<Attendance> findByUserIdOrderByLoginTimeDesc(Long userId);
}
