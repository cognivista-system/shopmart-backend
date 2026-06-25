package com.shopmart.module.attendance.service;

import com.shopmart.module.attendance.dto.AttendanceResponse;

import java.util.List;

public interface AttendanceService {
    AttendanceResponse login(Long userId);
    AttendanceResponse logout(Long userId);
    List<AttendanceResponse> myHours(Long userId);
}
