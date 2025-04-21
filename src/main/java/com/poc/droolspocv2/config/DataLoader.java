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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final DroolsTemplateRepository droolsTemplateRepository;
    private final AccountValidationDataRepository accountValidationDataRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Check if data already exists
            if (droolsTemplateRepository.count() > 0 || accountValidationDataRepository.count() > 0) {
                log.info("Data already loaded, skipping initialization");
                return;
            }

            log.info("Loading sample data...");

            // Load template data
            loadTemplateData();

            // Load account data
            loadAccountData();

            log.info("Sample data loaded successfully!");
        };
    }

    private void loadTemplateData() {
        List<DroolsTemplate> templates = List.of(
                DroolsTemplate.builder()
                        .validationType("ONLY_NAME")
                        .description("Validates only the name field")
                        .drlTemplate("""
                    package com.poc.droolsvalidation;
                    
                    import com.poc.droolspocv2.model.AccountValidationData;
                    import com.poc.droolspocv2.model.ValidationResult;
                    
                    rule "Validate Name for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getName() == null || $account.getName().trim().isEmpty()) {
                            $result.addMessage("Name is required for account ${accountId}");
                        } else if (!$account.getName().equals("${name}")) {
                            $result.addMessage("Name should be ${name} but found " + $account.getName());
                        }
                    end
                    """)
                        .build(),

                DroolsTemplate.builder()
                        .validationType("NAME_AND_AGE")
                        .description("Validates both name and age fields")
                        .drlTemplate("""
                    package com.poc.droolsvalidation;
                    
                    import com.poc.droolspocv2.model.AccountValidationData;
                    import com.poc.droolspocv2.model.ValidationResult;
                    
                    rule "Validate Name for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getName() == null || $account.getName().trim().isEmpty()) {
                            $result.addMessage("Name is required for account ${accountId}");
                        } else if (!$account.getName().equals("${name}")) {
                            $result.addMessage("Name should be ${name} but found " + $account.getName());
                        }
                    end
                    
                    rule "Validate Age for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getAge() == null) {
                            $result.addMessage("Age is required for account ${accountId}");
                        } else if ($account.getAge() != ${age}) {
                            $result.addMessage("Age should be ${age} but found " + $account.getAge());
                        }
                    end
                    """)
                        .build(),

                DroolsTemplate.builder()
                        .validationType("ALL_FIELDS")
                        .description("Validates all fields: name, age, and DOB")
                        .drlTemplate("""
                    package com.poc.droolsvalidation;
                    
                    import com.poc.droolspocv2.model.AccountValidationData;
                    import com.poc.droolspocv2.model.ValidationResult;
                    import java.time.LocalDate;
                    
                    rule "Validate Name for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getName() == null || $account.getName().trim().isEmpty()) {
                            $result.addMessage("Name is required for account ${accountId}");
                        } else if (!$account.getName().equals("${name}")) {
                            $result.addMessage("Name should be ${name} but found " + $account.getName());
                        }
                    end
                    
                    rule "Validate Age for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getAge() == null) {
                            $result.addMessage("Age is required for account ${accountId}");
                        } else if ($account.getAge() != ${age}) {
                            $result.addMessage("Age should be ${age} but found " + $account.getAge());
                        }
                    end
                    
                    rule "Validate DOB for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getDob() == null) {
                            $result.addMessage("DOB is required for account ${accountId}");
                        } else if (!$account.getDob().toString().equals("${dob}")) {
                            $result.addMessage("DOB should be ${dob} but found " + $account.getDob());
                        }
                    end
                    """)
                        .build(),

                DroolsTemplate.builder()
                        .validationType("ONLY_DOB")
                        .description("Validates only the DOB field")
                        .drlTemplate("""
                    package com.poc.droolsvalidation;
                    
                    import com.poc.droolspocv2.model.AccountValidationData;
                    import com.poc.droolspocv2.model.ValidationResult;
                    import java.time.LocalDate;
                    
                    rule "Validate DOB for Account ${accountId}"
                    when
                        $account : AccountValidationData(accountId == "${accountId}")
                        $result : ValidationResult()
                    then
                        if ($account.getDob() == null) {
                            $result.addMessage("DOB is required for account ${accountId}");
                        } else if (!$account.getDob().toString().equals("${dob}")) {
                            $result.addMessage("DOB should be ${dob} but found " + $account.getDob());
                        }
                    end
                    """)
                        .build()
        );

        droolsTemplateRepository.saveAll(templates);
        log.info("Loaded {} template records", templates.size());
    }

    private void loadAccountData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<AccountValidationData> accounts = List.of(
                AccountValidationData.builder()
                        .accountId("303")
                        .age(18)
                        .name("JOSH MD")
                        .dob(LocalDate.parse("2006-01-01"))
                        .build(),

                AccountValidationData.builder()
                        .accountId("305")
                        .age(19)
                        .name("WILLS MD")
                        .dob(LocalDate.parse("2005-03-02"))
                        .build(),

                AccountValidationData.builder()
                        .accountId("306")
                        .age(20)
                        .name("Saurabh")
                        .dob(LocalDate.parse("1999-10-08"))
                        .build(),

                AccountValidationData.builder()
                        .accountId("308")
                        .age(22)
                        .name("Hein")
                        .dob(LocalDate.parse("2002-04-04"))
                        .build()
        );

        accountValidationDataRepository.saveAll(accounts);
        log.info("Loaded {} account records", accounts.size());
    }
}