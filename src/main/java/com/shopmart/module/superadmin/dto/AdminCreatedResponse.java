package com.shopmart.module.superadmin.dto;

import java.util.Set;

public record AdminCreatedResponse(Long id, String name, String email, Set<String> roles) {}
