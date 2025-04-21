package com.poc.droolspocv2.service;

import com.poc.droolspocv2.model.AccountValidationData;
import com.poc.droolspocv2.model.DroolsTemplate;
import com.poc.droolspocv2.model.ValidationResult;
import com.poc.droolspocv2.repository.AccountValidationDataRepository;
import com.poc.droolspocv2.repository.DroolsTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroolsService {

    private final DroolsTemplateRepository droolsTemplateRepository;
    private final AccountValidationDataRepository accountValidationDataRepository;
    private KieServices kieServices;

    @PostConstruct
    public void init() {
        kieServices = KieServices.Factory.get();
    }

    public ValidationResult validateAccount(String accountId, String validationType) {
        log.info("Starting validation for accountId: {} with validationType: {}", accountId, validationType);

        // Create the validation result
        ValidationResult result = ValidationResult.builder()
                .accountId(accountId)
                .validationType(validationType)
                .valid(false)
                .build();

        // Get the template for the validation type
        Optional<DroolsTemplate> templateOpt = droolsTemplateRepository.findByValidationType(validationType);
        if (templateOpt.isEmpty()) {
            result.addMessage("Validation template not found for type: " + validationType);
            return result;
        }

        // Get account data
        Optional<AccountValidationData> accountDataOpt = accountValidationDataRepository.findByAccountId(accountId);
        if (accountDataOpt.isEmpty()) {
            result.addMessage("Account data not found for accountId: " + accountId);
            return result;
        }

        try {
            // Process and build the DRL
            String drl = processDrlTemplate(templateOpt.get(), accountDataOpt.get());
            KieSession kieSession = buildKieSessionFromDrl(drl);

            // Execute rules
            kieSession.insert(accountDataOpt.get());
            kieSession.insert(result);
            kieSession.fireAllRules();
            kieSession.dispose();

            // If no validation messages, mark as valid
            if (result.getMessages().isEmpty()) {
                result.setValid(true);
                result.addMessage("All validations passed successfully");
            }

            return result;
        } catch (Exception e) {
            log.error("Error during validation process", e);
            result.addMessage("Validation error: " + e.getMessage());
            return result;
        }
    }

    private String processDrlTemplate(DroolsTemplate template, AccountValidationData accountData) {
        log.debug("Processing DRL template for account: {}", accountData.getAccountId());

        // Prepare data for template replacement
        Map<String, Object> data = new HashMap<>();
        data.put("accountId", accountData.getAccountId());
        data.put("name", accountData.getName());
        data.put("age", accountData.getAge() != null ? accountData.getAge().toString() : "null");
        data.put("dob", accountData.getDob() != null ? accountData.getDob().toString() : "null");

        // Simple template replacement using a map of values
        String drl = template.getDrlTemplate();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            drl = drl.replace(placeholder, entry.getValue() != null ? entry.getValue().toString() : "null");
        }

        log.debug("Processed DRL: {}", drl);
        return drl;
    }

    private KieSession buildKieSessionFromDrl(String drl) {
        log.debug("Building KieSession from DRL");
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write("src/main/resources/rules/generated.drl", drl);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        if (kieBuilder.getResults().hasMessages()) {
            throw new RuntimeException("Error building Kie Module: " + kieBuilder.getResults().getMessages());
        }

        KieRepository kieRepository = kieServices.getRepository();
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());

        return kieContainer.newKieSession();
    }
}