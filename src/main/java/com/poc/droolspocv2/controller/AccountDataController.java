package com.poc.droolspocv2.controller;

import com.poc.droolspocv2.model.AccountValidationData;
import com.poc.droolspocv2.repository.AccountValidationDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountDataController {

    private final AccountValidationDataRepository accountValidationDataRepository;

    @GetMapping
    public ResponseEntity<List<AccountValidationData>> getAllAccounts() {
        return ResponseEntity.ok(accountValidationDataRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountValidationData> getAccountById(@PathVariable Long id) {
        return accountValidationDataRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/byAccountId/{accountId}")
    public ResponseEntity<AccountValidationData> getAccountByAccountId(@PathVariable String accountId) {
        return accountValidationDataRepository.findByAccountId(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountValidationData> createAccount(@RequestBody AccountValidationData account) {
        return ResponseEntity.ok(accountValidationDataRepository.save(account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountValidationData> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountValidationData account) {
        Optional<AccountValidationData> existingAccount = accountValidationDataRepository.findById(id);
        if (existingAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.setId(id);
        return ResponseEntity.ok(accountValidationDataRepository.save(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        if (!accountValidationDataRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        accountValidationDataRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}