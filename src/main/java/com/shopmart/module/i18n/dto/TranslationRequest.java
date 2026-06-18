package com.shopmart.module.i18n.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TranslationRequest(
        @NotBlank String entityType,
        @NotNull Long entityId,
        @NotBlank String locale,
        @NotBlank String field,
        @NotBlank String value
) {}
