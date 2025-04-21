package com.poc.droolspocv2.controller;

import com.poc.droolspocv2.model.DroolsTemplate;
import com.poc.droolspocv2.repository.DroolsTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final DroolsTemplateRepository droolsTemplateRepository;

    @GetMapping
    public ResponseEntity<List<DroolsTemplate>> getAllTemplates() {
        return ResponseEntity.ok(droolsTemplateRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroolsTemplate> getTemplateById(@PathVariable Long id) {
        return droolsTemplateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DroolsTemplate> createTemplate(@RequestBody DroolsTemplate template) {
        return ResponseEntity.ok(droolsTemplateRepository.save(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DroolsTemplate> updateTemplate(
            @PathVariable Long id,
            @RequestBody DroolsTemplate template) {
        Optional<DroolsTemplate> existingTemplate = droolsTemplateRepository.findById(id);
        if (existingTemplate.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        template.setId(id);
        return ResponseEntity.ok(droolsTemplateRepository.save(template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        if (!droolsTemplateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        droolsTemplateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}