package com.shopmart.module.attendance.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attendance",
        indexes = @Index(name = "idx_attendance_user", columnList = "user_id"))
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "login_time", nullable = false)
    private Instant loginTime;

    @Column(name = "logout_time")
    private Instant logoutTime;

    /** Hours worked for this session, set on logout. */
    @Column(name = "total_hours")
    private Double totalHours;
}
