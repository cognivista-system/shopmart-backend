package com.shopmart.module.attendance.dto;

import java.time.Instant;

public record AttendanceResponse(
        Long id,
        Long userId,
        Instant loginTime,
        Instant logoutTime,
        Double totalHours
) {}
