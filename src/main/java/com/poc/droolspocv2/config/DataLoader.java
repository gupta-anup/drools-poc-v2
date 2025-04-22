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
                String drlTemplate =
                        "import com.poc.droolspocv2.dto.OrderRequest;\n" +
                        "import com.poc.droolspocv2.model.AccountValidationData;\n" +
                        "import com.poc.droolspocv2.dto.ValidationResult;\n\n" +

                        "// Rule to validate that the customer data in the request matches the stored data\n" +
                        "rule \"Customer Validation - Name Match\"\n" +
                        "when\n" +
                        "    $request : OrderRequest($requestName : name)\n" +
                        "    $stored : AccountValidationData(accountId == $request.accountId, name != $requestName)\n" +
                        "    $result : ValidationResult(valid == true)\n" +
                        "then\n" +
                        "    $result.setValid(false);\n" +
                        "    $result.setMessage(\"Customer name in request does not match our records\");\n" +
                        "end\n\n" +

                        "// Rule to validate that the customer age in the request matches the stored age\n" +
                        "rule \"Customer Validation - Age Match\"\n" +
                        "when\n" +
                        "    $request : OrderRequest($requestAge : age)\n" +
                        "    $stored : AccountValidationData(accountId == $request.accountId, age != $requestAge)\n" +
                        "    $result : ValidationResult(valid == true)\n" +
                        "then\n" +
                        "    $result.setValid(false);\n" +
                        "    $result.setMessage(\"Customer age in request does not match our records\");\n" +
                        "end\n\n" +

                        "// Rule to validate that the customer DOB in the request matches the stored DOB\n" +
                        "rule \"Customer Validation - DOB Match\"\n" +
                        "when\n" +
                        "    $request : OrderRequest($requestDob : dob)\n" +
                        "    $stored : AccountValidationData(accountId == $request.accountId, dob != $requestDob)\n" +
                        "    $result : ValidationResult(valid == true)\n" +
                        "then\n" +
                        "    $result.setValid(false);\n" +
                        "    $result.setMessage(\"Customer date of birth in request does not match our records\");\n" +
                        "end\n\n" +

                        "// Rule to validate customer's age is at least 18\n" +
                        "rule \"Customer Validation - Minimum Age\"\n" +
                        "when\n" +
                        "    $request : OrderRequest(age < 18)\n" +
                        "    $result : ValidationResult(valid == true)\n" +
                        "then\n" +
                        "    $result.setValid(false);\n" +
                        "    $result.setMessage(\"Customer must be at least 18 years old\");\n" +
                        "end";

                DroolsTemplate template = DroolsTemplate.builder()
                        .drlTemplate(drlTemplate)
                        .validationType("ORDER_PLACEMENT")
                        .description("Validates customer for order placement by comparing request data with stored data")
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