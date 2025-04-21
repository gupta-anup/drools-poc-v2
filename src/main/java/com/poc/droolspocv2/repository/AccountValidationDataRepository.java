package com.poc.droolspocv2.repository;

import com.poc.droolspocv2.model.AccountValidationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountValidationDataRepository extends JpaRepository<AccountValidationData, Long> {
    Optional<AccountValidationData> findByAccountId(String accountId);
}