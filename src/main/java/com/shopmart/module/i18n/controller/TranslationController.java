package com.shopmart.module.i18n.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.i18n.dto.TranslationRequest;
import com.shopmart.module.i18n.dto.TranslationResponse;
import com.shopmart.module.i18n.service.TranslationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/translations")
@RequiredArgsConstructor
@Tag(name = "Translations (i18n)")
public class TranslationController {

    private final TranslationService service;

    /** Public: fetch localized fields for an entity, e.g. ?entityType=product&entityId=5&locale=hi */
    @GetMapping
    public ApiResponse<Map<String, String>> forEntity(@RequestParam String entityType,
                                                      @RequestParam Long entityId,
                                                      @RequestParam String locale) {
        return ApiResponse.ok(service.forEntity(entityType, entityId, locale));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TranslationResponse> upsert(@Valid @RequestBody TranslationRequest request) {
        return ApiResponse.ok("Translation saved", service.upsert(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Translation deleted");
    }
}
