package com.poc.droolspocv2.service;

import com.poc.droolspocv2.dto.OrderRequest;
import com.poc.droolspocv2.dto.ValidationResult;
import com.poc.droolspocv2.model.AccountValidationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroolsService {

    private final KieServices kieServices;

    public ValidationResult validateOrder(OrderRequest orderRequest, AccountValidationData storedCustomer, String drlTemplate) {
        // Create a validation result object to collect validation results
        ValidationResult result = ValidationResult.builder().valid(true).build();

        try {
            // Create facts map for template evaluation
            Map<String, Object> facts = new HashMap<>();
            facts.put("result", result);

            // Create a new KieContainer with the rule template
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            kieFileSystem.write("src/main/resources/rules/generated-rules.drl", drlTemplate);

            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

            // Create a session and insert facts
            KieSession kieSession = kieContainer.newKieSession();

            // Insert the request data and stored customer data for comparison
            kieSession.insert(orderRequest);
            kieSession.insert(storedCustomer);
            kieSession.insert(result);

            // Fire all rules
            kieSession.fireAllRules();
            kieSession.dispose();

            return result;
        } catch (Exception e) {
            log.error("Error validating order using Drools", e);
            return ValidationResult.builder()
                    .valid(false)
                    .message("Validation error: " + e.getMessage())
                    .build();
        }
    }
}