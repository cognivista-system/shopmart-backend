package com.shopmart.module.warehouse.dto;

public record WarehouseResponse(
        Long id,
        String name,
        String code,
        String addressLine,
        String city,
        String state,
        String country,
        String postalCode,
        boolean active
) {}
