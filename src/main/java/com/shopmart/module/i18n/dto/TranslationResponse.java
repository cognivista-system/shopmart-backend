package com.shopmart.module.i18n.dto;

public record TranslationResponse(Long id, String entityType, Long entityId,
                                  String locale, String field, String value) {}
