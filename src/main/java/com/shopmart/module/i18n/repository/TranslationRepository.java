package com.shopmart.module.i18n.repository;

import com.shopmart.module.i18n.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByEntityTypeAndEntityIdAndLocale(String entityType, Long entityId, String locale);
    Optional<Translation> findByEntityTypeAndEntityIdAndLocaleAndField(
            String entityType, Long entityId, String locale, String field);
}
