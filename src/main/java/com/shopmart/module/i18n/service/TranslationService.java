package com.shopmart.module.i18n.service;

import com.shopmart.module.i18n.dto.TranslationRequest;
import com.shopmart.module.i18n.dto.TranslationResponse;

import java.util.Map;

public interface TranslationService {
    TranslationResponse upsert(TranslationRequest request);
    Map<String, String> forEntity(String entityType, Long entityId, String locale);
    void delete(Long id);
}
