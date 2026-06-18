package com.shopmart.module.auth.mapper;

import com.shopmart.module.auth.dto.UserResponse;
import com.shopmart.module.user.entity.Role;
import com.shopmart.module.user.entity.User;

import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.isEmailVerified(),
                user.getRoles().stream().map(Role::name).collect(Collectors.toSet())
        );
    }
}
