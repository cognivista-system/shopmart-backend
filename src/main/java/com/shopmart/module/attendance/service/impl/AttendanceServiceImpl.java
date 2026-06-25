package com.shopmart.module.attendance.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.module.attendance.dto.AttendanceResponse;
import com.shopmart.module.attendance.entity.Attendance;
import com.shopmart.module.attendance.repository.AttendanceRepository;
import com.shopmart.module.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository repository;

    @Override
    @Transactional
    public AttendanceResponse login(Long userId) {
        repository.findFirstByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(userId)
                .ifPresent(a -> { throw new BadRequestException("You already have an open session. Logout first."); });
        Attendance a = new Attendance();
        a.setUserId(userId);
        a.setLoginTime(Instant.now());
        return toResponse(repository.save(a));
    }

    @Override
    @Transactional
    public AttendanceResponse logout(Long userId) {
        Attendance a = repository.findFirstByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(userId)
                .orElseThrow(() -> new BadRequestException("No open session to logout from."));
        Instant now = Instant.now();
        a.setLogoutTime(now);
        double hours = Duration.between(a.getLoginTime(), now).toMinutes() / 60.0;
        a.setTotalHours(Math.round(hours * 100.0) / 100.0);
        return toResponse(repository.save(a));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> myHours(Long userId) {
        return repository.findByUserIdOrderByLoginTimeDesc(userId).stream().map(this::toResponse).toList();
    }

    private AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(a.getId(), a.getUserId(), a.getLoginTime(),
                a.getLogoutTime(), a.getTotalHours());
    }
}
