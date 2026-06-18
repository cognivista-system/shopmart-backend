package com.shopmart.module.warehouse.dto;

import jakarta.validation.constraints.NotBlank;

public record WarehouseRequest(
        @NotBlank String name,
        @NotBlank String code,
        String addressLine,
        String city,
        String state,
        String country,
        String postalCode,
        Boolean active
) {}
