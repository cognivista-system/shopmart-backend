package com.shopmart.module.i18n.service.impl;

import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.i18n.dto.TranslationRequest;
import com.shopmart.module.i18n.dto.TranslationResponse;
import com.shopmart.module.i18n.entity.Translation;
import com.shopmart.module.i18n.repository.TranslationRepository;
import com.shopmart.module.i18n.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository repository;

    @Override
    @Transactional
    public TranslationResponse upsert(TranslationRequest r) {
        Translation t = repository.findByEntityTypeAndEntityIdAndLocaleAndField(
                        r.entityType(), r.entityId(), r.locale(), r.field())
                .orElseGet(Translation::new);
        t.setEntityType(r.entityType());
        t.setEntityId(r.entityId());
        t.setLocale(r.locale());
        t.setField(r.field());
        t.setValue(r.value());
        t = repository.save(t);
        return new TranslationResponse(t.getId(), t.getEntityType(), t.getEntityId(),
                t.getLocale(), t.getField(), t.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> forEntity(String entityType, Long entityId, String locale) {
        Map<String, String> out = new LinkedHashMap<>();
        for (Translation t : repository.findByEntityTypeAndEntityIdAndLocale(entityType, entityId, locale)) {
            out.put(t.getField(), t.getValue());
        }
        return out;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Translation t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation", "id", id));
        repository.delete(t);
    }
}
