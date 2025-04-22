package com.poc.droolspocv2.config;

import com.poc.droolspocv2.model.AccountValidationData;
import com.poc.droolspocv2.model.DroolsTemplate;
import com.poc.droolspocv2.repository.AccountValidationDataRepository;
import com.poc.droolspocv2.repository.DroolsTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final DroolsTemplateRepository droolsTemplateRepository;
    private final AccountValidationDataRepository accountValidationDataRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Initialize sample Drools template
            if (droolsTemplateRepository.findByValidationType("ORDER_PLACEMENT").isEmpty()) {
                String drlTemplate = "package rules;\n\n" +
                                     "import com.example.ordervalidation.entity.AccountValidationData;\n" +
                                     "import com.example.ordervalidation.dto.ValidationResult;\n\n" +
                                     "rule \"Minimum Age Check\"\n" +
                                     "when\n" +
                                     "    $customer : AccountValidationData(age < 18)\n" +
                                     "    $result : ValidationResult(valid == true)\n" +
                                     "then\n" +
                                     "    $result.setValid(false);\n" +
                                     "    $result.setMessage(\"Customer must be at least 18 years old\");\n" +
                                     "end\n\n" +
                                     "rule \"Name Check\"\n" +
                                     "when\n" +
                                     "    $customer : AccountValidationData(name == null || name.trim().isEmpty())\n" +
                                     "    $result : ValidationResult(valid == true)\n" +
                                     "then\n" +
                                     "    $result.setValid(false);\n" +
                                     "    $result.setMessage(\"Customer name is required\");\n" +
                                     "end";

                DroolsTemplate template = DroolsTemplate.builder()
                        .drlTemplate(drlTemplate)
                        .validationType("ORDER_PLACEMENT")
                        .description("Validates customer for order placement")
                        .build();

                droolsTemplateRepository.save(template);
            }

            // Initialize sample customer data
            if (accountValidationDataRepository.findByAccountId("CUST001").isEmpty()) {
                AccountValidationData customer = AccountValidationData.builder()
                        .accountId("CUST001")
                        .name("John Doe")
                        .age(25)
                        .dob(LocalDate.of(1998, 5, 15))
                        .build();

                accountValidationDataRepository.save(customer);
            }
        };
    }
}