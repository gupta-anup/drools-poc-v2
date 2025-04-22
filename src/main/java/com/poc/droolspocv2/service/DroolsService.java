package com.poc.droolspocv2.service;

import com.poc.droolspocv2.dto.ValidationResult;
import com.poc.droolspocv2.model.AccountValidationData;
import com.poc.droolspocv2.model.DroolsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroolsService {

    private final KieServices kieServices;

    public ValidationResult validateCustomer(AccountValidationData customer, String drlTemplate) {
        // Create a validation result object to collect validation results
        ValidationResult result = new ValidationResult(true, null);

        try {
            // Compile the template with customer data
            Map<String, Object> data = new HashMap<>();
            data.put("customer", customer);

            ObjectDataCompiler compiler = new ObjectDataCompiler();
            String drl = drlTemplate;

            // Create a new KieContainer with the generated rule
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            kieFileSystem.write("src/main/resources/rules/generated-rules.drl", drl);

            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

            // Create a session and fire rules
            KieSession kieSession = kieContainer.newKieSession();
            kieSession.insert(customer);
            kieSession.insert(result);
            kieSession.fireAllRules();
            kieSession.dispose();

            return result;
        } catch (Exception e) {
            log.error("Error validating customer using Drools", e);
            return ValidationResult.builder()
                    .valid(false)
                    .message("Validation error: " + e.getMessage())
                    .build();
        }
    }
}