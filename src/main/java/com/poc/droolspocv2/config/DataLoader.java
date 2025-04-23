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
                        """
                                import com.poc.droolspocv2.dto.OrderRequest;
                                import com.poc.droolspocv2.model.AccountValidationData;
                                import com.poc.droolspocv2.dto.ValidationResult;
                                
                                // Rule to validate that the customer data in the request matches the stored data
                                rule "Customer Validation - Name Match"
                                when
                                    $request : OrderRequest($requestName : name)
                                    $stored : AccountValidationData(accountId == $request.accountId, name != $requestName)
                                    $result : ValidationResult(valid == true)
                                then
                                    $result.setValid(false);
                                    $result.setMessage("Customer name in request does not match our records");
                                end
                                
                                // Rule to validate that the customer age in the request matches the stored age
                                rule "Customer Validation - Age Match"
                                when
                                    $request : OrderRequest($requestAge : age)
                                    $stored : AccountValidationData(accountId == $request.accountId, age != $requestAge)
                                    $result : ValidationResult(valid == true)
                                then
                                    $result.setValid(false);
                                    $result.setMessage("Customer age in request does not match our records");
                                end
                                
                                // Rule to validate that the customer DOB in the request matches the stored DOB
                                rule "Customer Validation - DOB Match"
                                when
                                    $request : OrderRequest($requestDob : dob)
                                    $stored : AccountValidationData(accountId == $request.accountId, dob != $requestDob)
                                    $result : ValidationResult(valid == true)
                                then
                                    $result.setValid(false);
                                    $result.setMessage("Customer date of birth in request does not match our records");
                                end
                                
                                // Rule to validate customer's age is at least 18
                                rule "Customer Validation - Minimum Age"
                                when
                                    $request : OrderRequest(age < 18)
                                    $result : ValidationResult(valid == true)
                                then
                                    $result.setValid(false);
                                    $result.setMessage("Customer must be at least 18 years old");
                                end""";

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