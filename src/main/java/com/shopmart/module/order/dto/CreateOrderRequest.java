package com.shopmart.module.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull @Valid ShippingAddressDto shippingAddress,
        @NotBlank String paymentMethod,   // COD, UPI, CARD, NETBANKING, WALLET
        String couponCode
) {}
