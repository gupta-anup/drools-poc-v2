package com.poc.droolspocv2.controller;

import com.poc.droolspocv2.model.ValidationResult;
import com.poc.droolspocv2.service.DroolsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final DroolsService droolsService;

    @GetMapping("/{accountId}")
    public ResponseEntity<ValidationResult> validateAccount(
            @PathVariable String accountId,
            @RequestParam String validationType) {
        ValidationResult result = droolsService.validateAccount(accountId, validationType);
        return ResponseEntity.ok(result);
    }
}